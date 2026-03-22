/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package classes;

/**
 *
 * @author truenno
 */
public class Disk {
    private Block[] bloques;
    private int capacidadTotal;
    private int bloquesLibres;

    public Disk(int capacidadTotal) {
        this.capacidadTotal = capacidadTotal;
        this.bloquesLibres = capacidadTotal;
        this.bloques = new Block[capacidadTotal];
        
        for (int i = 0; i < capacidadTotal; i++) {
            bloques[i] = new Block(i);
        }
    }

    // Lógica para asignar bloques a un archivo de forma encadenada.
    public int asignarArchivo(String nombreArchivo, int tamanoEnBloques) {
        if (tamanoEnBloques > bloquesLibres) {
            System.out.println("Error: No hay espacio suficiente en el disco.");
            return -1; 
        }

        int primerBloque = -1;
        int bloqueAnterior = -1;
        int bloquesAsignados = 0;

        for (int i = 0; i < capacidadTotal && bloquesAsignados < tamanoEnBloques; i++) {
            
            if (!bloques[i].isOcupado()) {
                bloques[i].setOcupado(true);
                bloques[i].setPropietario(nombreArchivo);
                bloques[i].setSiguienteBloque(-1); // Por defecto, asume que es el último

                if (primerBloque == -1) {
                    primerBloque = i;
                } else {
                    bloques[bloqueAnterior].setSiguienteBloque(i);
                }

                bloqueAnterior = i;
                bloquesAsignados++;
                bloquesLibres--;
            }
        }

        return primerBloque;     }

    // Lógica para liberar los bloques cuando se elimina un archivo.

    public void liberarArchivo(int primerBloque) {
        int bloqueActual = primerBloque;

        while (bloqueActual != -1) {
            Block b = bloques[bloqueActual];
            int siguiente = b.getSiguienteBloque(); // Guardamos a dónde apunta antes de borrarlo

            b.setOcupado(false);
            b.setPropietario("");
            b.setSiguienteBloque(-1);
            bloquesLibres++;

            bloqueActual = siguiente;
        }
    }

    public Block[] getBloques() {
        return bloques;
    }
}