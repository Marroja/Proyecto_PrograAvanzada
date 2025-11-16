package EstacionesDatos;

import Utils.Bitacora;
import Utils.LectorXML;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class AnalizadorEstacion {

	private final Estacion estacion;
	private final URL urlDiario;
	private final URL urlMensual;
	private final RenglonDatos[] renglonesDatos;

	static{
		try {
			disableSSLValidation();
		} catch (Exception e) {
			Bitacora.reportaExcepcion("ERROR: no se pudo inhabilitar protocolo SSL");
			throw new RuntimeException(e);
		}
	}

	AnalizadorEstacion(Estacion estacion) throws IOException, NumberFormatException {
		this.estacion = estacion;

		URL[] conectores = obtenURLs();
		this.urlDiario = conectores[0];
		this.urlMensual = conectores[1];
		this.renglonesDatos = this.leeDiarios();
	}

	private URL[] obtenURLs(){
		URL uDiario = null;
		URL uMensual = null;

		try{
			uDiario = new URL(estacion.dirDiarios());
			uMensual = new URL(estacion.dirMensuales());
		}catch (MalformedURLException e) {
			Bitacora.reportaExcepcion("Error en el formato de los URLs:" +
					"\r" + estacion.dirDiarios() +
					"\r" + estacion.dirMensuales());
			Bitacora.reportaExcepcion(e.getMessage());
		}

		return new URL[]{uDiario, uMensual};
	}


	public RenglonDatos[] filtraDatos(FiltroDatos filtroDatos) {
		ArrayList<RenglonDatos> filtrados = new ArrayList<>();
		for(int i = 0; i < renglonesDatos.length; i++){
			if(renglonesDatos[i].cumpleCriterio(filtroDatos)){
				filtrados.add(renglonesDatos[i]);
			}
		}

		RenglonDatos[] arrFiltrados = new RenglonDatos[filtrados.size()];
		for(int i = 0; i < arrFiltrados.length; i++){
			arrFiltrados[i] = filtrados.get(i);
		}

		Bitacora.reportaMovimiento("Se filtraron renglones " + this.renglonesDatos.length + " -> " + arrFiltrados.length);
		Bitacora.reportaMovimiento("->Filtro: "+ filtroDatos.toString());

		return arrFiltrados;
	}

	private RenglonDatos[] leeDiarios() throws IOException, NullPointerException {

		BufferedReader lectorDiario = new BufferedReader(new InputStreamReader(urlDiario.openStream()));

		ArrayList<RenglonDatos> renglones = new ArrayList<>();

		String linea;
		boolean leyendoEncabezado = true;
		while((linea = lectorDiario.readLine()) != null){
			try{
				renglones.add(RenglonDatos.digiereRenglon(linea));
				leyendoEncabezado = false;
			}catch (Exception e){
				if(!leyendoEncabezado){
					Bitacora.reportaExcepcion("No se pudo digerir renglón: "+ linea);
				}
			}
		}

		RenglonDatos[] arrRenglones = new RenglonDatos[renglones.size()];
		for(int i = 0; i < arrRenglones.length; i++){
			arrRenglones[i] = renglones.get(i);
		}

		return arrRenglones;
	}

	private RenglonDatos[]  leeMensuales() throws IOException {
		BufferedReader lectorMensual = new BufferedReader(new InputStreamReader(urlMensual.openStream()));

		ArrayList<RenglonDatos> renglones = new ArrayList<>();

		String linea;
		boolean leyendoEncabezado = true;
		while((linea = lectorMensual.readLine()) != null){
			try{
				renglones.add(RenglonDatos.digiereRenglon(linea));
				leyendoEncabezado = false;
			}catch (Exception e){
				if(!leyendoEncabezado){
					Bitacora.reportaExcepcion("No se pudo digerir renglón: "+ linea);
				}
			}
		}

		RenglonDatos[] arrRenglones = new RenglonDatos[renglones.size()];
		for(int i = 0; i < arrRenglones.length; i++){
			arrRenglones[i] = renglones.get(i);
		}

		return arrRenglones;
	}

	// El siguiente es obtenido de ChatGPT, nos atascamos al momento de tener que consultar las páginas en línea,
	// ya que por alguna razón los certificados SSL de CONAGUA no son válidos. Hacer certificados SSL está más
	// allá de lo que conocemos de redes.

	// Desactiva validación de certificados y nombre de host
	private static void disableSSLValidation() throws Exception {
		TrustManager[] trustAllCerts = new TrustManager[]{
				new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
					public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
					public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
				}
		};

		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		// Desactiva verificación del hostname
		HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
	}

	/**
	 * Para pruebas
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		disableSSLValidation();
		Estacion e = LectorXML.arregloEstaciones()[0];
		FiltroDatos f = new FiltroDatos(LocalDate.MIN, LocalDate.MAX, 1, 100, 0, 100, -100, 100, -100, 100);
		RenglonDatos[] r = e.generaAnalizador().filtraDatos(f);
		for(int i = 0; i < r.length; i++){
			System.out.println(r[i]);
		}
	}

}
