package Interfaz;

import EstacionesDatos.*;
import Hilos.MonoHilo.MonoHilo;
import Hilos.PoliHilo.Maestro;
import Utils.Matematicas;
import sun.awt.XSettings;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static java.lang.Thread.getDefaultUncaughtExceptionHandler;
import static java.lang.Thread.sleep;

/**
 * @author Rodrigo Aragón Mureddu
 * Clase para facilitar los diferentes estados de manejo de la interfaz vía terminal
 */
public final class InterfazTerminal{

	enum Estado{
		ENTRADA,

		SELEC_ESTADO,
		SELEC_MUNICIPIO,

		SELEC_LATITUD_MAX,
		SELEC_LATITUD_MIN,
		SELEC_LONGITUD_MAX,
		SELEC_LONGITUD_MIN,
		SELEC_ALTITUD_MIN,
		SELEC_ALTITUD_MAX,
		SELEC_FECHA_MAX,
		SELEC_FECHA_MIN,

		SELEC_COLUMNA_INTERES,
		SELEC_COTA_INFERIOR,
		SELEC_COTA_SUPERIOR,

		ESPERA_HILO_MAESTRO,
		RESULTADOS_LISTOS,
	}

	private Estado estado;
	private boolean abierto = true;

	private final Scanner lectorConsola;
	private final GestorEstaciones gestorEstaciones;

	private FiltroEstacion filtroEstacion = null;
	private FiltroDatos filtroDatos = null;

	private int columnaSeleccionada = 0;

	private RenglonDatos[] resultados;

	private String[] estados = new String[0];
	private String[] municipios = new String[0];
	private LocalDate fechaIni = LocalDate.MIN;
	private LocalDate fechaFin = LocalDate.MAX;
	private double latitudSup = Double.MAX_VALUE;
	private double latitudInf = -Double.MAX_VALUE;
	private double longitudSup = Double.MAX_VALUE;
	private double longitudInf = -Double.MAX_VALUE;
	private double altitudSup = Double.MAX_VALUE;
	private double altitudInf = -Double.MAX_VALUE;
	private double maxTempInf = -Double.MAX_VALUE;
	private double maxTempSup = Double.MAX_VALUE;
	private double minTempInf = -Double.MAX_VALUE;
	private double minTempSup = Double.MAX_VALUE;
	private double precipInf = -Double.MAX_VALUE;
	private double precipSup = Double.MAX_VALUE;
	private double evapInf = -Double.MAX_VALUE;
	private double evapSup = Double.MAX_VALUE;
	private double cotaInferior = -Double.MAX_VALUE;
	private double cotaSuperior = Double.MAX_VALUE;

	public InterfazTerminal(){
		gestorEstaciones = new GestorEstaciones();
		lectorConsola = new Scanner(System.in);

		this.estado = Estado.ENTRADA;
		while(abierto){
			this.maquinaEstados();
		}
	}

