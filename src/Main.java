import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.util.*;

public class Main extends JFrame {
    private JTextArea inputArea;
    private JButton ejecutarButton;
    private JPanel chatPanel;
    private JScrollPane chatScrollPane;

    public Main() {
        setTitle("💬 BizLang Chat IDE");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Icono
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/ui/resource/logo_lenguaje.jpg"));
            setIconImage(icon.getImage());
        } catch (Exception ignored) {}

        // Panel de chat
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(Color.WHITE);

        chatScrollPane = new JScrollPane(chatPanel);
        chatScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScrollPane.setBorder(null);

        // Área de entrada
        inputArea = new JTextArea(3, 40);
        inputArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Botón ejecutar
        ejecutarButton = new JButton("▶");
        ejecutarButton.setFont(new Font("Arial", Font.BOLD, 16));
        ejecutarButton.setPreferredSize(new Dimension(50, 50));
        ejecutarButton.setBackground(new Color(0, 153, 255)); // Azul fuerte
        ejecutarButton.setForeground(Color.WHITE);
        ejecutarButton.setFocusPainted(false);
        ejecutarButton.setBorder(new RoundedBorder(20));
        ejecutarButton.addActionListener(e -> ejecutarCodigo());

        // Panel inferior
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        bottomPanel.add(new JScrollPane(inputArea), BorderLayout.CENTER);
        bottomPanel.add(ejecutarButton, BorderLayout.EAST);

        // Menú
        JMenuBar menuBar = crearMenuBar();
        setJMenuBar(menuBar);

        // Estructura principal
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(chatScrollPane, BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
    }

    private void ejecutarCodigo() {
        String input = inputArea.getText().trim();
        if (input.isEmpty()) return;

        // Mostrar input como mensaje de usuario
        addChatBubble(input, true, new Color(0, 153, 255), Color.WHITE); // azul claro
        inputArea.setText("");

        // Ejecutar línea por línea
        String[] lineas = input.split("\n");
        for (String linea : lineas) {
            try {
                BizLangLexer lexer = new BizLangLexer(new StringReader(linea));
                Parser parser = new Parser(lexer);
                parser.parse();
                addChatBubble("✅ Ejecutado: " + linea, false, new Color(220, 248, 198), Color.BLACK); // celeste-verde
            } catch (Exception ex) {
                addChatBubble("❌ Error en: " + linea + "\n↳ " + ex.getMessage(), false, new Color(255, 224, 178), Color.BLACK); // naranja claro
            }
        }
    }

    private void addChatBubble(String text, boolean isUser, Color bgColor, Color textColor) {
        JPanel bubble = new JPanel();
        bubble.setLayout(new BorderLayout());
        bubble.setBorder(new EmptyBorder(5, 10, 5, 10));

        JTextArea msg = new JTextArea(text);
        msg.setLineWrap(true);
        msg.setWrapStyleWord(true);
        msg.setEditable(false);
        msg.setFont(new Font("SansSerif", Font.PLAIN, 14));
        msg.setBackground(bgColor);
        msg.setForeground(textColor);
        msg.setBorder(new EmptyBorder(10, 15, 10, 15));
        msg.setOpaque(true);

        // Redondear burbuja
        msg.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(20),
                new EmptyBorder(10, 10, 10, 10)
        ));

        bubble.add(msg, isUser ? BorderLayout.EAST : BorderLayout.WEST);

        chatPanel.add(bubble);
        chatPanel.revalidate();

        // Scroll automático al final
        SwingUtilities.invokeLater(() ->
                chatScrollPane.getVerticalScrollBar().setValue(chatScrollPane.getVerticalScrollBar().getMaximum()));
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
        JMenuItem limpiarChat = new JMenuItem("🗑 Limpiar Chat");
        limpiarChat.addActionListener(e -> chatPanel.removeAll());
        menuEditar.add(limpiarChat);

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
                addChatBubble("⚠️ No se pudo abrir el archivo.", false, Color.PINK, Color.BLACK);
            }
        }
    }

    private void guardarArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileChooser.getSelectedFile()))) {
                writer.write(inputArea.getText());
            } catch (IOException e) {
                addChatBubble("⚠️ No se pudo guardar el archivo.", false, Color.PINK, Color.BLACK);
            }
        }
    }

    // Borde redondeado para componentes
    static class RoundedBorder extends AbstractBorder {
        private final int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c.getBackground());
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius + 1, this.radius + 1, this.radius + 1, this.radius + 1);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = insets.top = insets.bottom = radius + 1;
            return insets;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
