package Interfaz;

import EstacionesDatos.GestorEstaciones;

import java.util.Scanner;

import static java.lang.Thread.sleep;

/**
 * @author Rodrigo Aragón Mureddu
 * Clase para facilitar los diferentes estados de manejo de la interfaz vía terminal
 */
public final class InterfazTerminal{

	enum Estado{
		ENTRADA,
		SELEC_CRITERIO,

		SELEC_ESTADO,
		SELEC_MUNICIPIO,

		SELEC_LATITUD_MAX,
		SELEC_LATITUD_MIN,
		SELEC_LONGITUD_MAX,
		SELEC_LONGITUD_MIN,
		SELEC_ALTITUD_MIN,
		SELEC_ALTITUD_MAX,

		SELEC_DIARIO_O_MENSUAL,
		SELEC_COLUMNA_INTERES,
		SELEC_INTERVALO_BUSQUEDA,

		ESPERA_HILO_MAESTRO,
		RESULTADOS_LISTOS,
	}

	private Estado estado;
	private boolean abierto = true;

	private final Scanner lectorConsola;
	private final GestorEstaciones gestorEstaciones;

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
				this.estado = Estado.SELEC_CRITERIO;
				break;

			case SELEC_CRITERIO:
				System.out.println("Seleccione el criterio con el que desea filtrar las estaciones");
				int[] estados = solicitaEstados();
				break;

			case SELEC_ESTADO:
				break;

			case SELEC_MUNICIPIO:
				break;

			case SELEC_LATITUD_MAX:
				break;

			case SELEC_LATITUD_MIN:
				break;

			case SELEC_LONGITUD_MAX:
				break;

			case SELEC_LONGITUD_MIN:
				break;

			case SELEC_DIARIO_O_MENSUAL:
				break;

			case SELEC_COLUMNA_INTERES:
				break;

			case ESPERA_HILO_MAESTRO:
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				break;

			case RESULTADOS_LISTOS:
				break;
		}

	}


	private int[] solicitaEstados(){
		String[] estados = gestorEstaciones.estados();
		for(int i = 1; i <= estados.length; i++){
			System.out.println(i+") " + estados[i-1]);
		}

		lectorConsola.next();

		return null;
	}

	private int solicitaEnteros(String[] valores, int[]indicesValidos){
		return 0;
	}

	private int solicitaEntero(String solicitud){
		return 0;
	}

	private double solicitaDoble(String solicitud){
		return 0;
	}
}
