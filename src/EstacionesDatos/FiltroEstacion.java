package EstacionesDatos;

import java.util.Arrays;

/**
 * Clase envoltorio para los criterios de filtración al momento de solicitar los archivos que se van a descartar
 */
public final class FiltroEstacion {

	public final String[] estados;
	public final String[] municipios;
	public final double latitudInf;
	public final double latitudSup;
	public final double longitudInf;
	public final double longitudSup;
	public final double altitudInf;
	public final double altitudSup;

	public FiltroEstacion(String[] estados, String[] municipios,
						  double latitudInf, double latitudSup,
						  double longitudInf, double longitudSup,
						  double altitudInf, double altitudSup){
		this.estados = estados;
		this.municipios = municipios;
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
				"Estados: " + Arrays.toString(estados) + " - " +
				"Municipio: " + Arrays.toString(municipios) + " - " +
				"Latitud: " +latitudInf + " < X < " + latitudSup + " - " +
				"Longitud: " +longitudInf + " < X < " + longitudSup + " - " +
				"Altitud: " +altitudInf + " < X < " + altitudSup;
	}
}
