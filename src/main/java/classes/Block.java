/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package classes;

/**
 *
 * @author truenno
 */
	public class Block {

		private int indice;         
		private boolean ocupado;      
		private String propietario;    
		private int siguienteBloque;    
		public Block(int indice) {
			this.indice = indice;
			this.ocupado = false;
			this.propietario = "";
			this.siguienteBloque = -1;  // -1 representará "Null" o "Fin del archivo"
		}

		public boolean isOcupado() {
			return ocupado;
		}

		public void setOcupado(boolean ocupado) {
			this.ocupado = ocupado;
		}

		public String getPropietario() {
			return propietario;
		}

		public void setPropietario(String propietario) {
			this.propietario = propietario;
		}

		public int getSiguienteBloque() {
			return siguienteBloque;
		}

		public void setSiguienteBloque(int siguienteBloque) {
			this.siguienteBloque = siguienteBloque;
		}
                public int getIndice() {
                    return indice;
                }
	}	
