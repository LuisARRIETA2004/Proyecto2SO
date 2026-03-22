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
public class PCB {
    private static int contadorIds = 1; 
    private int id;
    private String estado; 
    private String operacion; 
    private NodeFS archivoObjetivo; 
    
    private PCB siguienteProceso; 

    public PCB(String operacion, NodeFS archivoObjetivo) {
        this.id = contadorIds++;
        this.estado = "NUEVO"; // "NUEVO", "LISTO", "EJECUTANDO", "BLOQUEADO", "TERMINADO"
        this.operacion = operacion; // "LEER", "ESCRIBIR", "CREAR", "ELIMINAR"
        this.archivoObjetivo = archivoObjetivo;
        this.siguienteProceso = null;
    }

	public static int getContadorIds() {
		return contadorIds;
	}

	public static void setContadorIds(int contadorIds) {
		PCB.contadorIds = contadorIds;
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

	public PCB getSiguienteProceso() {
		return siguienteProceso;
	}

	public void setSiguienteProceso(PCB siguienteProceso) {
		this.siguienteProceso = siguienteProceso;
	}

}