	public void maquinaEstados() {
		switch (estado){
			case ENTRADA:
				System.out.println("Bienvenido al analizador de base de datos de CONAGUA");
				this.estado = Estado.SELEC_ESTADO;
				break;

			case SELEC_ESTADO:
				System.out.println("Seleccione el criterio con el que desea filtrar las estaciones");
				estados = solicitaEstados();

				reiniciaValoresAceptacion();

				if(estados[0].equals("TODO")){
					this.estados = gestorEstaciones.estados();
					this.municipios = gestorEstaciones.municipios(estados);
					this.estado = Estado.ESPERA_HILO_MAESTRO;
					break;
				}
				municipios = new String[0];
				this.estado = Estado.SELEC_MUNICIPIO;
				break;

			case SELEC_MUNICIPIO:
				System.out.println("Seleccione los municipios que desea para los estados");
				municipios = solicitaMunicipios(estados);
				this.estado = Estado.SELEC_LATITUD_MAX;
				break;

			case SELEC_LATITUD_MAX:
				System.out.println("Escriba la latitud máxima");
				latitudSup = solicitaDoble(Double.MAX_VALUE);
				this.estado = Estado.SELEC_LATITUD_MIN;
				break;

			case SELEC_LATITUD_MIN:
				System.out.println("Escriba la latitud mínima");
				latitudInf = solicitaDoble(-Double.MAX_VALUE);
				this.estado = Estado.SELEC_LONGITUD_MAX;
				break;

			case SELEC_LONGITUD_MAX:
				System.out.println("Escriba la longitud máxima");
				longitudSup = solicitaDoble(Double.MAX_VALUE);
				this.estado = Estado.SELEC_LONGITUD_MIN;
				break;

			case SELEC_LONGITUD_MIN:
				System.out.println("Escriba la longitud máxima");
				longitudInf = solicitaDoble(-Double.MAX_VALUE);
				this.estado = Estado.SELEC_COLUMNA_INTERES;
				break;

			case SELEC_ALTITUD_MAX:
				System.out.println("Escriba la altitud máxima");
				altitudSup = solicitaDoble(Double.MAX_VALUE);
				this.estado = Estado.SELEC_LONGITUD_MIN;
				break;

			case SELEC_ALTITUD_MIN:
				System.out.println("Escriba la altitud mínima");
				altitudInf = solicitaDoble(-Double.MAX_VALUE);
				this.estado = Estado.SELEC_FECHA_MAX;
				break;

			case SELEC_FECHA_MAX:
				System.out.println("Escriba la fecha más tardía");
				fechaFin = solicitaFecha(LocalDate.MAX);
				this.estado = Estado.SELEC_FECHA_MIN;
				break;

			case SELEC_FECHA_MIN:
				System.out.println("Escriba la fecha más tardía");
				fechaIni = solicitaFecha(LocalDate.MIN);
				this.estado = Estado.SELEC_COLUMNA_INTERES;
				break;

			case SELEC_COLUMNA_INTERES:
				System.out.println("Escriba las columans de interés");
				System.out.println("0)Ninguna 1)Precipitación 2)Evaporación 3)TMax 4)TMin ");
				columnaSeleccionada = solicitaEntero(0, 4);
				if(columnaSeleccionada == 0){
					this.estado = Estado.ESPERA_HILO_MAESTRO;
				}else{
					this.estado = Estado.SELEC_COTA_INFERIOR;
				}
				break;

			case SELEC_COTA_INFERIOR:
				System.out.println("Escriba el valor mínimo");
				cotaInferior = solicitaDoble(-Double.MAX_VALUE);
				this.estado = Estado.SELEC_COTA_SUPERIOR;
				break;

			case SELEC_COTA_SUPERIOR:
				System.out.println("Escriba el valor máximo");
				cotaSuperior = solicitaDoble(Double.MAX_VALUE);
				this.estado = Estado.ESPERA_HILO_MAESTRO;
				break;

			case ESPERA_HILO_MAESTRO:

				filtroEstacion = new FiltroEstacion(estados, municipios,
													latitudInf, latitudSup,
													longitudInf, longitudSup,
													altitudInf, altitudSup);

				gestorEstaciones.filtraEstaciones(filtroEstacion);

				switch (columnaSeleccionada){
					case 1:
						precipSup = cotaSuperior;
						precipInf = cotaInferior;
						break;
					case 2:
						evapSup = cotaSuperior;
						evapInf = cotaInferior;
						break;
					case 3:
						maxTempSup = cotaSuperior;
						maxTempInf = cotaInferior;
						break;
					case 4:
						minTempSup = cotaSuperior;
						minTempInf = cotaInferior;
						break;
				}

				filtroDatos = new FiltroDatos(	fechaIni, fechaFin,
												precipInf, precipSup,
												evapInf, evapSup,
												maxTempInf, maxTempSup,
												minTempInf, minTempSup);

				Estacion[] estaciones = gestorEstaciones.estacionesFiltradas();
				Maestro hilo = new Maestro(estaciones, filtroDatos);
				//MonoHilo hilo = new MonoHilo(estaciones, filtroDatos);
				hilo.start();

				while(hilo.estaCorriendo()){
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}

				resultados = hilo.resultados();
				this.estado = Estado.RESULTADOS_LISTOS;
				break;

			case RESULTADOS_LISTOS:
				System.out.println("----------Resultados----------");
				for(int i = 0; i < resultados.length; i++){
					System.out.println(resultados[i]);
				}
				this.estado = Estado.ENTRADA;
				break;
		}

	}

