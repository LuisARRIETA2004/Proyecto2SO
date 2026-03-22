/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package structures;

/**
 *
 * @author truenno
 */
public class NodeFS {
	// 1. Metadatos generales

	private String nombre;
	private boolean esDirectorio;
	private String propietario; // Para saber si es del Administrador o Usuario

	// 2. Metadatos específicos de Archivos (Para el JTable / FAT)
	private int tamanoBloques;  // 0 si es directorio
	private int bloqueInicial;  // -1 si es directorio

	// 3. Punteros Estructurales (¡La magia para evitar ArrayList!)
	private NodeFS padre;             // Quién es la carpeta que lo contiene
	private NodeFS primerHijo;        // Si es carpeta, apunta a su primer elemento
	private NodeFS siguienteHermano;  // Apunta al siguiente elemento en la MISMA carpeta

	// Constructor para un DIRECTORIO
	public NodeFS(String nombre, String propietario, NodeFS padre) {
		this.nombre = nombre;
		this.esDirectorio = true;
		this.propietario = propietario;
		this.tamanoBloques = 0;
		this.bloqueInicial = -1;
		this.padre = padre;
		this.primerHijo = null;
		this.siguienteHermano = null;
	}

	// Constructor para un ARCHIVO
	public NodeFS(String nombre, String propietario, int tamanoBloques, int bloqueInicial, NodeFS padre) {
		this.nombre = nombre;
		this.esDirectorio = false;
		this.propietario = propietario;
		this.tamanoBloques = tamanoBloques;
		this.bloqueInicial = bloqueInicial;
		this.padre = padre;
		this.primerHijo = null; // Un archivo no tiene hijos
		this.siguienteHermano = null;
	}

	// Lógica para agregar un elemento dentro de este nodo (si es directorio)
	public void agregarHijo(NodeFS nuevoHijo) {
		if (!this.esDirectorio) {
			System.out.println("Error: No puedes agregar elementos a un archivo.");
			return;
		}

		if (this.primerHijo == null) {
			// Si la carpeta está vacía, este es el primer hijo
			this.primerHijo = nuevoHijo;
		} else {
			// Si ya tiene hijos, recorremos los "hermanos" hasta el último
			NodeFS actual = this.primerHijo;
			while (actual.siguienteHermano != null) {
				actual = actual.siguienteHermano;
			}
			// Lo enganchamos al final
			actual.siguienteHermano = nuevoHijo;
		}
	}

	// --- Agrega los Getters y Setters necesarios (getNombre(), isDirectorio(), etc.) ---
}
