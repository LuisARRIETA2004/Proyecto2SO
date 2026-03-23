/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package structures;

import classes.PCB;

/**
 *
 * @author truenno
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

	public boolean estaVacia() {
		return frente == null;
	}

	public int getSize() {
		int contador = 0;
		PCB actual = frente; // Empezamos desde el primero

		// Mientras haya un proceso, contamos y pasamos al siguiente
		while (actual != null) {
			contador++;
			actual = actual.getSiguienteProceso();
		}

		return contador;
	}

	// POLITICAS DE DISCO
	
	public PCB removerNodoEspecifico(PCB aRemover, PCB anterior) {
        if (aRemover == frente) {
            return desencolar(); // Si es el primero, usamos el método normal
        }
        // Saltamos el nodo a remover (lo desconectamos)
        anterior.setSiguienteProceso(aRemover.getSiguienteProceso());
        
        if (aRemover == fin) {
            fin = anterior; // Si era el último, actualizamos la cola
        }
        
        aRemover.setSiguienteProceso(null);
        return aRemover;
    }

	// ALGORITMO SSTF
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

    // ALGORITMO SCAN: El ascensor (Sube y luego baja)
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
            return extraerSCAN(cabezal, direccionActual); // Llamada recursiva
        }
    }

    // ALGORITMO C-SCAN: Ascensor circular (Solo sube, cuando llega arriba vuelve a empezar desde abajo)
    public PCB extraerCSCAN(int cabezal) {
        if (estaVacia()) return null;

        PCB actual = frente;
        PCB anterior = null;
        
        PCB mejor = null;
        PCB anteriorAlMejor = null;
        int menorDistancia = Integer.MAX_VALUE;

        // Pasada 1: Buscar el más cercano HACIA ARRIBA (destino >= cabezal)
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
            // Pasada 2: No hay más hacia arriba. Saltamos al bloque MÁS PEQUEÑO de toda la cola
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
