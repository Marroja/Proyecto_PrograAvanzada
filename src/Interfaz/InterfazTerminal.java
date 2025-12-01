package Interfaz;

import ArchivosDatos.*;
import Hilos.Hilo;
import Hilos.MonoHilo.MonoHilo;
import Hilos.PoliHilo.Maestro;
import Utils.Arreglos;
import Utils.Config;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import Utils.Matematicas;
import Visualizacion.GraficadorSerieTiempo;
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
	}

	private Estado estado;
	private boolean abierto = true;

	private final Scanner lectorConsola;
	private GestorArchivos gestorArchivos;

	private char[] tipoColumnas;

	private int monoPoliHilo = 0;
	private int localRemoto = 0;
	private File archivoObjetivo = null;

	private boolean[] bColumnasSeleccionadas;
	private ArrayList<Object> acumuladorCotas = new ArrayList<>();
	private String[] etiquetasColumnas = new String[0];
	private int contadorColumnaPreguntada = 0;
	private FiltroDatos filtroDatos = null;

	private Object[] cotas = null;

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

			case SELEC_DIR_ARCHIVOS:
                if(localRemoto == LOCAL){
					System.out.println("Escriba la dirección de la carpeta donde se encuentran los archivos CSV");
					System.out.println("(Predet. " + Config.dirCarpeta() + ")");

					archivoObjetivo = solicitaCarpeta();
					gestorArchivos = GestorArchivos.deCarpeta(archivoObjetivo.getAbsolutePath());
				}else
				if(localRemoto == REMOTO){
					System.out.println("Escriba la dirección del archivo que contenga los URLs a consultar");
					System.out.println("(Predet. " + Config.dirURLs() + ")");

					archivoObjetivo = solicitaArchivo();
					gestorArchivos = GestorArchivos.deListaDeURLs(archivoObjetivo.getAbsolutePath());
				}else{
					//No debería de poder accederse a este estado, pero si lo hubiera, se regresa al inicio
					System.out.println("Hubo un error, regresando al inicio");
					this.estado = Estado.ENTRADA;
				}

				etiquetasColumnas = Config.etiquetas();

				this.estado = Estado.SELEC_COLUMNAS_INTERES;
				break;


            case SELEC_COLUMNAS_INTERES:
                System.out.println("Escriba las columnas de interés para filtrar");

				StringBuilder sb = new StringBuilder();

				sb.append("*").append(")").append("Todas").append(" ");
				for(int i = 0; i < etiquetasColumnas.length; i++){
					sb.append(i).append(")").append(etiquetasColumnas[i]).append(" ");
				}
				System.out.println(sb);

				int[] columnasSeleccionadas = solicitaEnteros(0, etiquetasColumnas.length - 1);

				tipoColumnas = Config.columnas();
				bColumnasSeleccionadas = new boolean[tipoColumnas.length];
				for(int i = 0; i < tipoColumnas.length; i++){
					bColumnasSeleccionadas[i] = Arreglos.contiene(columnasSeleccionadas, i);
				}

				System.out.println(Arrays.toString(bColumnasSeleccionadas));

				if (columnasSeleccionadas.length == 0) {
                    this.estado = Estado.ESPERA_HILO_MAESTRO;
                } else {
                    this.estado = Estado.SELEC_COTA_INFERIOR;
                }

                break;

            case SELEC_COTA_INFERIOR:
				if(bColumnasSeleccionadas[contadorColumnaPreguntada]){
					System.out.print("Escriba el valor mínimo de aceptación para la columna de " + etiquetasColumnas[contadorColumnaPreguntada] +": ");
					switch (tipoColumnas[contadorColumnaPreguntada]){
						case 'f':
							acumuladorCotas.add(solicitaFecha());
							break;
						case 'i':
							acumuladorCotas.add(solicitaEntero());
							break;
						case 'd':
							acumuladorCotas.add(solicitaDoble());
							break;
						case 'c':
							acumuladorCotas.add(solicitaCaracter());
							break;
						case 't':
							acumuladorCotas.add(solicitaTexto());
							break;
					}
				}else{
					acumuladorCotas.add("*");
				}

                this.estado = Estado.SELEC_COTA_SUPERIOR;
                break;

            case SELEC_COTA_SUPERIOR:
				if(bColumnasSeleccionadas[contadorColumnaPreguntada]){
					System.out.print("Escriba el valor máximo de aceptación para la columna de " + etiquetasColumnas[contadorColumnaPreguntada] + ": ");

					switch (tipoColumnas[contadorColumnaPreguntada]){
						case 'f':
							acumuladorCotas.add(solicitaFecha());
							break;
						case 'i':
							acumuladorCotas.add(solicitaEntero());
							break;
						case 'd':
							acumuladorCotas.add(solicitaDoble());
							break;
						case 'c':
							acumuladorCotas.add(solicitaCaracter());
							break;
						case 't':
							acumuladorCotas.add(solicitaTexto());
							break;
					}
				}else{
					acumuladorCotas.add("*");
				}

				contadorColumnaPreguntada ++;

				if(contadorColumnaPreguntada < tipoColumnas.length){
					this.estado = Estado.SELEC_COTA_INFERIOR;
				}
				else{
					this.estado = Estado.SELEC_CANTIDAD_HILOS;
				}
				break;

			case SELEC_CANTIDAD_HILOS:
				System.out.println("Elija si desea utilizar un sólo hilo de ejecución o utilizar todos los disponibles");
				System.out.println("0)Uno 1)Todos");
				monoPoliHilo = solicitaEntero(0, 1);
				this.estado = Estado.ESPERA_HILO_MAESTRO;
				break;

            case ESPERA_HILO_MAESTRO:

				cotas = new Object[acumuladorCotas.size()];

				for(int i = 0; i < cotas.length; i++){
					cotas[i] = acumuladorCotas.get(i);
				}

                filtroDatos = new FiltroDatos(cotas);

                Archivo[] archivos = this.gestorArchivos.archivos();

				Hilo hilo = null;
				if(monoPoliHilo == 0){
					hilo = new MonoHilo(archivos, filtroDatos);
				}else{
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
                this.estado = Estado.RESULTADOS_LISTOS;
                break;

            case RESULTADOS_LISTOS:
				System.out.println("Seleccione una de las siguiente opciones");
                System.out.println("0)Volver a inicio  1)");
                for (int i = 0; i < resultados.length; i++) {
                    System.out.println(resultados[i]);
                }


                this.estado = Estado.ENTRADA;
                break;


        }
    }

	private Object solicitaTexto() {
		//qph
		return "";
	}

	private Object solicitaCaracter() {
		//qph
		return '*';
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
