package Utils;

import java.util.Arrays;

public class Config {

	private static boolean inicializado = false;
	private static final String dirURLs = "recursos/urls.txt";
	private static final String dirCarpeta = "recursos/archivos";
	private static final String dirConfig = "recursos/formato.ini";
	private static final String ESPACIO = "\\s+";
	private static final String ESPACIO_OPCIONAL = "\\s*";
	private static String separador = ESPACIO;
	private static char[] columnas = new char[]{'f','d','d','d','d'};
	private static String[] etiquetas = new String[]{"Fecha", "Precipitación", "Evaporación", "Temp. Max.", "Temp. Min."};

	private Config(){

	}

	/*
		Las columnas tienen el significado siguiente
		f - fecha
		d - double
		c - char
		i - int
	 */
	private static void inicia() {
		if(inicializado){
			return;
		}

		String[] lineasFormato = LectorArchivos.leeArchivoCompletoConFiltro(dirConfig, ';');

		for(String linea : lineasFormato){
			String[] lineaSeparada = linea.split("=", 2);
			String destino = lineaSeparada[0].trim();
			String valAsignado = lineaSeparada[1].trim();

			switch (destino.toUpperCase()){
				case "SEPARADOR":
						separador = ESPACIO_OPCIONAL + valAsignado + ESPACIO_OPCIONAL;
					break;
				case "FORMATO":
						String[] textoColumnas  = valAsignado.split(separador);
						columnas = new char[textoColumnas.length];
						for(int i = 0; i < columnas.length; i++){
							columnas[i] = textoColumnas[i].charAt(0);
						}
					break;
				case "ETIQUETAS":
						//Se espera que las etiquetas estén entre comillas con separadores en medio
						etiquetas = valAsignado.split("\"("+  separador + ")\"");
						//Recortar comillas al inicio y al final
						etiquetas[0] = etiquetas[0].substring(1);
						etiquetas[etiquetas.length - 1] = etiquetas[etiquetas.length - 1].substring(0, etiquetas[etiquetas.length - 1].length() - 1);
					break;
			}
		}
		inicializado = true;
	}

	public static String[] etiquetas(){
		if(inicializado){
			return Arrays.copyOf(etiquetas, etiquetas.length);
		}

		inicia();
		return Arrays.copyOf(etiquetas, etiquetas.length);
	}

	public static char[] columnas() {
		if(inicializado){
			return Arrays.copyOf(columnas, columnas.length);
		}

		inicia();
		return Arrays.copyOf(columnas(), columnas.length);
	}

	public static String separador() {
		if(inicializado){
			return separador;
		}

		inicia();
		return separador;
	}

	public static void main(String[] args) {
		inicia();
		System.out.println(separador);
		System.out.println(Arrays.toString(columnas));
		System.out.println(Arrays.toString(etiquetas));
	}

	public static boolean aceptaNaN() {
		return false;
	}

	public static String dirURLs() {
		return dirURLs;
	}

	public static String dirCarpeta() {
		return dirCarpeta;
	}
}
