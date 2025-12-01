package Hilos.PoliHilo;

import ArchivosDatos.AnalizadorArchivo;
import ArchivosDatos.Archivo;
import ArchivosDatos.FiltroDatos;
import ArchivosDatos.RenglonDatos;
import Hilos.Hilo;
import Utils.Bitacora;

import java.util.ArrayList;

public class Trabajador extends Hilo {

	private final Archivo[] estaciones;
	private final FiltroDatos filtroDatos;
	private boolean fueLeido = false;

	Trabajador(Archivo[] estaciones, FiltroDatos filtroDatos){
		super();
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
	public void run() {
		ArrayList<RenglonDatos> listaFiltrados = new ArrayList<>();
		for(int i = 0; i < estaciones.length; i++){
			AnalizadorArchivo analizador = estaciones[i].generaAnalizador();
			if(analizador == null){
				Bitacora.reportaExcepcion("Error en la creación del analizador para la estación:" + estaciones[i].toString());
				continue;
			}
			RenglonDatos[] renglonesFiltrados = analizador.filtraDatos(filtroDatos);

			for(int j = 0; j < renglonesFiltrados.length; j++){
				if(filtroDatos.cumpleCriterio(renglonesFiltrados[j])){
					listaFiltrados.add(renglonesFiltrados[j]);
				}
			}
		}

		this.resultados = new RenglonDatos[listaFiltrados.size()];
		for(int i = 0; i < listaFiltrados.size(); i++){
			super.resultados[i] = listaFiltrados.get(i);
		}

		corriendo = false;

		Bitacora.reportaMovimiento("El hilo " + this.getName() + " terminó de procesar " + estaciones.length + " estaciones");
	}

	public boolean fueLeido(){
		return this.fueLeido;
	}

	public RenglonDatos[] resultados() {
		if(!fueLeido){
			fueLeido = true;
			return super.resultados();
		}
		throw new RuntimeException("Los valores del trabajador sólo deben leerse una vez, al acumularlos en el maestro");
	}

}
