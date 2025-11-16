package EstacionesDatos;

import Utils.Bitacora;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;

import static Utils.Matematicas.*;

public class RenglonDatos {

	public static final String VALOR_CENTINELA = "-1234";

	public final LocalDate fecha;
	public final double precipitacion;
	public final double evaporacion;
	public final double tempMax;
	public final double tempMin;

	public static RenglonDatos digiereRenglon(String renglonLeido) throws Exception{
		return new RenglonDatos(renglonLeido);
	}

	private RenglonDatos(String renglonLeido) throws ParseException, NumberFormatException{
		renglonLeido = renglonLeido.replace("NULO", VALOR_CENTINELA);
		String[] columnas = renglonLeido.split("\\s+", 5);

		this.fecha = LocalDate.parse(columnas[0]);
		this.precipitacion = Double.parseDouble(columnas[1]);
		this.evaporacion = Double.parseDouble(columnas[2]);
		this.tempMax = Double.parseDouble(columnas[3]);
		this.tempMin = Double.parseDouble(columnas[4]);
	}

	@Override
	public String toString() {
		return this.fecha.toString() + " "
				+ precipitacion + " " +
				+ evaporacion + " " +
				+ tempMax + " " +
				+ tempMin;
	}

	public boolean cumpleCriterio(FiltroDatos filtroDatos) {
		boolean cumple = true;

		cumple &= fechaEntreFechas(this.fecha, filtroDatos.fechaIni, filtroDatos.fechaFin);
		cumple &= valorEntreValores(this.precipitacion, filtroDatos.precipSup, filtroDatos.precipInf);
		cumple &= valorEntreValores(this.evaporacion, filtroDatos.evapSup, filtroDatos.evapInf);
		cumple &= valorEntreValores(this.tempMax, filtroDatos.tempMaxSup, filtroDatos.tempMaxInf);
		cumple &= valorEntreValores(this.tempMin, filtroDatos.tempMinSup, filtroDatos.tempMinInf);

		return cumple;
	}

	public static void main(String[] args) throws Exception {
		String s = digiereRenglon("1982-02-16\t0\t4.81\t25.6\t1.4").toString();
		System.out.println(s);
	}
}
