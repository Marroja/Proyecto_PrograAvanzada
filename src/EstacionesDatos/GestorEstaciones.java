package EstacionesDatos;

import Utils.Bitacora;
import Utils.LectorXML;

import java.util.ArrayList;
import java.util.HashMap;

public class GestorEstaciones {

	private final Estacion[] estaciones;
	private final ArrayList<Estacion> listasFiltrada;

	private final ArrayList<String> lEstados;
	private final ArrayList<String> lMunicipios;

	private final String[] estados;
	private final String[] municipios;

	private final HashMap<String, String> diccionarioMunicipioEstado;

	public GestorEstaciones() {
		this.estaciones = LectorXML.arregloEstaciones();
		this.listasFiltrada = new ArrayList<>();

		lEstados = new ArrayList<>();
		lMunicipios = new ArrayList<>();

		diccionarioMunicipioEstado = new HashMap<String, String>();

		for(int i = 0; i < estaciones.length; i++){
			String estado = estaciones[i].estado();
			String municipio = estaciones[i].municipio();

			if(!lEstados.contains(estado)){
				lEstados.add(estado);
			}

			if(!lMunicipios.contains(municipio)){
				lMunicipios.add(municipio);
				diccionarioMunicipioEstado.put(municipio, estado);
			}
		}

		estados = new String[lEstados.size()];
		for(int i = 0; i < lEstados.size(); i++){
			estados[i] = lEstados.get(i);
		}


		municipios = new String[lMunicipios.size()];
		for(int i = 0; i < municipios.length; i++){
			municipios[i] = lMunicipios.get(i);
		}
	}

	public Estacion[] estacionesFiltradas(){
		Estacion[] filtradas = new Estacion[listasFiltrada.size()];
		for(int i = 0; i < filtradas.length; i++){
			filtradas[i] = listasFiltrada.get(i);
		}
		return filtradas;
	}

	public void filtraEstaciones(FiltroEstacion filtro){
		for(int i = 0; i < estaciones.length; i++){
			if(estaciones[i].cumpleCriterios(filtro)){
				listasFiltrada.add(estaciones[i]);
			}
		}

		Bitacora.reportaMovimiento("Se filtraron "+ estaciones.length + " estaciones y quedaron " + listasFiltrada.size());
		Bitacora.reportaMovimiento("Filtro: " + filtro.toString());
	}

	public String[] estados(){
		return this.estados;
	}

	public String[] municipios(String[] estados){
		ArrayList<String> municipios = new ArrayList<>();
		for(int i = 0; i < estados.length; i++){
			String[] municipiosEstado = this.municipios(estados[i]);
			for(int j = 0; j < municipiosEstado.length; j++){
				municipios.add(municipiosEstado[j]);
			}
		}

		String[] municipiosRetorno = new String[municipios.size()];
		for(int i = 0; i < municipiosRetorno.length; i++){
			municipiosRetorno[i] = municipios.get(i);
		}

		return municipiosRetorno;
	}

	public String[] municipios(String estado){
		ArrayList<String> municipiosEnEstado = new ArrayList<>();
		for(String municipio : diccionarioMunicipioEstado.keySet()){
			if(diccionarioMunicipioEstado.get(municipio).equals(estado)){
				municipiosEnEstado.add(municipio);
			}
		}
		String[] municipios = new String[municipiosEnEstado.size()];
		for(int i = 0; i < municipios.length; i++){
			municipios[i] = municipiosEnEstado.get(i);
		}
		return municipios;
	}

	public boolean existeEstado(String estado){
		return lEstados.contains(estado);
	}

	public boolean existeMunicipio(String municipio){
		return lMunicipios.contains(municipio);
	}

	public static void main(String[] args) {
		GestorEstaciones g = new GestorEstaciones();
		FiltroEstacion f = new FiltroEstacion(new String[]{"*"}, new String[]{"*"}, -90, 90, -180, 180, 2000, 10000);
		g.filtraEstaciones(f);
		Estacion[] estaciones = g.estacionesFiltradas();
		for(int i = 0; i < estaciones.length; i++){
			System.out.println(estaciones[i]);
		}
	}

}
