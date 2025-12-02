package Interfaz;

import ArchivosDatos.*;
import Hilos.Hilo;
import Hilos.MonoHilo.MonoHilo;
import Hilos.PoliHilo.Maestro;
import Utils.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import Visualizacion.Graficador;

import static java.lang.Thread.sleep;

/**
 * @author Rodrigo Aragón Mureddu
 * Clase para facilitar los diferentes estados de manejo de la interfaz vía terminal
 */
public final class InterfazTerminal{

	private final static int LOCAL = 0;
	private final static int REMOTO = 1;

	enum Estado{
		ENTRADA,

		SELEC_CARPETA_O_URLS,
		SELEC_DIR_ARCHIVOS,

		SELEC_COLUMNAS_INTERES,
		SELEC_COTA_INFERIOR,
		SELEC_COTA_SUPERIOR,

		SELEC_CANTIDAD_HILOS,
		ESPERA_HILO_MAESTRO,
		RESULTADOS_LISTOS,

		SELEC_ESTADISTICOS,
		SELEC_GRAFICAS,
		SELEC_GUARDADO,
	}

	private Estado estado;
	private boolean abierto = true;

	private final Scanner lectorConsola;
	private GestorArchivos gestorArchivos;

	private char[] tipoColumnas;

	private int monoPoliHilo = 0;
	private int localRemoto = 0;

	private boolean[] bColumnasSeleccionadas;
	private final ArrayList<Object> acumuladorCotas = new ArrayList<>();
	private String[] etiquetasColumnas = new String[0];
	private int contadorColumnaPreguntada = 0;

	private RenglonDatos[] resultados;

	public InterfazTerminal(){
		lectorConsola = new Scanner(System.in);
		lectorConsola.useDelimiter("\r\n|\n");

		this.estado = Estado.ENTRADA;
		while(abierto){
			this.maquinaEstados();
		}
	}

