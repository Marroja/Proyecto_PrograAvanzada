package EstacionesDatos;

/**
 * Clase envoltorio para los criterios de filtración al momento de solicitar los archivos que se van a descartar
 */
public final class FiltroEstacion {

	public final String estado;
	public final String municipio;
	public final double latitudInf;
	public final double latitudSup;
	public final double longitudInf;
	public final double longitudSup;
	public final double altitudInf;
	public final double altitudSup;

	FiltroEstacion(String estado, String municipio,
				   double latitudInf, double latitudSup,
				   double longitudInf, double longitudSup,
				   double altitudInf, double altitudSup){
		this.estado = estado;
		this.municipio = municipio;
		this.latitudInf = latitudInf;
		this.latitudSup = latitudSup;
		this.longitudInf = longitudInf;
		this.longitudSup = longitudSup;
		this.altitudInf = altitudInf;
		this.altitudSup = altitudSup;
	}

	@Override
	public String toString() {
		return
				"Estado: " + estado + " - " +
				"Municipio: " + municipio + " - " +
				"Latitud: " +latitudInf + " < X < " + latitudSup + " - " +
				"Longitud: " +longitudInf + " < X < " + longitudSup + " - " +
				"Altitud: " +altitudInf + " < X < " + altitudSup;
	}
}
