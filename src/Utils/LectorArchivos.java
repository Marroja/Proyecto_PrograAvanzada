package Utils;


import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class LectorArchivos {

	private LectorArchivos(){

	}

	public static String[] leerArchivosEnCarpeta(String dirCarpeta){
		File carpeta = new File(dirCarpeta);
		ArrayList<String> archivos = new ArrayList<>();
		if(carpeta.isDirectory()){
			File[] enCarpeta = carpeta.listFiles();

			if(enCarpeta == null){
				Bitacora.reportaExcepcion("Error accediendo a carpeta: " + dirCarpeta);
				return null;
			}

			for(File f : enCarpeta){
				if(f.isFile()){
					archivos.add(f.getAbsolutePath());
				}
			}

			String[] dirs = new String[archivos.size()];
			for(int i = 0; i < archivos.size(); i++){
				dirs[i] = archivos.get(i);
			}

			return dirs;
		}
		return null;
	}

	public static String[] leerArchivoCompleto(String dirArchivo){
		ArrayList<String> lineas = new ArrayList<>();

		File archivo = new File(dirArchivo);
		try {
			BufferedReader br = new BufferedReader(new FileReader(archivo));
			String linea;
			while((linea = br.readLine()) != null){
				lineas.add(linea.trim());
			}
			br.close();
		} catch (IOException e) {
			throw new RuntimeException("Error de lectura del archivo: "+ dirArchivo);
		}
		return Arreglos.listaAArregloTexo(lineas);
	}


	public static String[] leeArchivoCompletoConFiltro(String dirArchivo, char filtro){
		ArrayList<String> lineas = new ArrayList<>();

		File archivo = new File(dirArchivo);
		try {
			BufferedReader br = new BufferedReader(new FileReader(archivo));
			String linea;
			while((linea = br.readLine()) != null){
				if(!linea.startsWith(""+filtro)){
					lineas.add(linea.trim());
				}
			}
			br.close();
		} catch (IOException e) {
			throw new RuntimeException("Error de lectura del archivo "+ dirArchivo);
		}
		return Arreglos.listaAArregloTexo(lineas);
	}

	public static void main(String[] args) {
		System.out.println(Arrays.toString(leerArchivoCompleto("Proyecto_PrograAvanzada/recursos/formato.ini")));
		System.out.println(Arrays.toString(leeArchivoCompletoConFiltro("Proyecto_PrograAvanzada/recursos/formato.ini", '#')));
	}

}
