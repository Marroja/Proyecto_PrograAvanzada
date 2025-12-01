package ArchivosDatos;

import Utils.Bitacora;
import Utils.Config;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class AnalizadorArchivo {

	private final Archivo archivo;
	private final RenglonDatos[] renglonesDatos;
    private final String nombreArchivo;

	static{
		try {
			disableSSLValidation();
		} catch (Exception e) {
			Bitacora.reportaExcepcion("ERROR: no se pudo inhabilitar protocolo SSL");
			throw new RuntimeException(e);
		}
	}

	AnalizadorArchivo(Archivo archivo) throws IOException, NumberFormatException, URISyntaxException {
		this.archivo = archivo;
        this.nombreArchivo = archivo.nombre();
		this.renglonesDatos = this.leeArchivo();
	}

	public RenglonDatos[] filtraDatos(FiltroDatos filtroDatos){
		ArrayList<RenglonDatos> filtrados = new ArrayList<>();
		RenglonDatos[] arrFiltrados;

		try{

			for(int i = 0; i < renglonesDatos.length; i++){
				if(filtroDatos.cumpleCriterio(renglonesDatos[i])){
					filtrados.add(renglonesDatos[i]);
				}
			}

			arrFiltrados = new RenglonDatos[filtrados.size()];
			for(int i = 0; i < arrFiltrados.length; i++){
				arrFiltrados[i] = filtrados.get(i);
			}

			Bitacora.reportaMovimiento("Se filtraron renglones " + this.renglonesDatos.length + " -> " + arrFiltrados.length);
			Bitacora.reportaMovimiento("->Filtro: "+ filtroDatos.toString());
		}catch (ClassCastException e){
			Bitacora.reportaExcepcion("Error en la aplicación del filtro suministrado:" + filtroDatos);
			Bitacora.reportaExcepcion("Se esperaba un filtro del tipo:" + Arrays.toString(Config.columnas()));
			return new RenglonDatos[]{};
		}

		return arrFiltrados;
	}

	private RenglonDatos[] leeArchivo() throws IOException, NullPointerException {
		ArrayList<RenglonDatos> acumRenglones = new ArrayList<>();

		BufferedReader br = null;

		if(archivo.esDeDisco()){
			br = new BufferedReader(new FileReader(archivo.comoArchivo()));
		}
		else
		if(archivo.esDeURL()){
			br = new BufferedReader(new InputStreamReader(archivo.comoURL().openStream()));
		}

		if(br == null){
			Bitacora.reportaExcepcion("Archivo no se pudo abrir correctamente: " + this.nombreArchivo);
			return new RenglonDatos[]{};
		}

		String linea;
		boolean leyendoCabecera = true;
		int numLinea = 0;
		while((linea = br.readLine()) != null){
			numLinea ++;
			try{
				RenglonDatos renglon = RenglonDatos.digiereRenglon(archivo.nombre(), numLinea, linea);
				acumRenglones.add(renglon);
				leyendoCabecera = false;
			} catch (Exception e) {
				if(!leyendoCabecera){
					Bitacora.reportaExcepcion("Error leyendo linea en el archivo: " + archivo.nombre());
					Bitacora.reportaExcepcion("Linea: " + linea);
					Bitacora.reportaExcepcion("Se espereba formato de columnas: "+ Arrays.toString(Config.columnas()));
				}
			}
		}

		RenglonDatos[] renglones = new RenglonDatos[acumRenglones.size()];
		for(int i = 0; i < renglones.length; i++){
			renglones[i] = acumRenglones.get(i);
		}

		return renglones;
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

		LocalDate fechaInf = LocalDate.MIN;
		LocalDate fechaMax = LocalDate.MAX;
		double precipMin = 0.0;
		double precipMax = 0.0;
		double evapMin = 0.0;
		double evapMax = 0.0;
		double tempMin = 15;
		double tempMax = 20.0;
		double tempMinMin = 0.0;
		double tempMinMax = 100.0;

		FiltroDatos f = new FiltroDatos(new Object[]{
				fechaInf, fechaMax,
				precipMin, precipMax,
				evapMin, evapMax,
				tempMin, tempMax,
				tempMinMin, tempMinMax
		});

		//disableSSLValidation();
		System.out.println("--------------- Disco -------------------");

		GestorArchivos gDisco = GestorArchivos.deCarpeta("Proyecto_PrograAvanzada/recursos/archivos");
		AnalizadorArchivo aDisco = gDisco.archivos()[0].generaAnalizador();

		RenglonDatos[] aFiltrados = aDisco.filtraDatos(f);

		for(int i = 0; i < aFiltrados.length; i++){
			System.out.println(aFiltrados[i]);
		}

		System.out.println("--------------- URL -------------------");

		GestorArchivos gURL = GestorArchivos.deListaDeURLs("Proyecto_PrograAvanzada/recursos/urls.txt");
		AnalizadorArchivo aURL = gURL.archivos()[0].generaAnalizador();

		RenglonDatos[] uFiltrados = aURL.filtraDatos(f);

		for(int i = 0; i < uFiltrados.length; i++){
			System.out.println(uFiltrados[i]);
		}
	}

}
