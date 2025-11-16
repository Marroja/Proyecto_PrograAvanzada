package Utils;

import java.time.LocalDate;
import java.util.ArrayList;

public final class Matematicas {

	public static boolean valorEntreValores(double valPrueba, double val1, double val2){
		double max = Math.max(val1, val2);
		double min = Math.min(val1, val2);

		return (valPrueba >= min && valPrueba <= max);
	}

	public static boolean fechaEntreFechas(LocalDate fechaPrueba, LocalDate fecha1, LocalDate fecha2){
		LocalDate max = fecha1.isAfter(fecha2) ? fecha1 : fecha2;
		LocalDate min = fecha1.isAfter(fecha2) ? fecha2 : fecha1;

		return (fechaPrueba.isAfter(min) && max.isAfter(fechaPrueba));
	}

	public static int[] enterosEnCadena(String cadena){
		String[] separados = cadena.replace(",","").split("\\s+");
		ArrayList<Integer> enteros = new ArrayList<>();
		for(int i = 0; i < separados.length; i++){
			try{
				enteros.add(Integer.parseInt(separados[i]));
			}catch (NumberFormatException e){

			}
		}
		int[] valsRetorno = new int[enteros.size()];
		for(int i = 0; i < valsRetorno.length; i++){
			valsRetorno[i] = enteros.get(i);
		}

		return valsRetorno;
	}


}
