/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package classes;
import structures.NodeFS;

public class PCB {
    private static int contadorIds = 1; 
    private int id;
    private String estado; 
    private String operacion; 
    private NodeFS archivoObjetivo; 
    private int bloqueDestino;
    private String nombreAux; 
    private int tamanoAux;   
    private PCB siguienteProceso; 

// Constructor de 5 parámetros (Necesario para el CRUD)
    public PCB(String operacion, NodeFS archivoObjetivo, int bloqueDestino, String nombreAux, int tamanoAux) {
        this.id = contadorIds++;
        this.estado = "NUEVO";
        this.operacion = operacion;
        this.archivoObjetivo = archivoObjetivo;
        this.bloqueDestino = bloqueDestino;
        this.nombreAux = nombreAux; // Asegúrate de tener este atributo arriba
        this.tamanoAux = tamanoAux; // Asegúrate de tener este atributo arriba
        this.siguienteProceso = null;
    }

    // Getters para los nuevos campos
    public String getNombreAux() { return nombreAux; }
    public int getTamanoAux() { return tamanoAux; }
    // ... mantén tus otros getters y setters ...
    public int getId() { return id; }
    public String getOperacion() { return operacion; }
    public NodeFS getArchivoObjetivo() { return archivoObjetivo; }
    public int getBloqueDestino() { return bloqueDestino; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public PCB getSiguienteProceso() { return siguienteProceso; }
    public void setSiguienteProceso(PCB sig) { this.siguienteProceso = sig; }
}