package Hilos.PoliHilo;

import EstacionesDatos.AnalizadorEstacion;
import EstacionesDatos.Estacion;
import EstacionesDatos.FiltroDatos;
import EstacionesDatos.RenglonDatos;
import Utils.Bitacora;

import java.util.ArrayList;

public class Trabajador extends Thread{

	private final Estacion[] estaciones;
	private final FiltroDatos filtroDatos;
	private RenglonDatos[] renglonesLimpios;
	private boolean corriendo = true;
	private boolean fueLeido = false;

	Trabajador(Estacion[] estaciones, FiltroDatos filtroDatos){
		this.estaciones = estaciones;
		this.filtroDatos = filtroDatos;
	}


		/*
		- descarga el contenido del URL (como Stream)
		- va a leer cada renglón
		- va a filtrar el renglón de acuerdo al criterio de filtro
		- cuando termina lo regresa al maestro
	 */

	@Override
	public synchronized void run() {
		ArrayList<RenglonDatos> listaFiltrados = new ArrayList<>();
		for(int i = 0; i < estaciones.length; i++){
			AnalizadorEstacion analizador = estaciones[i].generaAnalizador();
			if(analizador == null){
				Bitacora.reportaExcepcion("Error en la creación del analizador para la estación:" + estaciones[i].toString());
				continue;
			}
			RenglonDatos[] renglonesFiltrados = analizador.filtraDatos(filtroDatos);

			for(int j = 0; j < renglonesFiltrados.length; j++){
				if(renglonesFiltrados[j].cumpleCriterio(filtroDatos)){
					listaFiltrados.add(renglonesFiltrados[j]);
				}
			}
		}

		renglonesLimpios = new RenglonDatos[listaFiltrados.size()];
		for(int i = 0; i < listaFiltrados.size(); i++){
			renglonesLimpios[i] = listaFiltrados.get(i);
		}

		corriendo = false;

		Bitacora.reportaMovimiento("El hilo " + this.getName() + " terminó de procesar " + estaciones.length + " estaciones");
	}

	public boolean estaCorriendo(){
		return this.corriendo;
	}

	public boolean fueLeido(){
		return this.fueLeido;
	}

	//qph se tienen que regresar los renglones con la estación a la que corresponden
	public RenglonDatos[] resultados() {
		fueLeido = true;
		return this.renglonesLimpios;
	}

}
