package Utils;

import java.time.LocalDate;

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

}
