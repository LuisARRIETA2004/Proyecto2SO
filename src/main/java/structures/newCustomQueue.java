/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package structures;

import classes.PCB;

/**
 * Cola personalizada para la gestión de procesos y planificación de disco.
 */
public class newCustomQueue {

    private PCB frente;
    private PCB fin;

    public newCustomQueue() {
        this.frente = null;
        this.fin = null;
    }

    public void encolar(PCB nuevoProceso) {
        if (estaVacia()) {
            frente = nuevoProceso;
            fin = nuevoProceso;
        } else {
            fin.setSiguienteProceso(nuevoProceso);
            fin = nuevoProceso;
        }
    }

    public PCB desencolar() {
        if (estaVacia()) {
            return null;
        }

        PCB procesoSaliente = frente;
        frente = frente.getSiguienteProceso();

        if (frente == null) {
            fin = null;
        }

        procesoSaliente.setSiguienteProceso(null);
        return procesoSaliente;
    }

    /**
     * Retorna el primer elemento sin sacarlo de la cola.
     * Requerido por el ThreadScheduler.
     */
    public PCB peek() {
        return frente;
    }

    public boolean estaVacia() {
        return frente == null;
    }

    public int getSize() {
        int contador = 0;
        PCB actual = frente;
        while (actual != null) {
            contador++;
            actual = actual.getSiguienteProceso();
        }
        return contador;
    }

    /**
     * Retorna un proceso según su posición (índice).
     * Requerido por el MainFrame para llenar las tablas.
     */
    public PCB get(int index) {
        if (estaVacia() || index < 0 || index >= getSize()) {
            return null;
        }

        PCB actual = frente;
        for (int i = 0; i < index; i++) {
            actual = actual.getSiguienteProceso();
        }
        return actual;
    }

    // --- POLÍTICAS DE PLANIFICACIÓN DE DISCO ---

    public PCB removerNodoEspecifico(PCB aRemover, PCB anterior) {
        if (aRemover == frente) {
            return desencolar();
        }
        anterior.setSiguienteProceso(aRemover.getSiguienteProceso());
        if (aRemover == fin) {
            fin = anterior;
        }
        aRemover.setSiguienteProceso(null);
        return aRemover;
    }

    public PCB extraerSSTF(int cabezal) {
        if (estaVacia()) return null;

        PCB actual = frente;
        PCB anterior = null;
        PCB mejor = frente;
        PCB anteriorAlMejor = null;
        int menorDistancia = Math.abs(frente.getBloqueDestino() - cabezal);

        while (actual != null) {
            int distancia = Math.abs(actual.getBloqueDestino() - cabezal);
            if (distancia < menorDistancia) {
                menorDistancia = distancia;
                mejor = actual;
                anteriorAlMejor = anterior;
            }
            anterior = actual;
            actual = actual.getSiguienteProceso();
        }
        return removerNodoEspecifico(mejor, anteriorAlMejor);
    }

    public PCB extraerSCAN(int cabezal, int[] direccionActual) {
        if (estaVacia()) return null;

        PCB actual = frente;
        PCB anterior = null;
        PCB mejor = null;
        PCB anteriorAlMejor = null;
        int menorDistancia = Integer.MAX_VALUE;

        while (actual != null) {
            int destino = actual.getBloqueDestino();
            boolean enCamino = (direccionActual[0] == 1 && destino >= cabezal) || 
                               (direccionActual[0] == -1 && destino <= cabezal);

            if (enCamino) {
                int distancia = Math.abs(destino - cabezal);
                if (distancia < menorDistancia) {
                    menorDistancia = distancia;
                    mejor = actual;
                    anteriorAlMejor = anterior;
                }
            }
            anterior = actual;
            actual = actual.getSiguienteProceso();
        }

        if (mejor != null) {
            return removerNodoEspecifico(mejor, anteriorAlMejor);
        } else {
            direccionActual[0] *= -1; 
            return extraerSCAN(cabezal, direccionActual);
        }
    }

    public PCB extraerCSCAN(int cabezal) {
        if (estaVacia()) return null;

        PCB actual = frente;
        PCB anterior = null;
        PCB mejor = null;
        PCB anteriorAlMejor = null;
        int menorDistancia = Integer.MAX_VALUE;

        while (actual != null) {
            int destino = actual.getBloqueDestino();
            if (destino >= cabezal) {
                int distancia = destino - cabezal;
                if (distancia < menorDistancia) {
                    menorDistancia = distancia;
                    mejor = actual;
                    anteriorAlMejor = anterior;
                }
            }
            anterior = actual;
            actual = actual.getSiguienteProceso();
        }

        if (mejor != null) {
            return removerNodoEspecifico(mejor, anteriorAlMejor);
        } else {
            actual = frente;
            anterior = null;
            int bloqueMasBajo = Integer.MAX_VALUE;
            while (actual != null) {
                if (actual.getBloqueDestino() < bloqueMasBajo) {
                    bloqueMasBajo = actual.getBloqueDestino();
                    mejor = actual;
                    anteriorAlMejor = anterior;
                }
                anterior = actual;
                actual = actual.getSiguienteProceso();
            }
            return removerNodoEspecifico(mejor, anteriorAlMejor);
        }
    }
}
