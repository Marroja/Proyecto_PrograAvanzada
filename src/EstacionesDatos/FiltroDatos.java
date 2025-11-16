package EstacionesDatos;

import java.time.LocalDate;

public class FiltroDatos{

	public final LocalDate fechaIni;
	public final LocalDate fechaFin;
	public final double precipInf;
	public final double precipSup;
	public final double evapInf;
	public final double evapSup;
	public final double tempMaxInf;
	public final double tempMaxSup;
	public final double tempMinInf;
	public final double tempMinSup;

	public FiltroDatos(
			LocalDate fechaIni, LocalDate fechaFin,
			double precipInf, double precipSup,
			double evapInf, double evapSup,
			double tempMaxInf, double tempMaxSup,
			double tempMinInf, double tempMinSup){
		this.fechaIni = fechaIni;
		this.fechaFin = fechaFin;
		this.precipInf = precipInf;
		this.precipSup = precipSup;
		this.evapInf = evapInf;
		this.evapSup = evapSup;
		this.tempMaxInf = tempMaxInf;
		this.tempMaxSup = tempMaxSup;
		this.tempMinInf = tempMinInf;
		this.tempMinSup = tempMinSup;
	}

	@Override
	public String toString() {
		return "FiltroDatos{" +
				"fechaIni=" + fechaIni +
				", fechaFin=" + fechaFin +
				", precipInf=" + precipInf +
				", precipSup=" + precipSup +
				", evapInf=" + evapInf +
				", evapSup=" + evapSup +
				", tempMaxInf=" + tempMaxInf +
				", tempMaxSup=" + tempMaxSup +
				", tempMinInf=" + tempMinInf +
				", tempMinSup=" + tempMinSup +
				'}';
	}
}
