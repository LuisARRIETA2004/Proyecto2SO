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
}
