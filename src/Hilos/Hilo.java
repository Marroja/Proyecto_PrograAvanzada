package Hilos;

import ArchivosDatos.RenglonDatos;

/**
 * Plantilla base para los hilos que estarán trabajando sabiendo que tienen que regresar resultados
 */
public class Hilo extends Thread{

	protected boolean corriendo = true;
	protected RenglonDatos[] resultados;

	public boolean estaCorriendo(){
		return this.corriendo;
	}

	/**
	 * Esta función debe sólo leerse después de que haya terminado el hilo de ejecución
	 * @return regresa los renglones de todos los resultados obtenidos
	 */
	public RenglonDatos[] resultados(){
		if(!this.corriendo){
			return this.resultados;
		}else{
			throw new RuntimeException("No debe llamarse a los resultados antes de que haya terminado la tarea");
		}
	}
}
