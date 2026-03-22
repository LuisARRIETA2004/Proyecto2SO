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
        // Creamos la carpeta raíz del sistema
        this.raiz = new NodeFS("Raiz", "Administrador", null);
    }

    // Método CREAR ARCHIVO
    public void crearArchivo(String nombre, int tamano, String propietario, NodeFS carpetaDestino) {
        // 1. Pedimos espacio al disco
        int bloqueInicial = disco.asignarArchivo(nombre, tamano);
        
        if (bloqueInicial != -1) {
            // 2. Si hubo espacio, creamos el nodo y lo conectamos al árbol
            NodeFS nuevoArchivo = new NodeFS(nombre, propietario, tamano, bloqueInicial, carpetaDestino);
            carpetaDestino.agregarHijo(nuevoArchivo);
            System.out.println("Archivo " + nombre + " creado exitosamente.");
        } else {
            System.out.println("No se pudo crear el archivo: Disco lleno.");
        }
    }

    // Método ELIMINAR ARCHIVO (Lógica básica)
    public void eliminarArchivo(NodeFS carpetaPadre, String nombreArchivo) {
        NodeFS actual = carpetaPadre.getPrimerHijo();
        NodeFS anterior = null;

        while (actual != null) {
            if (actual.getNombre().equals(nombreArchivo)) {
                
                // 1. Liberamos los bloques en el disco físico
                if (!actual.isEsDirectorio()) {
                    disco.liberarArchivo(actual.getBloqueInicial());
                }

                // 2. Lo desenlazamos del árbol (saltamos este nodo)
                if (anterior == null) {
                    // Era el primer hijo
                    carpetaPadre.setPrimerHijo(actual.getSiguienteHermano());
                } else {
                    // Estaba en medio o al final
                    anterior.setSiguienteHermano(actual.getSiguienteHermano());
                }
                return; // Eliminación completada
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
