import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.*;

public class Main extends JFrame {
    private JTextArea inputArea;
    private JTextPane outputPane;
    private JButton ejecutarButton;
    private StyledDocument doc;
    private Style estiloNormal, estiloError;

    public Main() {
        setTitle("💼 BizLang IDE");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Icono de ventana
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/ui/resource/logo_lenguaje.jpg"));
            setIconImage(icon.getImage());
        } catch (Exception e) {
            System.err.println("❗ No se pudo cargar el logo.");
        }


        // Áreas de entrada y salida
        inputArea = new JTextArea(8, 60);
        inputArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        inputArea.setBorder(BorderFactory.createTitledBorder("📝 Código BizLang"));

        outputPane = new JTextPane();
        outputPane.setFont(new Font("Consolas", Font.PLAIN, 14));
        outputPane.setEditable(false);
        outputPane.setBackground(Color.BLACK);
        outputPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Consola de salida",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 13),
                Color.WHITE
        ));


        doc = outputPane.getStyledDocument();
        estiloNormal = doc.addStyle("normal", null);
        StyleConstants.setForeground(estiloNormal, Color.GREEN);
        estiloError = doc.addStyle("error", null);
        StyleConstants.setForeground(estiloError, Color.RED);

        // Boton Run
        ejecutarButton = new JButton("Run");
        ejecutarButton.setPreferredSize(new Dimension(100, 50));
        ejecutarButton.setBackground(new Color(0, 153, 76));
        ejecutarButton.setForeground(Color.WHITE);
        ejecutarButton.setFont(new Font("Arial", Font.BOLD, 20));
        ejecutarButton.setFocusPainted(false);
        ejecutarButton.setToolTipText("Ejecutar código BizLang");
        ejecutarButton.addActionListener(e -> ejecutarCodigo());

        // Panel superior
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JScrollPane(inputArea), BorderLayout.CENTER);
        topPanel.add(ejecutarButton, BorderLayout.EAST);

        // Menú
        JMenuBar menuBar = crearMenuBar();
        setJMenuBar(menuBar);

        // Agregar al frame
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(outputPane), BorderLayout.CENTER);

        redirectSystemStreams();
    }

    private void ejecutarCodigo() {
        outputPane.setText(""); // Limpiar consola
        String input = inputArea.getText();
        String[] lineas = input.split("\\n"); // Divido el texto en lineas independientes

        for (String linea : lineas) {
            linea = linea.trim();
            // Analiza linea por linea
            if (!linea.isEmpty()) {
                try {
                    BizLangLexer lexer = new BizLangLexer(new StringReader(linea));
                    Parser parser = new Parser(lexer);
                    parser.parse();
                    println(" Ejecutado: " + linea, estiloNormal);
                } catch (Exception ex) {
                    println(" Error en: " + linea, estiloError);
                    println("   ↳ " + ex.getMessage(), estiloError);
                }
            }
        }
    }

    private JMenuBar crearMenuBar() {
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
        limpiarSalidaItem.addActionListener(e -> outputPane.setText(""));
        menuEditar.add(limpiarEntradaItem);
        menuEditar.add(limpiarSalidaItem);

        menuBar.add(menuArchivo);
        menuBar.add(menuEditar);
        return menuBar;
    }

    private void abrirArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))) {
                inputArea.setText("");
                String line;
                while ((line = reader.readLine()) != null) {
                    inputArea.append(line + "\n");
                }
            } catch (IOException e) {
                println("No se pudo abrir el archivo.", estiloError);
            }
        }
    }

    private void guardarArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileChooser.getSelectedFile()))) {
                writer.write(inputArea.getText());
            } catch (IOException e) {
                println("No se pudo guardar el archivo.", estiloError);
            }
        }
    }
 // Clases de funcionamiento
    private void redirectSystemStreams() {
        PrintStream printStream = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
                appendToPane(String.valueOf((char) b), estiloNormal);
            }

            @Override
            public void write(byte[] b, int off, int len) {
                appendToPane(new String(b, off, len), estiloNormal);
            }
        });

        PrintStream errorStream = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
                appendToPane(String.valueOf((char) b), estiloError);
            }

            @Override
            public void write(byte[] b, int off, int len) {
                appendToPane(new String(b, off, len), estiloError);
            }
        });

        System.setOut(printStream);
        System.setErr(errorStream);
    }

    private void println(String msg, Style style) {
        try {
            doc.insertString(doc.getLength(), msg + "\n", style);
        } catch (Exception ignored) {
        }
    }

    private void appendToPane(String text, Style style) {
        try {
            doc.insertString(doc.getLength(), text, style);
            outputPane.setCaretPosition(doc.getLength());
        } catch (Exception ignored) {
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}