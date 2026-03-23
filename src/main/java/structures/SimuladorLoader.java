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
            String seccionActual = "";

            while (sc.hasNextLine()) {
                String linea = sc.nextLine().trim();
                
                // Saltar líneas vacías o comentarios
                if (linea.isEmpty() || linea.startsWith("#")) continue;

                // 1. Detectar Cabezal
                if (linea.startsWith("HEAD:")) {
                    int pos = Integer.parseInt(linea.split(":")[1].trim());
                    scheduler.setPosicionCabezal(pos);
                    continue;
                }

                // 2. Detectar cambios de sección
                if (linea.equals("FILES:")) {
                    seccionActual = "FILES";
                    continue;
                }
                if (linea.equals("REQUESTS:")) {
                    seccionActual = "REQUESTS";
                    continue;
                }

                // 3. Procesar datos según la sección
                String[] datos = linea.split(",");

                if (seccionActual.equals("FILES")) {
                    // Formato: nombre, inicio, bloques
                    String nombre = datos[0].trim();
                    int inicio = Integer.parseInt(datos[1].trim());
                    int numBloques = Integer.parseInt(datos[2].trim());
                    
                    // Ocupamos el disco físicamente en esa posición exacta
                    disco.asignarArchivoEnPosicion(nombre, inicio, numBloques);
                    
                    // Agregamos al árbol
                    NodeFS nuevo = new NodeFS(nombre, "Sistema", numBloques, inicio, arbol.getRaiz());
                    arbol.getRaiz().agregarHijo(nuevo);
                    
                } else if (seccionActual.equals("REQUESTS")) {
                    // Formato: operacion, bloque
                    String op = datos[0].trim();
                    int bloqueDestino = Integer.parseInt(datos[1].trim());

                    // Creamos el PCB (ticket) y lo mandamos a la cola
                    PCB p = new PCB(op, null, bloqueDestino, "Externo_" + bloqueDestino, 1);
                    colaListos.encolar(p);
                }
            }
            System.out.println(">>> Escenario de texto cargado con éxito.");

        } catch (Exception e) {
            System.err.println("Error procesando archivo de prueba: " + e.getMessage());
        }
    }
}