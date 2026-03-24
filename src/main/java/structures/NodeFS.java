/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package structures;

import java.util.concurrent.Semaphore;

/**
 *
 * @author truenno
 */
public class NodeFS {

	private String nombre;
	private boolean esDirectorio;
	private String propietario; // Para saber si es del Administrador o Usuario
	private int tamanoBloques;
	private int bloqueInicial;
	private int lectoresActivos = 0;
	private Semaphore mutexLectores = new Semaphore(1);
	private Semaphore lockEscritura = new Semaphore(1);

	private NodeFS padre;
	private NodeFS primerHijo;
	private NodeFS siguienteHermano;

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

	public void agregarHijo(NodeFS nuevoHijo) {
		if (!this.esDirectorio) {
			System.out.println("Error: No puedes agregar elementos a un archivo.");
			return;
		}

		if (this.primerHijo == null) {
			this.primerHijo = nuevoHijo;
		} else {
			NodeFS actual = this.primerHijo;
			while (actual.siguienteHermano != null) {
				actual = actual.siguienteHermano;
			}
			actual.siguienteHermano = nuevoHijo;
		}
	}

	// Método para cuando un proceso quiere LEER
	public boolean adquirirLockLectura() {
		try {
			mutexLectores.acquire();
			lectoresActivos++;
			if (lectoresActivos == 1) {
				if (!lockEscritura.tryAcquire()) {
					lectoresActivos--;
					mutexLectores.release();
					return false;
				}
			}
			mutexLectores.release();
			return true;
		} catch (InterruptedException e) {
			return false;
		}
	}

	// Método para cuando un proceso quiere ESCRIBIR (Actualizar/Eliminar)
	public boolean adquirirLockEscritura() {
		return lockEscritura.tryAcquire();
	}

	// Liberar después de LEER
	public void liberarLockLectura() {
		try {
			mutexLectores.acquire();
			lectoresActivos--;
			if (lectoresActivos == 0) {
				lockEscritura.release();
			}
			mutexLectores.release();
		} catch (InterruptedException e) {
		}
	}

	// Liberar después de ESCRIBIR
	public void liberarLockEscritura() {
		lockEscritura.release();
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public boolean isEsDirectorio() {
		return esDirectorio;
	}

	public void setEsDirectorio(boolean esDirectorio) {
		this.esDirectorio = esDirectorio;
	}

	public String getPropietario() {
		return propietario;
	}

	public void setPropietario(String propietario) {
		this.propietario = propietario;
	}

	public int getTamanoBloques() {
		return tamanoBloques;
	}

	public void setTamanoBloques(int tamanoBloques) {
		this.tamanoBloques = tamanoBloques;
	}

	public int getBloqueInicial() {
		return bloqueInicial;
	}

	public void setBloqueInicial(int bloqueInicial) {
		this.bloqueInicial = bloqueInicial;
	}

	public NodeFS getPadre() {
		return padre;
	}

	public void setPadre(NodeFS padre) {
		this.padre = padre;
	}

	public NodeFS getPrimerHijo() {
		return primerHijo;
	}

	public void setPrimerHijo(NodeFS primerHijo) {
		this.primerHijo = primerHijo;
	}

	public NodeFS getSiguienteHermano() {
		return siguienteHermano;
	}

	public void setSiguienteHermano(NodeFS siguienteHermano) {
		this.siguienteHermano = siguienteHermano;
	}

    public int getLectoresActivos() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
