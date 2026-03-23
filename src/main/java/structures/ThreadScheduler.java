/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package structures;

import classes.PCB;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class ThreadScheduler extends Thread {
    private newCustomQueue colaListos;
    private newCustomQueue colaBloqueados;
    private newCustomQueue colaES;
    private int posicionCabezal = 0;
    private int direccion = 1;
    private String politicaActiva = "FIFO";
    private TreeFS.ArbolSistemaArchivos arbol;
    private JTextArea txtJournal;
    private PCB procesoActual;
    private boolean simulando;

    public ThreadScheduler(newCustomQueue listos, newCustomQueue bloqueados, newCustomQueue es, 
                           TreeFS.ArbolSistemaArchivos arbol, JTextArea journal) {
        this.colaListos = listos;
        this.colaBloqueados = bloqueados;
        this.colaES = es;
        this.arbol = arbol;
        this.txtJournal = journal;
        this.simulando = true;
    }

    @Override
    public void run() {
        while (simulando) {
            revisarBloqueados();
            procesarListos();
            ejecutarOperacionDisco();
            try { Thread.sleep(1500); } catch (InterruptedException e) {
                System.out.println("Scheduler en pausa por fallo.");
            }
        }
    }

    private void revisarBloqueados() {
        if (colaBloqueados.estaVacia()) return;
        int n = colaBloqueados.getSize();
        for (int i = 0; i < n; i++) {
            PCB p = colaBloqueados.desencolar();
            if (intentarAdquirirLock(p)) {
                p.setEstado("LISTO (E/S)");
                colaES.encolar(p);
            } else {
                colaBloqueados.encolar(p);
            }
        }
    }

    private void procesarListos() {
        if (colaListos.estaVacia()) return;
        PCB p = colaListos.desencolar();
        if (intentarAdquirirLock(p)) {
            p.setEstado("EJECUTANDO (E/S)");
            colaES.encolar(p);
        } else {
            p.setEstado("BLOQUEADO (LOCK)");
            colaBloqueados.encolar(p);
        }
    }

    private boolean intentarAdquirirLock(PCB p) {
        if (p.getArchivoObjetivo() == null) return true;
        return p.getOperacion().equals("LEER") ? 
               p.getArchivoObjetivo().adquirirLockLectura() : 
               p.getArchivoObjetivo().adquirirLockEscritura();
    }

    private void ejecutarOperacionDisco() {
        if (colaES.estaVacia()) { procesoActual = null; return; }
        
        int[] refDir = {this.direccion};
        switch (politicaActiva) {
            case "SSTF": procesoActual = colaES.extraerSSTF(posicionCabezal); break;
            case "SCAN": procesoActual = colaES.extraerSCAN(posicionCabezal, refDir); this.direccion = refDir[0]; break;
            case "C-SCAN": procesoActual = colaES.extraerCSCAN(posicionCabezal); break;
            default: procesoActual = colaES.desencolar();
        }

        if (procesoActual != null) {
            posicionCabezal = procesoActual.getBloqueDestino();
            procesoActual.setEstado("TERMINADO");
            
            String op = procesoActual.getOperacion();
            String nom = procesoActual.getNombreAux();

            // EJECUCIÓN REAL DE LA OPERACIÓN
            switch(op) {
                case "CREAR":
                    arbol.crearArchivo(nom, procesoActual.getTamanoAux(), "Admin", arbol.getRaiz());
                    break;
                case "CREAR_DIR":
                    arbol.getRaiz().agregarHijo(new NodeFS(nom, "Admin", arbol.getRaiz()));
                    break;
                case "ELIMINAR":
                    arbol.eliminarArchivo(arbol.getRaiz(), nom);
                    break;
                case "LEER":
                    System.out.println("Lectura completada: " + nom);
                    break;
            }
            
            registrarEnGUI("CONFIRMADA", op + " de " + nom + " exitosa.");
            
            // Liberar Locks
            if (procesoActual.getArchivoObjetivo() != null) {
                if (op.equals("LEER")) procesoActual.getArchivoObjetivo().liberarLockLectura();
                else procesoActual.getArchivoObjetivo().liberarLockEscritura();
            }
            procesoActual = null;
        }
    }

    private void registrarEnGUI(String tag, String mensaje) {
        if (txtJournal != null) SwingUtilities.invokeLater(() -> txtJournal.append("[" + tag + "] " + mensaje + "\n"));
    }

    // Getters y Setters
    public int getPosicionCabezal() { return posicionCabezal; }
    public PCB getProcesoActual() { return procesoActual; }
    public void setPoliticaActiva(String pol) { this.politicaActiva = pol; }
    public void setPosicionCabezal(int pos) { this.posicionCabezal = pos; }
    public void setSimulando(boolean s) { this.simulando = s; }
}