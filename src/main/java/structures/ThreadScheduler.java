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

	private newCustomQueue colaListos;
	private newCustomQueue colaBloqueados;
	private newCustomQueue colaES;
	private int posicionCabezal = 50;
	private int direccion = 1;
	private String politicaActiva = "SSTF"; // Puede ser "FIFO", "SSTF", "SCAN", "C-SCAN"

	private boolean simulando;

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
			revisarBloqueados();

			procesarListos();

			ejecutarOperacionDisco();

			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				System.out.println("Planificador interrumpido.");
			}
		}
		System.out.println("Planificador detenido.");
	}

	private void revisarBloqueados() {
		if (colaBloqueados.estaVacia()) {
			return;
		}

		int cantidad = colaBloqueados.getSize();

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
			return p.getArchivoObjetivo().adquirirLockEscritura();
		}
	}

	private void ejecutarOperacionDisco() {
		if (!colaES.estaVacia()) {

			PCB p = null;
			int[] refDireccion = {this.direccion}; 

			switch (politicaActiva) {
				case "FIFO":
					p = colaES.desencolar(); 
					break;
				case "SSTF":
					p = colaES.extraerSSTF(posicionCabezal);
					break;
				case "SCAN":
					p = colaES.extraerSCAN(posicionCabezal, refDireccion);
					this.direccion = refDireccion[0]; 
					break;
				case "C-SCAN":
					p = colaES.extraerCSCAN(posicionCabezal);
					break;
				default:
					p = colaES.desencolar();
					break;
			}

			if (p != null) {
				System.out.println("-> [DISCO] Movimiento: de cilindro " + posicionCabezal + " a " + p.getBloqueDestino());

				posicionCabezal = p.getBloqueDestino();

				p.setEstado("TERMINADO");
				if (p.getOperacion().equals("LEER")) {
					p.getArchivoObjetivo().liberarLockLectura();
				} else {
					p.getArchivoObjetivo().liberarLockEscritura();
				}
			}
		}
	}

	public void detener() {
		this.simulando = false;
	}
}
