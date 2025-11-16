package EstacionesDatos;

import Utils.Bitacora;
import Utils.LectorXML;

import java.io.IOException;

import static Utils.Matematicas.*;

public class Estacion {

	private final String estado;
	private final String municipio;
	private final double latitud;
	private final double longitud;
	private final double altitud;
	private final String dirDiarios;
	private final String dirMensuales;

	public AnalizadorEstacion generaAnalizador(){
		try{
			return new AnalizadorEstacion(this);
		}catch (IOException e){
			Bitacora.reportaExcepcion("No se pudo generar el analizador de la estación");
			Bitacora.reportaExcepcion("\t->" + this.toString());
			return null;
		}
	}

	public Estacion(String estado, String municipio, double latitud, double longitud, double altitud, String dirDiarios, String dirMensuales){
		this.estado = estado;
		this.municipio = municipio;
		this.latitud = latitud;
		this.longitud = longitud;
		this.altitud = altitud;
		this.dirDiarios = dirDiarios;
		this.dirMensuales = dirMensuales;
	}

	public String dirDiarios(){
		return this.dirDiarios;
	}

	public String dirMensuales(){
		return this.dirMensuales;
	}

	public boolean cumpleCriterios(String estado, String municipio,
								   double latitudInf, double latitudSup,
								   double longitudInf, double longitudSup,
								   double altitudInf, double altitudSup){

		boolean cumple = true;
		cumple &= esEstado(estado);
		cumple &= esMunicipio(municipio);
		cumple &= entreLatitudes(latitudInf, latitudSup);
		cumple &= entreLongitudes(longitudInf, longitudSup);
		cumple &= entreAltitudes(altitudInf, altitudSup);

		return cumple;
	}

	public boolean cumpleCriterios(FiltroEstacion discriminante){

		boolean cumple = true;
		cumple &= esEstado(discriminante.estado);
		cumple &= esMunicipio(discriminante.municipio);
		cumple &= entreLatitudes(discriminante.latitudInf, discriminante.latitudSup);
		cumple &= entreLongitudes(discriminante.longitudInf, discriminante.longitudSup);
		cumple &= entreAltitudes(discriminante.altitudInf, discriminante.altitudSup);

		return cumple;
	}

	public String estado(){
		return this.estado;
	}

	public String municipio(){
		return this.municipio;
	}

	private boolean esEstado(String estado){
		if(estado.equalsIgnoreCase("*")){
			return true;
		}
		return this.estado.equalsIgnoreCase(estado);
	}

	private boolean esMunicipio(String municipio){
		if(municipio.equalsIgnoreCase("*")){
			return true;
		}
		return this.municipio.equalsIgnoreCase(municipio);
	}

	private boolean entreAltitudes(double alt1, double alt2){
		return valorEntreValores(this.altitud, alt1, alt2);
	}

	private boolean entreLongitudes(double long1, double long2){
		return valorEntreValores(this.longitud, long1, long2);
	}

	private boolean entreLatitudes(double lat1, double lat2){
		return valorEntreValores(this.latitud, lat1, lat2);
	}



	@Override
	public String toString() {
		return 	estado + ".." +
				municipio + ".." +
				latitud + ".." +
				longitud + ".." +
				altitud;
	}

	public static void main(String[] args) {
		Estacion[] estaciones = LectorXML.arregloEstaciones();

		//Sólo en el estado de México
		String estado = "MÉXICO";
		double latitudMin = 0;
		double latitudMax = 90;
		double longitudMin = -180;
		double longitudMax = 180;
		//Las estaciones arriba de 1500 Mts
		double altitudMin = 2500;
		double altitudMax = 10000;

		for(int i = 0; i < estaciones.length; i++){
			if(estaciones[i].cumpleCriterios(estado, "*",
					latitudMin, latitudMax,
					longitudMin, longitudMax,
					altitudMin, altitudMax)){
				System.out.println(estaciones[i]);
			}
		}
	}
}
