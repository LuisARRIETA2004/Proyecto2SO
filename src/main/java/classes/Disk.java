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
        
        // Inicializamos el disco con bloques vacíos
        for (int i = 0; i < capacidadTotal; i++) {
            bloques[i] = new Block(i);
        }
    }

    // Lógica para asignar bloques a un archivo de forma encadenada.
    public int asignarArchivo(String nombreArchivo, int tamanoEnBloques) {
        // Validación 1: ¿Hay espacio suficiente?
        if (tamanoEnBloques > bloquesLibres) {
            System.out.println("Error: No hay espacio suficiente en el disco.");
            return -1; 
        }

        int primerBloque = -1;
        int bloqueAnterior = -1;
        int bloquesAsignados = 0;

        // Recorremos el disco buscando bloques libres
        for (int i = 0; i < capacidadTotal && bloquesAsignados < tamanoEnBloques; i++) {
            
            if (!bloques[i].isOcupado()) {
                // 1. Ocupamos el bloque actual
                bloques[i].setOcupado(true);
                bloques[i].setPropietario(nombreArchivo);
                bloques[i].setSiguienteBloque(-1); // Por defecto, asume que es el último

                // 2. Lógica de encadenamiento
                if (primerBloque == -1) {
                    // Si es el primer bloque que encontramos, lo guardamos para retornarlo
                    primerBloque = i;
                } else {
                    // Si NO es el primero, hacemos que el bloque anterior apunte a este nuevo
                    bloques[bloqueAnterior].setSiguienteBloque(i);
                }

                // 3. Actualizamos variables para la siguiente iteración
                bloqueAnterior = i;
                bloquesAsignados++;
                bloquesLibres--;
            }
        }

        return primerBloque; // Retornamos la "cabeza" de la lista enlazada
    }

    // Lógica para liberar los bloques cuando se elimina un archivo.

    public void liberarArchivo(int primerBloque) {
        int bloqueActual = primerBloque;

        // Recorremos la cadena hasta llegar a un bloque que apunte a -1
        while (bloqueActual != -1) {
            Block b = bloques[bloqueActual];
            int siguiente = b.getSiguienteBloque(); // Guardamos a dónde apunta antes de borrarlo

            // Limpiamos el bloque actual
            b.setOcupado(false);
            b.setPropietario("");
            b.setSiguienteBloque(-1);
            bloquesLibres++;

            // Saltamos al siguiente eslabón de la cadena
            bloqueActual = siguiente;
        }
    }

    // Método útil para la GUI: Obtener el arreglo de bloques para pintarlos
    public Block[] getBloques() {
        return bloques;
    }
}