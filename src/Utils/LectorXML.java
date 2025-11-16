package Utils;

import EstacionesDatos.Estacion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Clase utilizada para leer el archivo XML donde se contienen los URLs de la base de datos de CONAGUA
 * @author Aragón Mureddu Rodrigo
 */
public final class LectorXML {

	private enum Criterio{
		DIARIOS,
		MENSUALES,
		ESTADO,
		MUNICIPIO,
		LATITUD,
		LONGITUD,
		ALTITUD,
	}

	/**
	 * La clase Utils.LectorXML no debe ser instanciada
	 */
	private LectorXML(){

	}

	/**
	 * Se lee el XML obtenido de la base de datos de CONAGUA y posteriormente se filtran los URLs donde se obtendrán
	 * los valores.
	 * @return
	 * Se retorna un arreglo de URLs válidos para consulta como archivos .txt
	 */
	private static String[] listaCriterio(Criterio criterio){
		//Meridianos: -120 -> -85
		//Paralelos: 13 -> 33
		String[] lineasRetorno = new String[0];
		try{

			BufferedReader br = new BufferedReader(new FileReader(new File("/home/marroja/Documentos/PCIC/PrograAvanzada/Proyecto/estaciones.xml")));
			ArrayList<String> lineas = new ArrayList<>();

			String linea;
			while((linea = br.readLine()) != null){
				if(linea.contains("<SimpleData name=\""+criterio.name()+"\">")){
					int i = linea.indexOf("<SimpleData name=\""+criterio.name()+"\">");
					int l = ("<SimpleData name=\""+criterio.name()+"\">").length();
					int f = linea.indexOf("</SimpleData>");
					String dir = linea.substring(i+l, f);
					lineas.add(dir);
					//System.out.println(linea.substring(i + l, f));
				}
			}

			lineasRetorno = new String[lineas.size()];
			for(int i = 0; i < lineasRetorno.length; i++){
				lineasRetorno[i] = lineas.get(i);
			}

		}catch (IOException e){
			Bitacora.reportaExcepcion("ERROR: No se pudo abrir el archivo XML ");
			Bitacora.reportaExcepcion(e.getLocalizedMessage());
		}

		return lineasRetorno;
	}


	private static double[] listaAltitudes(){
		String[] comoTexto = listaCriterio(Criterio.ALTITUD);
		double[] comoDoble = new double[comoTexto.length];
		for(int i = 0; i < comoTexto.length; i++){
			//qph falta revisar problemas en la conversión de datos
			comoDoble[i] = Double.parseDouble(comoTexto[i]);
		}
		return comoDoble;
	}

	private static double[] listaLatitudes(){
		String[] comoTexto = listaCriterio(Criterio.LATITUD);
		double[] comoDoble = new double[comoTexto.length];
		for(int i = 0; i < comoTexto.length; i++){
			//qph falta revisar problemas en la conversión de datos
			comoDoble[i] = Double.parseDouble(comoTexto[i]);
		}
		return comoDoble;
	}

	private static double[] listaLongitudes(){
		String[] comoTexto = listaCriterio(Criterio.LONGITUD);
		double[] comoDoble = new double[comoTexto.length];
		for(int i = 0; i < comoTexto.length; i++){
			//qph falta revisar problemas en la conversión de datos
			comoDoble[i] = Double.parseDouble(comoTexto[i]);
		}
		return comoDoble;
	}

	private static String[] listaEstados(){
		return listaCriterio(Criterio.ESTADO);
	}

	private static String[] listaMunicipios(){
		return listaCriterio(Criterio.MUNICIPIO);
	}

	private static String[] listaDiarios(){
		return listaCriterio(Criterio.DIARIOS);
	}

	private static String[] listaMensuales(){
		return listaCriterio(Criterio.MENSUALES);
	}

	public static Estacion[] arregloEstaciones(){
		//Se espera que todos los arreglos obtenidos del XML sean del mismo tamaño
		String[] estados = listaEstados();
		String[] municipios = listaMunicipios();
		double[] latitudes = listaLatitudes();
		double[] longitudes = listaLongitudes();
		double[] altitudes = listaAltitudes();
		String[] dirsDiarios = listaDiarios();
		String[] dirsMensuales = listaMensuales();

		//Se crea un arreglo del tamaño de los datos leídos
		Estacion[] estaciones = new Estacion[estados.length];
		for(int i = 0; i < estaciones.length; i++){
			estaciones[i] = new Estacion(
					estados[i], municipios[i],
					latitudes[i], longitudes[i], altitudes[i],
					dirsDiarios[i], dirsMensuales[i]);
		}

		return estaciones;
	}


	public static void main(String[] args) throws Exception {
		System.out.println(Arrays.toString(arregloEstaciones()));
	}
}
