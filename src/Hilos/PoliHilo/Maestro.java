package Hilos.PoliHilo;

import EstacionesDatos.Estacion;
import EstacionesDatos.FiltroDatos;
import EstacionesDatos.RenglonDatos;
import Utils.Bitacora;

import java.util.ArrayList;

public class Maestro extends Thread {

	private final int numProcesadores;
	private final ArrayList<RenglonDatos> acumuladorRenglones;
	private final Trabajador[] trabajadores;
	private RenglonDatos[] resultados;
	private boolean corriendo = true;

	private long contadorTiempo;

	public Maestro(Estacion[] estaciones, FiltroDatos filtroDatos) {
		numProcesadores = Runtime.getRuntime().availableProcessors();
		acumuladorRenglones = new ArrayList<>();

		ArrayList<Estacion>[] estacionesPorProcesador = new ArrayList[numProcesadores];

		//Se reparten las estaciones en los arreglos que se asignarán a cada trabajador
		{
			for (int i = 0; i < numProcesadores; i++) {
				estacionesPorProcesador[i] = new ArrayList<>();
			}
			for (int i = 0; i < estaciones.length; i++) {
				estacionesPorProcesador[i % numProcesadores].add(estaciones[i]);
			}
		}

		/*
			T1	[e1	e5 e9]
			T2	[e2	e6]
			T3	[e3	e7]
			T4	[e4	e8]
		 */

		trabajadores = new Trabajador[numProcesadores];
		for(int i = 0; i < trabajadores.length; i++){
			Estacion[] estacionesTrabajador = new Estacion[estacionesPorProcesador[i].size()];
			for(int j = 0; j < estacionesTrabajador.length; j++){
				estacionesTrabajador[j] = estacionesPorProcesador[i].get(j);
			}
			trabajadores[i] = new Trabajador(estacionesTrabajador, filtroDatos);
		}
	}


	@Override
	public void run() {
		contadorTiempo = System.currentTimeMillis();
		for(Trabajador t : trabajadores){
			t.start();
		}
		Bitacora.reportaMovimiento("Se llamó a " + trabajadores.length + " a procesar la tarea");

		boolean esperando = true;
		do{
			//Si está corriendo un sólo hilo, seguimos esperando
			esperando = false;
			int numEsperados = 0;
			for(int i = 0; i < trabajadores.length; i++){
				if(trabajadores[i].estaCorriendo()){
					esperando = true;
					numEsperados ++;
				}
			}

			//Cuando un hilo termina de correr y todavía no ha sido leído, se acumulan sus resultados
			for(int i = 0; i < trabajadores.length; i++){
				if(!trabajadores[i].estaCorriendo() & !trabajadores[i].fueLeido()){
					RenglonDatos[] resultadosTrabajador = trabajadores[i].resultados();
					Bitacora.reportaMovimiento("Se acumularon " + resultadosTrabajador.length + " renglones a resultados");
					for(int k = 0; k < resultadosTrabajador.length; k++){
						acumuladorRenglones.add(resultadosTrabajador[k]);
					}
				}
			}

			if(esperando){
				try {
					sleep(1000);
					Bitacora.reportaMovimiento("Hilo maestro esperando a " + numEsperados + " hilos faltantes");
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}while(esperando);

		resultados = new RenglonDatos[acumuladorRenglones.size()];
		for(int i = 0; i < resultados.length; i++){
			resultados[i] = acumuladorRenglones.get(i);
		}

		Bitacora.reportaMovimiento("Hilo maestro entregando " + resultados.length + " renglones resultantes");

		long tiempoFin = System.currentTimeMillis();
		double deltaTiempo = (tiempoFin - contadorTiempo) / 1000.0;
		Bitacora.reportaMovimiento("Esta tarea tomó: " + deltaTiempo +"s");

		this.corriendo = false;
	}

	public boolean estaCorriendo(){
		return this.corriendo;
	}

	public RenglonDatos[] resultados(){
		return this.resultados;
	}
}
