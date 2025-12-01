package ArchivosDatos;

import Utils.Config;
import Utils.Matematicas.*;

import java.time.LocalDate;
import java.util.Arrays;

import static Utils.Matematicas.fechaEntreFechas;
import static Utils.Matematicas.valorEntreValores;

/**
 * Clase envoltorio para los criterios de filtración de datos
 */
public class FiltroDatos{

	private final Object[] cotasSuperiores;
	private final Object[] cotasInferiores;

	private static final LocalDate FECHA_MAX = LocalDate.MAX;
	private static final LocalDate FECHA_MIN = LocalDate.MIN;
	private static final double D_MAX = Double.MAX_VALUE;
	private static final double D_MIN = -Double.MAX_VALUE;
	private static final int I_MAX = Integer.MAX_VALUE;
	private static final int I_MIN = Integer.MIN_VALUE;
	private static final char C_PREDET = '.';
	private static final String T_PREDET = ".*";

	public FiltroDatos(Object[] cotas){
		char[] tiposColumna = Config.columnas();
		cotasInferiores = new Object[tiposColumna.length];
		cotasSuperiores = new Object[tiposColumna.length];

		//Primero se llenan todas las cotas con los valores extremos
		for(int i = 0; i < tiposColumna.length; i++){
			switch (tiposColumna[i]){
				case 'f':
						cotasInferiores[i] = FECHA_MIN;
						cotasSuperiores[i] = FECHA_MAX;
					break;
				case 'i':
						cotasInferiores[i] = I_MIN;
						cotasSuperiores[i] = I_MAX;
					break;
				case 'd':
						cotasInferiores[i] = D_MIN;
						cotasSuperiores[i] = D_MAX;
					break;
				case 'c':
						cotasInferiores[i] = C_PREDET;
						cotasSuperiores[i] = C_PREDET;
					break;
				case 't':
						cotasInferiores[i] = T_PREDET;
						cotasSuperiores[i] = T_PREDET;
					break;
			}
		}

		//Y si se recibe un valor que no sea "*" entonces se toma el valor de la cota dado
		for(int i = 0; i < tiposColumna.length; i++){
			cotasInferiores[i] = (cotas[i*2].equals("*")) ? cotasInferiores[i] : cotas[i*2];
			cotasSuperiores[i] = (cotas[i*2+1].equals("*")) ? cotasSuperiores[i] : cotas[i*2+1];
		}
	}

	public boolean cumpleCriterio(RenglonDatos renglonDatos) throws ClassCastException{
		char[] tiposColumna = Config.columnas();
		Object[] columnas = renglonDatos.columnas();

		boolean cumple = true;

		for(int i = 0; i < columnas.length; i++){
			switch (tiposColumna[i]){
				case 'f':
					cumple &= fechaEntreFechas((LocalDate) columnas[i], (LocalDate) cotasSuperiores[i], (LocalDate)cotasInferiores[i]);
					break;
				case 'd':
					cumple &= valorEntreValores((Double)columnas[i], (Double) cotasSuperiores[i], (Double) cotasInferiores[i]);
					break;
				case 'i':
					cumple &= valorEntreValores((Integer)columnas[i], (Integer) cotasSuperiores[i], (Integer) cotasInferiores[i]);
					break;
				case 'c':
					cumple &= ((Character) columnas[i]).equals((Character) cotasSuperiores[i]) ;
					break;
				case 't':
					cumple &= ((String) columnas[i]).equals((String) cotasSuperiores[i]);
					break;
			}
		}

		return cumple;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Filtro: ");
		for(int i = 0; i < cotasInferiores.length; i++){
			sb.append("[").append(cotasInferiores[i]).append(" < X < ").append(cotasSuperiores[i]).append("] ");
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		System.out.println("--Ejemplo de filtro de datos--");
		LocalDate fechaInf = LocalDate.MIN;
		LocalDate fechaMax = LocalDate.MAX;
		double precipMin = 0.0;
		double precipMax = 100.0;
		double evapMin = 0.0;
		double evapMax = 100.0;
		String tempMin = "*";
		String tempMax = "*";
		double tempMinMin = 0.0;
		double tempMinMax = 100.0;

		FiltroDatos f = new FiltroDatos(new Object[]{
				fechaInf, fechaMax,
				precipMin, precipMax,
				evapMin, evapMax,
				tempMin, tempMax,
				tempMinMin, tempMinMax
		});

		System.out.println(f);
	}
}
