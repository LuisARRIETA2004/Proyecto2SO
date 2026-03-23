/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package structures;

import classes.PCB;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;


/**
 * Motor de simulación encargado de gestionar las colas de procesos,
 * los locks de archivos y el movimiento físico del cabezal del disco.
 */
public class ThreadScheduler extends Thread {

    // Colas del sistema
    private newCustomQueue colaListos;
    private newCustomQueue colaBloqueados;
    private newCustomQueue colaES; // Cola de espera del disco

    // Estado del disco
    private int posicionCabezal = 0;
    private int direccion = 1; // 1: Arriba, -1: Abajo (para SCAN)
    private String politicaActiva = "FIFO";

    // Referencias externas para lógica y visualización
    private TreeFS.ArbolSistemaArchivos arbol;
    private JTextArea txtJournal;
    
    private PCB procesoActual; // El proceso que posee el cabezal actualmente
    private boolean simulando;

    public ThreadScheduler(newCustomQueue listos, newCustomQueue bloqueados, newCustomQueue es, 
                           TreeFS.ArbolSistemaArchivos arbol, JTextArea journal) {
        this.colaListos = listos;
        this.colaBloqueados = bloqueados;
        this.colaES = es;
        this.arbol = arbol;
        this.txtJournal = journal;
        this.simulando = true;
        this.procesoActual = null;
    }

    @Override
    public void run() {
        System.out.println(">>> Motor del Sistema de Archivos Iniciado.");

        while (simulando) {
            // 1. Intentar despertar procesos bloqueados (si el archivo ya se liberó)
            revisarBloqueados();

            // 2. Procesar el siguiente en la cola de listos
            procesarListos();

            // 3. Mover el cabezal y completar la operación de disco
            ejecutarOperacionDisco();

            try {
                // Tiempo de espera para que la simulación sea visible (1.5 segundos)
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                System.err.println("Error en el hilo del planificador.");
            }
        }
    }

    /**
     * Revisa si los procesos que no pudieron obtener un lock antes, pueden obtenerlo ahora.
     */
    private void revisarBloqueados() {
        if (colaBloqueados.estaVacia()) return;

        int n = colaBloqueados.getSize();
        for (int i = 0; i < n; i++) {
            PCB p = colaBloqueados.desencolar();
            if (intentarAdquirirLock(p)) {
                p.setEstado("LISTO (E/S)");
                colaES.encolar(p);
                logJournal("LOCK OBTENIDO", "Proceso " + p.getId() + " accede a " + p.getArchivoObjetivo().getNombre());
            } else {
                colaBloqueados.encolar(p); // Sigue esperando
            }
        }
    }

    /**
     * Toma el primer proceso de la cola de listos e intenta pedir permiso (Lock) para operar.
     */
    private void procesarListos() {
        if (colaListos.estaVacia()) return;

        PCB p = colaListos.desencolar();
        if (intentarAdquirirLock(p)) {
            p.setEstado("EJECUTANDO (E/S)");
            colaES.encolar(p);
        } else {
            p.setEstado("BLOQUEADO (LOCK)");
            colaBloqueados.encolar(p);
            logJournal("LOCK DENEGADO", "Archivo " + p.getArchivoObjetivo().getNombre() + " en uso. Proceso " + p.getId() + " bloqueado.");
        }
    }

    /**
     * Lógica de Semáforos (Requerimiento de Concurrencia del PDF)
     */
    private boolean intentarAdquirirLock(PCB p) {
        if (p.getArchivoObjetivo() == null) return true;

        if (p.getOperacion().equals("LEER")) {
            return p.getArchivoObjetivo().adquirirLockLectura();
        } else {
            // ESCRIBIR, CREAR o ELIMINAR requieren lock exclusivo
            return p.getArchivoObjetivo().adquirirLockEscritura();
        }
    }

    /**
     * Mueve el cabezal según la política y finaliza la tarea (Journaling COMMIT).
     */
    private void ejecutarOperacionDisco() {
        if (colaES.estaVacia()) {
            procesoActual = null;
            return;
        }

        // Selección según política (Usa la lógica matemática de tu newCustomQueue)
        int[] refDireccion = {this.direccion};
        switch (politicaActiva) {
            case "FIFO":
                procesoActual = colaES.desencolar();
                break;
            case "SSTF":
                procesoActual = colaES.extraerSSTF(posicionCabezal);
                break;
            case "SCAN":
                procesoActual = colaES.extraerSCAN(posicionCabezal, refDireccion);
                this.direccion = refDireccion[0];
                break;
            case "C-SCAN":
                procesoActual = colaES.extraerCSCAN(posicionCabezal);
                break;
        }

        if (procesoActual != null) {
            // Simular movimiento físico
            posicionCabezal = procesoActual.getBloqueDestino();
            procesoActual.setEstado("TERMINADO");

            // REQUISITO 8: Journaling (COMMIT)
            logJournal("COMMIT", "Operación " + procesoActual.getOperacion() + " en ID:" + procesoActual.getId() + " finalizada.");

            // Liberar semáforos
            if (procesoActual.getArchivoObjetivo() != null) {
                if (procesoActual.getOperacion().equals("LEER")) {
                    procesoActual.getArchivoObjetivo().liberarLockLectura();
                } else {
                    procesoActual.getArchivoObjetivo().liberarLockEscritura();
                }
            }
        }
    }

    // --- Helpers y Getters ---

    private void logJournal(String tag, String msg) {
        if (txtJournal != null) {
            SwingUtilities.invokeLater(() -> {
                txtJournal.append("[" + tag + "] " + msg + "\n");
            });
        }
    }

    public int getPosicionCabezal() { return posicionCabezal; }
    public PCB getProcesoActual() { return procesoActual; }
    public void setPoliticaActiva(String politica) { this.politicaActiva = politica; }
    public void detener() { this.simulando = false; }
}