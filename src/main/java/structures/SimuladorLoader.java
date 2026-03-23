/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package structures;

import java.io.*;
import java.util.Scanner;
import classes.*;

public class SimuladorLoader {

    public static void cargarEscenario(File archivo, Disk disco, TreeFS.ArbolSistemaArchivos arbol, newCustomQueue colaListos, ThreadScheduler scheduler) {
        try (Scanner sc = new Scanner(archivo)) {
            StringBuilder contenidoBuilder = new StringBuilder();
            while (sc.hasNextLine()) {
                contenidoBuilder.append(sc.nextLine());
            }
            String contenido = contenidoBuilder.toString();

            // 1. Extraer initial_head
            String headVal = extraerValor(contenido, "initial_head");
            if (!headVal.isEmpty()) {
                scheduler.setPosicionCabezal(Integer.parseInt(headVal));
            }

            // 2. Extraer System Files (Archivos base)
            if (contenido.contains("\"system_files\":")) {
                String seccionFiles = contenido.split("\"system_files\":")[1].split("\\}")[0];
                String[] bloques = seccionFiles.split("\"\\d+\":"); // Divide por las llaves "11":, "34":, etc.

                for (String b : bloques) {
                    if (b.contains("name")) {
                        String nombre = extraerValor(b, "name");
                        int tam = Integer.parseInt(extraerValor(b, "blocks"));
                        // Buscar el ID del bloque manualmente en el texto original antes de este bloque
                        // Para simplificar, buscaremos un espacio libre
                        int inicio = buscarPrimerBloqueLibre(disco);
                        
                        disco.asignarArchivo(nombre, tam); 
                        arbol.getRaiz().agregarHijo(new NodeFS(nombre, "Sistema", tam, inicio, arbol.getRaiz()));
                    }
                }
            }

            // 3. Extraer Requests (Peticiones)
            if (contenido.contains("\"requests\":")) {
                String seccionReq = contenido.split("\"requests\":")[1].split("\\]")[0];
                String[] reqs = seccionReq.split("\\{");

                for (String r : reqs) {
                    if (r.contains("pos")) {
                        int pos = Integer.parseInt(extraerValor(r, "pos"));
                        String op = extraerValor(r, "op");

                        PCB p = new PCB(op, null, pos, "P_JSON_" + pos, 1);
                        colaListos.encolar(p);
                    }
                }
            }

            System.out.println(">>> Escenario JSON cargado con éxito.");

        } catch (Exception e) {
            System.err.println("Error procesando el archivo: " + e.getMessage());
        }
    }

    private static String extraerValor(String texto, String llave) {
        try {
            String[] partes = texto.split("\"" + llave + "\":");
            if (partes.length < 2) return "";
            String valor = partes[1].split("[,\\}\\]]")[0].replace("\"", "").trim();
            return valor;
        } catch (Exception e) { return ""; }
    }
    
    private static int buscarPrimerBloqueLibre(Disk disco) {
        for (int i = 0; i < disco.getBloques().length; i++) {
            if (!disco.getBloques()[i].isOcupado()) return i;
        }
        return 0;
    }
}
