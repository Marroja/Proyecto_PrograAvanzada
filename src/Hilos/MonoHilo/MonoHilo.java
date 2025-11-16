package Hilos.MonoHilo;

import EstacionesDatos.AnalizadorEstacion;
import EstacionesDatos.Estacion;
import EstacionesDatos.FiltroDatos;
import EstacionesDatos.RenglonDatos;
import Utils.LectorXML;

public class MonoHilo extends Thread{

	private final Estacion[] estaciones;
	private final FiltroDatos filtroDatos;
	private boolean corriendo = true;

	MonoHilo(Estacion[] estaciones, FiltroDatos filtroDatos){
		this.estaciones = estaciones;
		this.filtroDatos = filtroDatos;
	}

	@Override
	public void run() {
		for(int i = 0; i < estaciones.length; i++){
			AnalizadorEstacion analizador = estaciones[i].generaAnalizador();
			RenglonDatos[] renglonesFiltrados = analizador.filtraDatos(filtroDatos);


		}

		corriendo = false;
	}
}
