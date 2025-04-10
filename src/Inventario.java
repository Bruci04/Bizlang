
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
        try {
            FileWriter writer = new FileWriter("reporte_ventas.txt");
            writer.write("📜 Reporte de Ventas\n");
            for (String venta : ventas) {
                writer.write(venta + "\n");
            }
            writer.close();
            System.out.println("📄 Reporte generado correctamente.");
        } catch (IOException e) {
            System.err.println("❌ Error al generar el reporte.");
        }
    }
}

