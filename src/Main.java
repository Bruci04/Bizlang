import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.*;

public class Main extends JFrame {
    private JTextArea inputArea;
    private JTextPane outputPane;
    private JButton ejecutarButton;
    private StyledDocument doc;
    private Style estiloNormal, estiloError;

    public Main() {
        setTitle("💼 BizLang Chat IDE");
        setSize(750, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(244, 244, 248));

        // Margen general para la aplicación
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/ui/resource/logo_lenguaje.jpg"));
            setIconImage(icon.getImage());
        } catch (Exception e) {
            System.err.println("❗ No se pudo cargar el logo.");
        }

        // PANE DE SALIDA
        outputPane = new JTextPane();
        outputPane.setFont(new Font("Consolas", Font.PLAIN, 14));
        outputPane.setEditable(false);
        outputPane.setBackground(new Color(33, 36, 45));
        outputPane.setForeground(Color.WHITE);
        outputPane.setBorder(new EmptyBorder(15, 15, 15, 15));

        doc = outputPane.getStyledDocument();
        estiloNormal = doc.addStyle("normal", null);
        StyleConstants.setForeground(estiloNormal, new Color(0, 255, 128));
        StyleConstants.setFontSize(estiloNormal, 14);
        StyleConstants.setLeftIndent(estiloNormal, 10);
        StyleConstants.setSpaceAbove(estiloNormal, 10);
        StyleConstants.setSpaceBelow(estiloNormal, 10);

        estiloError = doc.addStyle("error", null);
        StyleConstants.setForeground(estiloError, Color.RED);
        StyleConstants.setFontSize(estiloError, 14);
        StyleConstants.setLeftIndent(estiloError, 10);
        StyleConstants.setSpaceAbove(estiloError, 10);
        StyleConstants.setSpaceBelow(estiloError, 10);

        JScrollPane outputScroll = new JScrollPane(outputPane);
        outputScroll.setBorder(BorderFactory.createEmptyBorder());
        add(outputScroll, BorderLayout.CENTER); // 🟢 ARRIBA

        // PANEL DE ENTRADA ABAJO
        inputArea = new JTextArea(5, 60);
        inputArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setBackground(new Color(232, 240, 255));
        inputArea.setForeground(Color.DARK_GRAY);
        inputArea.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), new RoundedBorder(20)));

        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setBorder(BorderFactory.createEmptyBorder());

        // Botón circular con flecha - ahora con tamaño fijo que no se distorsiona
        ejecutarButton = new JButton("▶");
        ejecutarButton.setPreferredSize(new Dimension(60, 60)); // Tamaño fijo
        ejecutarButton.setMinimumSize(new Dimension(60, 60)); // Evita que se haga más pequeño
        ejecutarButton.setMaximumSize(new Dimension(60, 60)); // Evita que se haga más grande
        ejecutarButton.setBackground(new Color(0, 153, 255));
        ejecutarButton.setForeground(Color.WHITE);
        ejecutarButton.setFont(new Font("Segoe UI Symbol", Font.BOLD, 22));
        ejecutarButton.setFocusPainted(false);
        ejecutarButton.setBorder(new CircleBorder());
        ejecutarButton.setContentAreaFilled(true);
        ejecutarButton.addActionListener(e -> {
            ejecutarCodigo();
            inputArea.setText(""); // Limpia el cuadro de texto después de ejecutar
        });

        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBackground(getContentPane().getBackground());
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Más márgenes
        inputPanel.add(inputScroll, BorderLayout.CENTER);

        // Panel para el botón para mantener su posición y tamaño
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(getContentPane().getBackground());
        buttonPanel.setBorder(new EmptyBorder(0, 10, 0, 0));
        buttonPanel.add(ejecutarButton);

        inputPanel.add(buttonPanel, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH); // 🔵 ABAJO

        setJMenuBar(crearMenuBar());
        redirectSystemStreams();
    }

    private JMenuBar crearMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu archivo = new JMenu("Archivo");
        JMenuItem abrir = new JMenuItem("📂 Abrir...");
        JMenuItem guardar = new JMenuItem("💾 Guardar");
        abrir.addActionListener(e -> abrirArchivo());
        guardar.addActionListener(e -> guardarArchivo());
        archivo.add(abrir);
        archivo.add(guardar);

        JMenu edicion = new JMenu("Edición");
        JMenuItem limpiarIn = new JMenuItem("🗑 Limpiar Entrada");
        JMenuItem limpiarOut = new JMenuItem("🗑 Limpiar Consola");
        limpiarIn.addActionListener(e -> inputArea.setText(""));
        limpiarOut.addActionListener(e -> outputPane.setText(""));
        edicion.add(limpiarIn);
        edicion.add(limpiarOut);

        menuBar.add(archivo);
        menuBar.add(edicion);
        return menuBar;
    }

    private void ejecutarCodigo() {
        outputPane.setText("");
        String[] lineas = inputArea.getText().split("\\n");
        for (String linea : lineas) {
            linea = linea.trim();
            if (!linea.isEmpty()) {
                try {
                    BizLangLexer lexer = new BizLangLexer(new StringReader(linea));
                    Parser parser = new Parser(lexer);
                    parser.parse();
                    println("🟢 Ejecutado: " + linea, estiloNormal);
                } catch (Exception ex) {
                    println("🔴 Error en: " + linea, estiloError);
                    println("   ↳ " + ex.getMessage(), estiloError);
                }
            }
        }
    }

    private void abrirArchivo() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader reader = new BufferedReader(new FileReader(chooser.getSelectedFile()))) {
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
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(chooser.getSelectedFile()))) {
                writer.write(inputArea.getText());
            } catch (IOException e) {
                println("No se pudo guardar el archivo.", estiloError);
            }
        }
    }

    private void redirectSystemStreams() {
        PrintStream ps = new PrintStream(new OutputStream() {
            @Override public void write(int b) {
                appendToPane(String.valueOf((char) b), estiloNormal);
            }

            @Override public void write(byte[] b, int off, int len) {
                appendToPane(new String(b, off, len), estiloNormal);
            }
        });

        PrintStream err = new PrintStream(new OutputStream() {
            @Override public void write(int b) {
                appendToPane(String.valueOf((char) b), estiloError);
            }

            @Override public void write(byte[] b, int off, int len) {
                appendToPane(new String(b, off, len), estiloError);
            }
        });

        System.setOut(ps);
        System.setErr(err);
    }

    private void println(String msg, Style style) {
        try {
            doc.insertString(doc.getLength(), msg + "\n", style);
        } catch (BadLocationException ignored) {}
    }

    private void appendToPane(String text, Style style) {
        try {
            doc.insertString(doc.getLength(), text, style);
            outputPane.setCaretPosition(doc.getLength());
        } catch (BadLocationException ignored) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }

    // Borde redondeado
    static class RoundedBorder implements Border {
        private final int radius;

        public RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius + 1, radius + 1, radius + 1, radius + 1);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.GRAY);
            g2.setStroke(new BasicStroke(1));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }

    // Borde circular para el botón de ejecutar
    static class CircleBorder implements Border {
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(5, 5, 5, 5);
        }

        @Override
        public boolean isBorderOpaque() {
            return true;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(Color.DARK_GRAY);
            g.drawOval(x, y, width - 1, height - 1);
        }
    }
}