package Hilos.MonoHilo;

import EstacionesDatos.AnalizadorEstacion;
import EstacionesDatos.Estacion;
import EstacionesDatos.FiltroDatos;
import EstacionesDatos.RenglonDatos;
import Utils.LectorXML;

import java.util.ArrayList;

public class MonoHilo extends Thread{

	private final Estacion[] estaciones;
	private final FiltroDatos filtroDatos;
	private boolean corriendo = true;

	private RenglonDatos[] renglonesLimpios;

	public MonoHilo(Estacion[] estaciones, FiltroDatos filtroDatos){
		this.estaciones = estaciones;
		this.filtroDatos = filtroDatos;
	}

	@Override
	public void run() {
		ArrayList<RenglonDatos> listaFiltrados = new ArrayList<>();
		for(int i = 0; i < estaciones.length; i++){
			AnalizadorEstacion analizador = estaciones[i].generaAnalizador();
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
	}

	public boolean estaCorriendo(){
		return this.corriendo;
	}

	//qph se tienen que regresar los renglones con la estación a la que corresponden
	public RenglonDatos[] resultados() {
		return this.renglonesLimpios;
	}
}
