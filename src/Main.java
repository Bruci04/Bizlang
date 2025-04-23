
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Main extends JFrame {
    private JTextArea inputArea;
    private JTextArea outputArea;
    private JButton ejecutarButton;

    public Main() {
        setTitle("💼 BizLang IDE");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Áreas de texto
        inputArea = new JTextArea(8, 60);
        inputArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        inputArea.setBorder(BorderFactory.createTitledBorder("📝 Código BizLang"));

        outputArea = new JTextArea(15, 60);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        outputArea.setEditable(false);
        outputArea.setBackground(Color.BLACK);
        outputArea.setForeground(Color.GREEN);
        outputArea.setBorder(BorderFactory.createTitledBorder("📤 Consola de salida"));

        // Botón Ejecutar
        ejecutarButton = new JButton("▶ Ejecutar");
        ejecutarButton.setFont(new Font("Arial", Font.BOLD, 14));
        ejecutarButton.setBackground(new Color(30, 144, 255));
        ejecutarButton.setForeground(Color.WHITE);
        ejecutarButton.addActionListener(e -> ejecutarCodigo());

        // Panel de entrada
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JScrollPane(inputArea), BorderLayout.CENTER);
        topPanel.add(ejecutarButton, BorderLayout.EAST);

        // Barra de menú
        JMenuBar menuBar = new JMenuBar();

        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem abrirItem = new JMenuItem("📂 Abrir...");
        JMenuItem guardarItem = new JMenuItem("💾 Guardar");

        abrirItem.addActionListener(e -> abrirArchivo());
        guardarItem.addActionListener(e -> guardarArchivo());

        menuArchivo.add(abrirItem);
        menuArchivo.add(guardarItem);

        JMenu menuEditar = new JMenu("Edición");
        JMenuItem limpiarEntradaItem = new JMenuItem("🗑 Limpiar Entrada");
        JMenuItem limpiarSalidaItem = new JMenuItem("🗑 Limpiar Consola");

        limpiarEntradaItem.addActionListener(e -> inputArea.setText(""));
        limpiarSalidaItem.addActionListener(e -> outputArea.setText(""));

        menuEditar.add(limpiarEntradaItem);
        menuEditar.add(limpiarSalidaItem);

        menuBar.add(menuArchivo);
        menuBar.add(menuEditar);
        setJMenuBar(menuBar);

        // Agregar al frame
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        redirectSystemStreams();
    }

    private void ejecutarCodigo() {
        outputArea.setText(""); // Limpiar consola
        String input = inputArea.getText();
        try {
            BizLangLexer lexer = new BizLangLexer(new StringReader(input));
            Parser parser = new Parser(lexer);
            parser.parse();
            System.out.println("✅ Comando ejecutado correctamente.");
        } catch (Exception ex) {
            System.err.println("❌ Error al ejecutar BizLang: " + ex.getMessage());
        }
    }

    private void abrirArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        int resultado = fileChooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
                inputArea.setText("");
                String linea;
                while ((linea = reader.readLine()) != null) {
                    inputArea.append(linea + "\n");
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "❌ No se pudo abrir el archivo.");
            }
        }
    }

    private void guardarArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        int resultado = fileChooser.showSaveDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
                writer.write(inputArea.getText());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "❌ No se pudo guardar el archivo.");
            }
        }
    }

    private void redirectSystemStreams() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) {
                outputArea.append(String.valueOf((char) b));
                outputArea.setCaretPosition(outputArea.getDocument().getLength());
            }

            @Override
            public void write(byte[] b, int off, int len) {
                outputArea.append(new String(b, off, len));
                outputArea.setCaretPosition(outputArea.getDocument().getLength());
            }
        };

        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