	public void maquinaEstados() {
		switch (estado) {
			case ENTRADA:
				System.out.println("Bienvenido al analizador de base de datos de CONAGUA");
				this.estado = Estado.SELEC_CARPETA_O_URLS;
				break;

			case SELEC_CARPETA_O_URLS:
				System.out.println("Seleccione si desea consultar una base de datos en línea o local");
				System.out.println("0)Local  1)En línea");

				localRemoto = solicitaEntero(0, 1);

				this.estado = Estado.SELEC_DIR_ARCHIVOS;
				break;

			case SELEC_DIR_ARCHIVOS:{
				File archivoObjetivo = null;
				if (localRemoto == LOCAL) {
					System.out.println("Escriba la dirección de la carpeta donde se encuentran los archivos CSV");
					System.out.println("(Predet. " + Config.dirCarpeta() + ")");

					archivoObjetivo = solicitaCarpeta();
					gestorArchivos = GestorArchivos.deCarpeta(archivoObjetivo.getAbsolutePath());
				} else if (localRemoto == REMOTO) {
					System.out.println("Escriba la dirección del archivo que contenga los URLs a consultar");
					System.out.println("(Predet. " + Config.dirURLs() + ")");

					archivoObjetivo = solicitaArchivo();
					gestorArchivos = GestorArchivos.deListaDeURLs(archivoObjetivo.getAbsolutePath());
				} else {
					//No debería de poder accederse a este estado, pero si lo hubiera, se regresa al inicio
					System.out.println("Hubo un error, regresando al inicio");
					this.estado = Estado.ENTRADA;
				}

				etiquetasColumnas = Config.etiquetas();

				this.estado = Estado.SELEC_COLUMNAS_INTERES;
			}
			break;


			case SELEC_COLUMNAS_INTERES:
				System.out.println("Escriba las columnas de interés para filtrar");

				StringBuilder sb = new StringBuilder();

				sb.append("*").append(")").append("Todas").append(" ");
				for (int i = 0; i < etiquetasColumnas.length; i++) {
					sb.append(i).append(")").append(etiquetasColumnas[i]).append(" ");
				}
				System.out.println(sb);

				int[] columnasSeleccionadas = solicitaEnteros(0, etiquetasColumnas.length - 1);

				tipoColumnas = Config.columnas();
				bColumnasSeleccionadas = new boolean[tipoColumnas.length];
				for (int i = 0; i < tipoColumnas.length; i++) {
					bColumnasSeleccionadas[i] = Arreglos.contiene(columnasSeleccionadas, i);
				}

				if (columnasSeleccionadas.length == 0) {
					this.estado = Estado.ESPERA_HILO_MAESTRO;
				} else {
					this.estado = Estado.SELEC_COTA_INFERIOR;
				}

				break;

			case SELEC_COTA_INFERIOR:
				if (bColumnasSeleccionadas[contadorColumnaPreguntada]) {
					System.out.print("Escriba el valor mínimo de aceptación para la columna de " + etiquetasColumnas[contadorColumnaPreguntada] + ": ");
					switch (tipoColumnas[contadorColumnaPreguntada]) {
						case 'f':	acumuladorCotas.add(solicitaFecha());	break;
						case 'i':	acumuladorCotas.add(solicitaEntero());	break;
						case 'd':	acumuladorCotas.add(solicitaDoble());	break;
						case 'c':	acumuladorCotas.add(solicitaCaracter());	break;
						case 't':	acumuladorCotas.add(solicitaTexto());	break;
					}
				} else {
					acumuladorCotas.add("*");
				}

				this.estado = Estado.SELEC_COTA_SUPERIOR;
				break;

			case SELEC_COTA_SUPERIOR:
				if (bColumnasSeleccionadas[contadorColumnaPreguntada]) {
					System.out.print("Escriba el valor máximo de aceptación para la columna de " + etiquetasColumnas[contadorColumnaPreguntada] + ": ");

					switch (tipoColumnas[contadorColumnaPreguntada]) {
						case 'f': 	acumuladorCotas.add(solicitaFecha());		break;
						case 'i':	acumuladorCotas.add(solicitaEntero());		break;
						case 'd':	acumuladorCotas.add(solicitaDoble());		break;
						case 'c':	acumuladorCotas.add(solicitaCaracter());	break;
						case 't':	acumuladorCotas.add(solicitaTexto());		break;
					}
				} else {
					acumuladorCotas.add("*");
				}

				contadorColumnaPreguntada++;

				if (contadorColumnaPreguntada < tipoColumnas.length) {
					this.estado = Estado.SELEC_COTA_INFERIOR;
				} else {
					this.estado = Estado.SELEC_CANTIDAD_HILOS;
					contadorColumnaPreguntada = 0;
				}
				break;

			case SELEC_CANTIDAD_HILOS:
				System.out.println("Elija si desea utilizar un sólo hilo de ejecución o utilizar todos los disponibles");
				System.out.println("0)Uno 1)Todos");
				monoPoliHilo = solicitaEntero(0, 1);
				this.estado = Estado.ESPERA_HILO_MAESTRO;
				break;

			case ESPERA_HILO_MAESTRO: {
				Object[] cotas = new Object[acumuladorCotas.size()];

				for (int i = 0; i < cotas.length; i++) {
					cotas[i] = acumuladorCotas.get(i);
				}

				FiltroDatos filtroDatos = new FiltroDatos(cotas);

				Archivo[] archivos = this.gestorArchivos.archivos();

				Hilo hilo = null;
				if (monoPoliHilo == 0) {
					hilo = new MonoHilo(archivos, filtroDatos);
				} else {
					//if(monoPoliHilo == 1){
					hilo = new Maestro(archivos, filtroDatos);
				}

				hilo.start();

				while (hilo.estaCorriendo()) {
					try {
						sleep(500);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}

				resultados = hilo.resultados();

				System.out.println("Se obtuvieron " + resultados.length + " renglones que satisfacen el filtro");
				this.estado = Estado.RESULTADOS_LISTOS;
			}
			break;

			case RESULTADOS_LISTOS: {
				System.out.println("Seleccione una de las siguiente opciones");
				System.out.println("0)Volver a inicio  1)Estadísticos  2)Graficas  3)Guardar en archivo");
				int seleccion = solicitaEntero(0, 3);

				switch (seleccion){
					case 0: estado = Estado.ENTRADA;			 	break;
					case 1: estado = Estado.SELEC_ESTADISTICOS; 	break;
					case 2: estado = Estado.SELEC_GRAFICAS; 		break;
					case 3: estado = Estado.SELEC_GUARDADO; 		break;
				}
			}
			break;

			case SELEC_ESTADISTICOS: {
				System.out.println("¿Qué operación desearía realizar en los resultados?");
				System.out.println(
						"0)Volver a resultados  \n" +
								"1)Media  \n" +
								"2)Moda  \n" +
								"3)Mediana	\n" +
								"4)Varianza	\n" +
								"5)Desviación típica \n" +
								"6)Covarianza \n" +
								"7)Correlación de Pearson");

				int seleccion = solicitaEntero(0, 7);
				int columna1 = -1;
				int columna2 = -1;

				//Se eligen las columnas de acuerdo al tipo de cálculos que se van a hacer
				switch (seleccion){
					case 0:	this.estado = Estado.RESULTADOS_LISTOS;	break;

					//Estadísticos de una sola columna
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:
						System.out.println("¿Cuál es la columna a la que le quiere realizar los cálculos?");
						for(int i = 0; i < etiquetasColumnas.length; i++){
							if(tipoColumnas[i]=='d' || tipoColumnas[i]=='i'){
								System.out.println(i + ")" + etiquetasColumnas[i]);
							}
						}
						columna1 = solicitaColumnaNumerica();
						break;
					case 6:
					case 7:
						System.out.println("¿Cuáles son las columnas con las que quiere realizar los cálculos?");
						for(int i = 0; i < etiquetasColumnas.length; i++){
							if(tipoColumnas[i]=='d' || tipoColumnas[i]=='i'){
								System.out.println(i + ")" + etiquetasColumnas[i]);
							}
						}
						System.out.print("Columna 1: ");
						columna1 = solicitaColumnaNumerica();
						System.out.println("Columna 2: ");
						columna2 = solicitaColumnaNumerica();
						break;
				}

				if(seleccion == 0){
					break;
				}

				double[] valsColumna1 = new double[resultados.length];
				double[] valsColumna2 = new double[resultados.length];

				for(int i = 0; i < valsColumna1.length; i++){
					valsColumna1[i] = (double) resultados[i].columna(columna1);
					if(columna2 >= 0){
						valsColumna2[i] = (double) resultados[i].columna(columna2);
					}
				}

				double resultado = 0;
				//Se retornan los resultados de acuerdo al tipo de operación que se seleccionó
				switch (seleccion){
					case 1:	resultado = Estadisticas.media(valsColumna1); 		break;
					case 2:	resultado = Estadisticas.moda(valsColumna1);		break;
					case 3:	resultado = Estadisticas.mediana(valsColumna1);		break;
					case 4:	resultado = Estadisticas.varianza(valsColumna1);	break;
					case 5: resultado = Estadisticas.desviacionTipica(valsColumna1);					break;
					case 6: resultado = Estadisticas.covarianza(valsColumna1, valsColumna2);			break;
					case 7:	resultado = Estadisticas.correlacionPearson(valsColumna1, valsColumna2);	break;
				}

				System.out.println("El valor resultante es de: " + resultado);
			}
			break;

			case SELEC_GRAFICAS:{
				int columnaX = -1;
				int columnaY = -1;

				System.out.println("¿Cuáles son las columnas que quiere usar para graficación?");
				for(int i = 0; i < etiquetasColumnas.length; i++){
					if(tipoColumnas[i]=='d' || tipoColumnas[i]=='i' || tipoColumnas[i] == 'f'){
						System.out.println(i + ")" + etiquetasColumnas[i]);
					}
				}
				System.out.print("Columna X: ");
				columnaX = solicitaColumnaCantidad();
				System.out.println("Columna Y: ");
				columnaY = solicitaColumnaCantidad();

				Object[] valsColumnaX = new Object[resultados.length];
				Object[] valsColumnaY = new Object[resultados.length];

				for(int i = 0; i < valsColumnaX.length;  i++){
					valsColumnaX[i] = resultados[columnaX];
					valsColumnaY[i] = resultados[columnaY];
				}

				Graficador.dibuja(valsColumnaX, valsColumnaY);
				estado = Estado.RESULTADOS_LISTOS;
			}
			break;

			case SELEC_GUARDADO:{
				System.out.println("Escriba la dirección del archivo donde desea guardar los resultados:");
				File direccion =  solicitaArchivoNuevo();

				try{
					EscritorArchivos.guardaArchivo(direccion.getAbsolutePath(), resultados);
				}catch (IOException e){
					System.err.println("No se pudo guardar el archivo en la dirección: " + direccion.getAbsolutePath());
				}

				estado = Estado.RESULTADOS_LISTOS;
			}
			break;

		}
    }

	private String solicitaTexto() {
		return lectorConsola.next().trim();
	}

	private char solicitaCaracter() {
		return lectorConsola.next().trim().charAt(0);
	}

	private File solicitaArchivoNuevo() {
		boolean valido = false;
		File archivo;
		do{
			String dir = lectorConsola.next().trim();
			archivo = new File(dir);
			if(!archivo.exists()){
				valido = true;
			}else{
				System.err.println("Esa dirección de archivo no es válida, por favor introdúzca otra");
			}
		}while(!valido);

		return archivo;
	}


	private File solicitaArchivo(){
		boolean valido = false;
		File archivo;
		do{
			String dir = lectorConsola.next().trim();
			archivo = new File(dir);
			if(archivo.exists() && archivo.isFile()){
				valido = true;
			}else{
				System.err.println("Esa dirección de archivo no es válida, por favor introdúzca otra");
			}
		}while(!valido);

		return archivo;
	}

	private File solicitaCarpeta(){
		boolean valido = false;
		File archivo;
		do{
			String dir = lectorConsola.next().trim();
			archivo = new File(dir);
			if(archivo.exists() && archivo.isDirectory()){
				valido = true;
			}else{
				System.err.println("Esa dirección de carpeta no es válida, por favor introdúzca otra");
			}
		}while(!valido);

		return archivo;
	}

	private int solicitaColumnaCantidad(){
		boolean valido = false;
		char[] tiposColumna = Config.columnas();

		int colSelec = -1;
		do{
			String lectura = lectorConsola.next().trim();
			try{
				colSelec = Integer.parseInt(lectura);
			}catch (NumberFormatException e){
				System.out.println("Por favor introduzca un valor válido");
				continue;
			}

			valido = (tiposColumna[colSelec] == 'i' || tiposColumna[colSelec] == 'd'|| tiposColumna[colSelec] == 'f');
			if(!valido){
				System.err.println("La columna seleccionada no es de valores numéricos");
				System.out.println(Arrays.toString(Config.etiquetas()));
			}
		}while(!valido);

		return colSelec;
	}

	private int solicitaColumnaNumerica(){
		boolean valido = false;
		char[] tiposColumna = Config.columnas();

		int colSelec = -1;
		do{
			String lectura = lectorConsola.next().trim();
			try{
				colSelec = Integer.parseInt(lectura);
			}catch (NumberFormatException e){
				System.out.println("Por favor introduzca un valor válido");
				continue;
			}

			valido = (tiposColumna[colSelec] == 'i' || tiposColumna[colSelec] == 'd');
			if(!valido){
				System.err.println("La columna seleccionada no es de valores numéricos");
				System.out.println(Arrays.toString(Config.etiquetas()));
			}
		}while(!valido);

		return colSelec;
	}

	private int[] solicitaEnteros(int valInferior, int valSuperior) {
		boolean valido = false;
		int[] digeridos = new int[0];
		do{
			boolean numsValidos = true;
			String cadena = lectorConsola.next().trim();

			//Si se lee una * se regresan todos los valores posibles
			if(cadena.equals("*")){
				digeridos = new int[valSuperior - valInferior];
				for(int i = 0; i < (valSuperior - valInferior); i++){
					digeridos[i] = valInferior ++;
				}
				return digeridos;
			}

			//Sino, se leen los valores separados por comas o espacios
			String[] enteros = cadena.split("(,+\\s*|\\s+)");
			digeridos = new int[enteros.length];
			for(int i = 0; i < enteros.length; i++){
				try{
					digeridos[i] = Integer.parseInt(enteros[i]);
					numsValidos &= Matematicas.valorEntreValores(digeridos[i], valInferior, valSuperior);
				}catch (NumberFormatException e){
					numsValidos = false;
				}
			}

			if(numsValidos){
				valido = true;
			}else{
				System.err.println("Los valores no son válidos, por favor introduzca otros valores");
			}
		}while(!valido);

		return digeridos;
	}

	private Object solicitaFecha(){
		LocalDate fecha = LocalDate.MIN;
		boolean valido = false;
		do{
			try{
				String lectura = lectorConsola.next();
				if(lectura.trim().equals("*")){
					return "*";
				}else{
					fecha = LocalDate.parse(lectura);
				}
				valido = true;
			}catch (Exception e){
				System.err.println("Por favor indroduzca un valor de fecha válida (año-mes-día) aaaa-mm-dd");
			}
		}while(!valido);

		return fecha;
	}

	private Object solicitaDoble(){
		double val = 0;
		boolean valido = false;
		do{
			try{
				String lectura = lectorConsola.next();
				if(lectura.trim().equals("*")){
					return "*";
				}else{
					val = Double.parseDouble(lectura);
				}
				valido = true;
			}catch (Exception e){
				System.err.println("Por favor indroduzca un valor válido");
			}
		}while(!valido);

		return val;
	}

	private int solicitaEntero(int valInferior, int valSuperior){
		int val = 0;
		boolean valido = false;
		do{
			try{
				String lectura = lectorConsola.next();
				val = Integer.parseInt(lectura);

				valido = Matematicas.valorEntreValores(val, valInferior, valSuperior);
			}catch (NumberFormatException e){
				System.err.println("No se pudo obtener un número desde la cadena leída");
			}

			if(!valido){
				System.err.println("Por favor indroduzca un valor válido entre " + valInferior + " -> " + valSuperior);
			}
		}while(!valido);

		return val;
	}

	private Object solicitaEntero(){
		int val = 0;
		boolean valido = false;
		do{
			try{
				String lectura = lectorConsola.next();
				if(lectura.trim().equals("*")){
					return "*";
				}else{
					val = Integer.parseInt(lectura);
				}
				valido = true;
			}catch (Exception e){
				System.err.println("Por favor indroduzca un valor válido");
			}
		}while(!valido);

		return val;
	}

	public static void main(String[] args) {
		new InterfazTerminal();
	}

}
