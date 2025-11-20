package EstacionesDatos;

import java.text.ParseException;
import java.time.LocalDate;

import static Utils.Matematicas.*;

public class RenglonDatos {

	//qph traer el nombre, el estado y el municipio al renglón de datos para su escritura al final
	public static final String VALOR_CENTINELA = "-1234";

	public final LocalDate fecha;
	public final double precipitacion;
	public final double evaporacion;
	public final double tempMax;
	public final double tempMin;

	//qph tenemos que poder elegir cuál será el caracter de separación para que no sólo sean tabuladores
	public static RenglonDatos digiereRenglon(String renglonLeido) throws Exception{
		return new RenglonDatos(renglonLeido);
	}

	//OJO la clase es privada para que SIEMPRE se genere un renglón a partir del método estático de arriba
	private RenglonDatos(String renglonLeido) throws ParseException, NumberFormatException{
		renglonLeido = renglonLeido.replace("NULO", VALOR_CENTINELA);
		String[] columnas = renglonLeido.split("\\s+");	// "\\s+" indica espacio blanco (habrá que generalizar)

		//qph tenemos que generalizar esta parte para N columnas
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

	//qph necesitamos generalizar esto para que el filtro no sólo sea de 5 columnas sino de n columnas
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
		RenglonDatos r = digiereRenglon("1982-02-16\t0\t4.81\t25.6\t1.4");
		System.out.println(r.toString());
	}
}
