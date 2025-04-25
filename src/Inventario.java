
import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;

public class Inventario {
    private static Map<String, Integer> productos = new HashMap<>();
    private static List<String> ventas = new ArrayList<>();

    public static void agregarProducto(String nombre, int cantidad) {
        productos.put(nombre, productos.getOrDefault(nombre, 0) + cantidad);
        System.out.println("✅ Producto agregado: " + nombre + " (" + cantidad + " unidades)");
    }

    public static void venderProducto(String nombre, int precio) {
        if (productos.containsKey(nombre) && productos.get(nombre) > 0) {
            productos.put(nombre, productos.get(nombre) - 1);
            ventas.add(nombre + " - $" + precio);
            System.out.println("✅ Venta realizada: " + nombre + " por $" + precio);
        } else {
            System.out.println("❌ Producto no disponible.");
        }
    }

    public static void mostrarInventario() {
        System.out.println("📦 Inventario actual:");
        for (Map.Entry<String, Integer> entry : productos.entrySet()) {
            System.out.println("🛒 " + entry.getKey() + ": " + entry.getValue() + " unidades");
        }
    }

    public static void generReportePDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte de Ventas");
        int seleccion = fileChooser.showSaveDialog(null);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
                writer.write("📜 Reporte de Ventas\n");
                for (String venta : ventas) { // Asegúrate de que esta lista esté disponible y sea accesible
                    writer.write(venta + "\n");
                }

            } catch (IOException e) {
            System.err.println("❌ Error al generar el reporte.");
        }
        }
    }
}

