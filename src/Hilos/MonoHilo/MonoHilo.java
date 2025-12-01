package Hilos.MonoHilo;

import ArchivosDatos.AnalizadorArchivo;
import ArchivosDatos.Archivo;
import ArchivosDatos.FiltroDatos;
import ArchivosDatos.RenglonDatos;
import Hilos.Hilo;

import java.util.ArrayList;

public class MonoHilo extends Hilo {

	private final Archivo[] estaciones;
	private final FiltroDatos filtroDatos;

	public MonoHilo(Archivo[] estaciones, FiltroDatos filtroDatos){
		this.estaciones = estaciones;
		this.filtroDatos = filtroDatos;
	}

	@Override
	public void run() {
		ArrayList<RenglonDatos> listaFiltrados = new ArrayList<>();
		for(int i = 0; i < estaciones.length; i++){
			AnalizadorArchivo analizador = estaciones[i].generaAnalizador();
			RenglonDatos[] renglonesFiltrados = analizador.filtraDatos(filtroDatos);

			for(int j = 0; j < renglonesFiltrados.length; j++){
				if(filtroDatos.cumpleCriterio(renglonesFiltrados[j])){
					listaFiltrados.add(renglonesFiltrados[j]);
				}
			}
		}

		resultados = new RenglonDatos[listaFiltrados.size()];
		for(int i = 0; i < listaFiltrados.size(); i++){
			resultados[i] = listaFiltrados.get(i);
		}
		corriendo = false;
	}


}
