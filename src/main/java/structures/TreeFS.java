/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package structures;
import classes.Disk;

/**
 *
 * @author truenno
 */
public class TreeFS {
public class ArbolSistemaArchivos {
    private NodeFS raiz;
    private Disk disco; // Referencia al disco que programamos antes

    public ArbolSistemaArchivos(Disk disco) {
        this.disco = disco;
        this.raiz = new NodeFS("Raiz", "Administrador", null);
    }

    // Método CREAR ARCHIVO
    public void crearArchivo(String nombre, int tamano, String propietario, NodeFS carpetaDestino) {
        int bloqueInicial = disco.asignarArchivo(nombre, tamano);
        
        if (bloqueInicial != -1) {
            NodeFS nuevoArchivo = new NodeFS(nombre, propietario, tamano, bloqueInicial, carpetaDestino);
            carpetaDestino.agregarHijo(nuevoArchivo);
            System.out.println("Archivo " + nombre + " creado exitosamente.");
        } else {
            System.out.println("No se pudo crear el archivo: Disco lleno.");
        }
    }

    // Método ELIMINAR ARCHIVO 
    public void eliminarArchivo(NodeFS carpetaPadre, String nombreArchivo) {
        NodeFS actual = carpetaPadre.getPrimerHijo();
        NodeFS anterior = null;

        while (actual != null) {
            if (actual.getNombre().equals(nombreArchivo)) {
                if (!actual.isEsDirectorio()) {
                    disco.liberarArchivo(actual.getBloqueInicial());
                }
                if (anterior == null) {
                    carpetaPadre.setPrimerHijo(actual.getSiguienteHermano());
                } else {
                    anterior.setSiguienteHermano(actual.getSiguienteHermano());
                }
                return; 
            }
            anterior = actual;
            actual = actual.getSiguienteHermano();
        }
    }

    public NodeFS getRaiz() {
        return raiz;
        }
    }	
}
