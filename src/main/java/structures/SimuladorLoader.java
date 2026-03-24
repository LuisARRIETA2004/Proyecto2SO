/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package structures;

import classes.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;
import classes.*;

public class SimuladorLoader {

    public static void cargarEscenarioJSON(File archivo, Disk disco, TreeFS.ArbolSistemaArchivos arbol, newCustomQueue colaListos, ThreadScheduler scheduler) {
        try {
            String contenido = new String(Files.readAllBytes(Paths.get(archivo.getAbsolutePath())));
            JSONObject json = new JSONObject(contenido);
            
            if (json.has("initial_head")) {
                int head = json.getInt("initial_head");
                scheduler.setPosicionCabezal(head);
            }
            
            if (json.has("system_files")) {
                JSONObject systemFiles = json.getJSONObject("system_files");
                
                Iterator<String> llaves = systemFiles.keys();
                while (llaves.hasNext()) {
                    String posStr = llaves.next();
                    int bloqueInicio = Integer.parseInt(posStr);
                    
                    JSONObject archivoObj = systemFiles.getJSONObject(posStr);
                    String nombre = archivoObj.getString("name");
                    int numBloques = archivoObj.getInt("blocks");
                    
                    disco.asignarArchivoEnPosicion(nombre, bloqueInicio, numBloques);
                    
                    NodeFS nuevoNodo = new NodeFS(nombre, "Sistema", numBloques, bloqueInicio, arbol.getRaiz());
                    arbol.getRaiz().agregarHijo(nuevoNodo);
                }
            }
            
            if (json.has("requests")) {
                JSONArray requests = json.getJSONArray("requests");
                for (int i = 0; i < requests.length(); i++) {
                    JSONObject req = requests.getJSONObject(i);
                    int pos = req.getInt("pos");
                    String opJSON = req.getString("op");
                    
                    String opTraducida = opJSON;
                    if (opJSON.equals("READ")) opTraducida = "LEER";
                    else if (opJSON.equals("UPDATE")) opTraducida = "ACTUALIZAR";
                    else if (opJSON.equals("DELETE")) opTraducida = "ELIMINAR";
                    else if (opJSON.equals("CREATE")) opTraducida = "CREAR";

                    PCB proceso = new PCB(opTraducida, null, pos, "Req_" + opTraducida + "_" + pos, 0);
                    colaListos.encolar(proceso);
                }
            }
            
            System.out.println(">>> Escenario JSON cargado con éxito.");

        } catch (Exception e) {
            System.err.println("Error procesando archivo JSON: " + e.getMessage());
        }
    }
}