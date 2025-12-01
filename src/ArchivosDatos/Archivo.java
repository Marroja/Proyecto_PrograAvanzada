package ArchivosDatos;

import Utils.Bitacora;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class Archivo {

	//tenemos que poder tener un número arbitrario de columnas

	//Estado, municipio, latitud, longitud y altitud NO forman parte de la base de datos, son filtros para elegir
	//las estaciones -antes- de consultar la base de datos per-se.

	private final String nombre;
	private final URL url;
	private final File archivo;

	public static Archivo deURL(String url) throws MalformedURLException {
		return new Archivo(new URL(url));
	}

	public static Archivo deDisco(String direccion) throws FileNotFoundException {
		File archivo  = new File(direccion);
		if(archivo.exists()){
			return new Archivo(archivo);
		}
		throw new FileNotFoundException(direccion);
	}

	private Archivo(URL url){
		this.url = url;
		this.archivo = null;
		this.nombre = url.getFile();
	}

	private Archivo(File archivo){
		this.archivo = archivo;
		this.url = null;
		this.nombre = archivo.getName();
	}

	public AnalizadorArchivo generaAnalizador(){
		try{
			return new AnalizadorArchivo(this);
		}catch (IOException | NullPointerException e){
			Bitacora.reportaExcepcion("No se pudo generar el analizador de la estación");
			Bitacora.reportaExcepcion("\t->" + this.toString());
			return null;
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean esDeURL(){
		return this.url != null;
	}

	public boolean esDeDisco(){
		return this.archivo != null;
	}

	public String nombre(){
		return this.nombre;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(this.url == null){
			sb.append("ARCHIVO[").append(nombre).append("]");
		}else{
			sb.append("URL[").append(nombre).append("]");
		}
		return sb.toString();
	}

	public File comoArchivo() {
		return this.archivo;
	}

	public URL comoURL(){
		return this.url;
	}
}
