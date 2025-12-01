package ArchivosDatos;

import Utils.Bitacora;
import Utils.LectorArchivos;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class GestorArchivos {

	private final Archivo[] archivos;

	public static GestorArchivos deCarpeta(String direccionCarpeta){
		String[] dirs = LectorArchivos.leerArchivosEnCarpeta(direccionCarpeta);
		ArrayList<Archivo> acumArchivos = new ArrayList<>();

		if(dirs == null){
			Bitacora.reportaExcepcion("La carpeta de disco esta vacia o no existe: " + direccionCarpeta);
			return new GestorArchivos(new Archivo[]{});
		}
		try{
			for(int i = 0; i < dirs.length; i++){
				acumArchivos.add(Archivo.deDisco(dirs[i]));
			}
		}catch (NullPointerException e){
			Bitacora.reportaExcepcion("Error abriendo carpeta: " + direccionCarpeta);
			return new GestorArchivos(new Archivo[]{});
		}catch (FileNotFoundException e) {
			Bitacora.reportaExcepcion("Error abriendo el archivo: " + e.getMessage());
		}

		Archivo[] archivosDisco = new Archivo[acumArchivos.size()];
		for(int i = 0; i < acumArchivos.size(); i++){
			archivosDisco[i] = acumArchivos.get(i);
		}

		return new GestorArchivos(archivosDisco);
	}

	public static GestorArchivos deListaDeURLs(String direccionArchivo){
		String[] urls = LectorArchivos.leerArchivoCompleto(direccionArchivo);
		ArrayList<Archivo> acumArchivos = new ArrayList<>();

		for(int i = 0; i < urls.length; i++){
			try {
				acumArchivos.add(Archivo.deURL(urls[i]));
			} catch (MalformedURLException e) {
				Bitacora.reportaExcepcion("Error estableciendo conexion con el URL: " + urls[i]);
			}
		}

		Archivo[] archivosURL = new Archivo[acumArchivos.size()];
		for(int i = 0; i < archivosURL.length; i++){
			archivosURL[i] = acumArchivos.get(i);
		}

		return new GestorArchivos(archivosURL);
	}

	private GestorArchivos(Archivo[] estaciones) {
		this.archivos = estaciones;
	}


	public Archivo[] archivos(){
		return this.archivos;
	}

	public static void main(String[] args) {
		GestorArchivos gConagua = GestorArchivos.deListaDeURLs("Proyecto_PrograAvanzada/recursos/urls.txt");
		for(int i = 0; i < gConagua.archivos.length; i++){
			System.out.println(gConagua.archivos[i]);
		}

		GestorArchivos gLocal = GestorArchivos.deCarpeta("Proyecto_PrograAvanzada/recursos/archivos");
		for(int i = 0; i < gLocal.archivos.length; i++){
			System.out.println(gLocal.archivos[i]);
		}
	}

}
