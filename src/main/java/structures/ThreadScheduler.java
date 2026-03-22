/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package structures;
import javax.swing.SwingUtilities;
import classes.PCB;

/**
 *
 * @author truenno
 */

public class ThreadScheduler extends Thread {
    // Referencias a tus colas (estructuras propias)
    private newCustomQueue colaListos;
    private newCustomQueue colaBloqueados;
    private newCustomQueue colaES; 
    
    // Bandera para encender/apagar el simulador
    private boolean simulando;

    // Constructor limpio, solo recibe las estructuras de datos
    public ThreadScheduler(newCustomQueue listos, newCustomQueue bloqueados, newCustomQueue es) {
        this.colaListos = listos;
        this.colaBloqueados = bloqueados;
        this.colaES = es;
        this.simulando = true;
    }

    @Override
    public void run() {
        System.out.println("Planificador iniciado...");
        
        while (simulando) {
            // 1. Dar oportunidad a los procesos bloqueados
            revisarBloqueados();
            
            // 2. Revisar los procesos nuevos que acaban de llegar
            procesarListos();
            
            // 3. Atender las operaciones en el disco (FIFO, SSTF, etc.)
            ejecutarOperacionDisco();
            
            // 4. Pausa de simulación (1.5 segundos) para dar un ritmo realista
            try {
                Thread.sleep(1500); 
            } catch (InterruptedException e) {
                System.out.println("Planificador interrumpido.");
            }
        }
        System.out.println("Planificador detenido.");
    }

    private void revisarBloqueados() {
        if (colaBloqueados.estaVacia()) return;

        int cantidad = colaBloqueados.getSize(); 
        
        // Solo revisamos los que estaban al inicio de este ciclo para evitar loops infinitos
        for (int i = 0; i < cantidad; i++) {
            PCB p = colaBloqueados.desencolar();
            boolean lockObtenido = intentarLock(p);

            if (lockObtenido) {
                p.setEstado("EJECUTANDO (E/S)");
                colaES.encolar(p);
                System.out.println("--> Proceso " + p.getId() + " DESBLOQUEADO. Pasó a la Cola de E/S.");
            } else {
                colaBloqueados.encolar(p); // Vuelve al final de la cola
            }
        }
    }

    private void procesarListos() {
        if (!colaListos.estaVacia()) {
            PCB p = colaListos.desencolar();
            boolean lockObtenido = intentarLock(p);

            if (lockObtenido) {
                p.setEstado("EJECUTANDO (E/S)");
                colaES.encolar(p);
                System.out.println("--> Proceso " + p.getId() + " obtuvo LOCK de " + p.getArchivoObjetivo().getNombre() + ". Pasó a E/S.");
            } else {
                p.setEstado("BLOQUEADO");
                colaBloqueados.encolar(p);
                System.out.println("--> Proceso " + p.getId() + " BLOQUEADO. El archivo " + p.getArchivoObjetivo().getNombre() + " está en uso.");
            }
        }
    }

    private boolean intentarLock(PCB p) {
        if (p.getOperacion().equals("LEER")) {
            return p.getArchivoObjetivo().adquirirLockLectura();
        } else {
            // CREAR, ACTUALIZAR, ELIMINAR usan lock exclusivo
            return p.getArchivoObjetivo().adquirirLockEscritura();
        }
    }

    private void ejecutarOperacionDisco() {
        if (!colaES.estaVacia()) {
            
            // --- AQUÍ APLICARÁS TUS POLÍTICAS: FIFO, SSTF, SCAN, C-SCAN ---
            // Por ahora extraemos el primero (Comportamiento FIFO básico)
            PCB p = colaES.desencolar(); 
            
            System.out.println("--> DISCO: Procesando petición del Proceso " + p.getId() + " (" + p.getOperacion() + ")");
            
            // El trabajo físico del disco termina
            p.setEstado("TERMINADO");

            // ¡VITAL! Liberar el archivo para que los de la cola de bloqueados puedan entrar
            if (p.getOperacion().equals("LEER")) {
                p.getArchivoObjetivo().liberarLockLectura();
            } else {
                p.getArchivoObjetivo().liberarLockEscritura();
            }
            
            System.out.println("--> Proceso " + p.getId() + " FINALIZADO. Lock de " + p.getArchivoObjetivo().getNombre() + " liberado.");
        }
    }

    public void detener() {
        this.simulando = false;
    }
}