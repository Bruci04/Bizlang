import java.io.*;
import java_cup.runtime.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Crear el lector de consola
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String input;

            // Ejecutar el ciclo para aceptar múltiples comandos
            while (true) {
                System.out.println("💻 Ingresa un comando en BizLang (o 'salir' para terminar):");
                input = br.readLine();

                // Verificar si el usuario quiere salir
                if (input.equalsIgnoreCase("salir")) {
                    System.out.println("👋 Saliendo del programa...");
                    break; // Salir del ciclo y terminar el programa
                }

                // Pasar la entrada al analizador léxico
                BizLangLexer lexer = new BizLangLexer(new StringReader(input));
                Parser parser = new Parser(lexer);

                // Ejecutar el análisis
                try {
                    parser.parse();
                    System.out.println("✅ Comando ejecutado correctamente.");
                } catch (Exception e) {
                    System.err.println("❌ Error al ejecutar BizLang: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Error al leer la entrada: " + e.getMessage());
        }
    }
}