	private void reiniciaValoresAceptacion() {
		//Se reinician los máximos de aceptación
		fechaIni = LocalDate.MIN;
		fechaFin = LocalDate.MAX;
		latitudSup = Double.MAX_VALUE;
		latitudInf = -Double.MAX_VALUE;
		longitudSup = Double.MAX_VALUE;
		longitudInf = -Double.MAX_VALUE;
		altitudSup = Double.MAX_VALUE;
		altitudInf = -Double.MAX_VALUE;
		maxTempInf = -Double.MAX_VALUE;
		maxTempSup = Double.MAX_VALUE;
		minTempInf = -Double.MAX_VALUE;
		minTempSup = Double.MAX_VALUE;
		precipInf = -Double.MAX_VALUE;
		precipSup = Double.MAX_VALUE;
		evapInf = -Double.MAX_VALUE;
		evapSup = Double.MAX_VALUE;
		cotaInferior = -Double.MAX_VALUE;
		cotaSuperior = Double.MAX_VALUE;
	}


	private String[] solicitaEstados(){
		String[] estados = gestorEstaciones.estados();
		for(int i = 0; i < estados.length; i++){
			System.out.println(i+") " + estados[i]);
		}

		String entrada = "";
		String[] iEstados = new String[0];

		do{
			entrada = lectorConsola.next();
			if(entrada.trim().equals("**")){
				return new String[]{"TODO"};
			}
			if(entrada.trim().equals("*")){
				iEstados = new String[estados.length];
				for(int i = 0; i < estados.length; i++){
					iEstados[i] = estados[i];
				}
			}else{
				int[] indices = Matematicas.enterosEnCadena(entrada);
				iEstados = new String[indices.length];
				for(int i = 0; i < indices.length; i++){
					iEstados[i] = estados[indices[i]];
				}
			}

			if(iEstados.length == 0){
				System.err.println("Por favor introduzca valores válidos");
			}
		}while(iEstados.length < 1);

		return iEstados;
	}

	private String[] solicitaMunicipios(String[] estados){
		String[] nombresEstados = gestorEstaciones.estados();

		System.out.println(Arrays.toString(estados));
		String[] municipios = new String[0];

		String entrada;
		ArrayList<String> listaMunicipiosEstado = new ArrayList<>();
		ArrayList<String> listaMunicipios = new ArrayList<String>();

 		for(int i = 0; i < estados.length; i++){
			System.out.println("Seleccione los municipios para el estado de " + estados[i]);
			String[] nombresMunicipios = gestorEstaciones.municipios(estados[i]);
			for(int j = 0; j < nombresMunicipios.length; j++){
				System.out.println(j +") " + nombresMunicipios[j]);
			}

			do{
				entrada = lectorConsola.next();

				if(entrada.equals("*")){
					for(int k = 0; k < nombresMunicipios.length; k++){
						listaMunicipiosEstado.add(nombresMunicipios[k]);
					}
				}else{
					int[] indices = Matematicas.enterosEnCadena(entrada);

					for(int k = 0; k < indices.length; k++){
						listaMunicipiosEstado.add(nombresMunicipios[indices[k]]);
					}

					if(listaMunicipiosEstado.isEmpty()){
						System.err.println("Por favor introduzca valores válidos");
					}
				}
			}while(listaMunicipiosEstado.isEmpty());

			listaMunicipios.addAll(listaMunicipiosEstado);
			listaMunicipiosEstado.clear();
		}

		 municipios = new String[listaMunicipios.size()];
		 for(int i = 0; i < listaMunicipios.size(); i++){
			 municipios[i] = listaMunicipios.get(i);
		 }

		 return municipios;
	}

	private LocalDate solicitaFecha(LocalDate fechaPredet){
		LocalDate fecha = LocalDate.MIN;
		boolean valido = false;
		do{
			try{
				String lectura = lectorConsola.next();
				if(lectura.trim().equals("*")){
					fecha = fechaPredet;
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

	private double solicitaDoble(double valorPredet){
		double val = 0;
		boolean valido = false;
		do{
			try{
				String lectura = lectorConsola.next();
				if(lectura.trim().equals("*")){
					val = valorPredet;
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

				if(val >= valInferior && val <= valSuperior){
					valido = true;
				}
			}catch (Exception e){
				System.err.println("Por favor indroduzca un valor válido entre " + valInferior + " -> " + valSuperior);
			}
		}while(!valido);

		return val;
	}

	private double solicitaEntero(int valorPredet){
		int val = 0;
		boolean valido = false;
		do{
			try{
				String lectura = lectorConsola.next();
				if(lectura.trim().equals("*")){
					val = valorPredet;
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


}
