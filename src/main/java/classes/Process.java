/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package classes;
import structures.NodeFS;

/**
 *
 * @author truenno
 */
public class Process {
    private static int contadorIds = 1; 
    private int id;
    private String estado; 
    private String operacion; 
    private NodeFS archivoObjetivo; 
    
    private Process siguienteProceso; 

    public Process(String operacion, NodeFS archivoObjetivo) {
        this.id = contadorIds++;
        this.estado = "NUEVO";
        this.operacion = operacion;
        this.archivoObjetivo = archivoObjetivo;
        this.siguienteProceso = null;
    }

	public static int getContadorIds() {
		return contadorIds;
	}

	public static void setContadorIds(int contadorIds) {
		Process.contadorIds = contadorIds;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getOperacion() {
		return operacion;
	}

	public void setOperacion(String operacion) {
		this.operacion = operacion;
	}

	public NodeFS getArchivoObjetivo() {
		return archivoObjetivo;
	}

	public void setArchivoObjetivo(NodeFS archivoObjetivo) {
		this.archivoObjetivo = archivoObjetivo;
	}

	public Process getSiguienteProceso() {
		return siguienteProceso;
	}

	public void setSiguienteProceso(Process siguienteProceso) {
		this.siguienteProceso = siguienteProceso;
	}

}
