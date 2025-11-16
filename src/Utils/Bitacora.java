package Utils;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Clase envoltorio utilizada para registrar los posibles errores al momento de ejecutar el programa
 * @author Rodrigo Aragón Mureddu
 */
public final class Bitacora {

	private static final String dirBitacoraA = "bitacora.txt";
	private static final String dirBitacoraE = "bitacoraErrores.txt";

	/**
	 * La clase Utils.Bitacora no debe ser instanciada
	 */
	private Bitacora(){

	}

	/**
	 * Método utilizado para escribir en la bitácora de errores y excepciones "bitacoraErrores.txt",
	 * ahí se registra cualquier error de comportamiento al momento de abrir, cerrar o escribir archivos.
	 *
	 * @param mensaje
	 * Se imprime el mensaje de la expeción que se desea registrar
	 *
	 */
	public synchronized static void reportaExcepcion(String mensaje){
		char[] fecha = LocalDateTime.now().toString().toCharArray();
		char[] hora = LocalTime.now().toString().toCharArray();
		char[] comoChar = mensaje.toCharArray();

		try(FileOutputStream escritorBitacora = new FileOutputStream(dirBitacoraE, true)){
			for(int i = 0; i < fecha.length; i++){
				escritorBitacora.write(fecha[i]);
			}
			escritorBitacora.write('[');
			for(int i = 0; i < hora.length; i++){
				escritorBitacora.write(hora[i]);
			}
			escritorBitacora.write(']');
			escritorBitacora.write(':');
			escritorBitacora.write(' ');
			for(int i = 0; i < comoChar.length; i++){
				escritorBitacora.write(comoChar[i]);
			}
			escritorBitacora.write('\r');
		}catch (IOException ee){
			System.err.println("ERROR: No se pudo registrar excepción en bitácora");
			System.err.println("El programa terminará");
		}
	}

	/**
	 * Método utilizado para escribir en el archivo de "bitacora.txt" en la cual se registran todos
	 * los movimientos comunes, como apertura, descarga y cierre de archivos leídos.
	 *
	 * @param mensaje
	 * Mensaje que se desea registrar en la bitácora
	 */
	public synchronized  static void reportaMovimiento(String mensaje){
		char[] fecha = LocalDate.now().toString().toCharArray();
		char[] hora = LocalTime.now().toString().toCharArray();
		char[] comoChar = mensaje.toCharArray();

		try(FileOutputStream escritorBitacora = new FileOutputStream(dirBitacoraA, true)){

			for(int i = 0; i < fecha.length; i++){
				escritorBitacora.write(fecha[i]);
			}
			escritorBitacora.write('[');
			for(int i = 0; i < hora.length; i++){
				escritorBitacora.write(hora[i]);
			}
			escritorBitacora.write(']');
			escritorBitacora.write(':');
			escritorBitacora.write(' ');
			for(int i = 0; i < comoChar.length; i++){
				escritorBitacora.write(comoChar[i]);
			}
			escritorBitacora.write('\r');
		}catch (IOException ee){
			System.err.println("ERROR: No se pudo registrar movimiento de información en bitácora");
			System.err.println("El programa terminará");
		}
	}
}
