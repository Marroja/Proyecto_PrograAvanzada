package Utils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Utilería para ahorrar código al momento de pasar arreglos a listas y listas a arreglos
 */
public class Arreglos {

	private Arreglos(){

	};

	public static String[] listaAArregloTexo(ArrayList<String> lista){
		String[] arr = new String[lista.size()];

		for(int i = 0; i < lista.size(); i++){
			arr[i] = lista.get(i);
		}

		return arr;
	}

	public static double[] listaAArregloDoble(ArrayList<Double> lista){
		double[] arr = new double[lista.size()];

		for(int i = 0; i < lista.size(); i++){
			arr[i] = lista.get(i);
		}

		return arr;
	}

	public static int[] listaAArregloEntero(ArrayList<Integer> lista){
		int[] arr = new int[lista.size()];

		for(int i = 0; i < lista.size(); i++){
			arr[i] = lista.get(i);
		}

		return arr;
	}

	public static Object[] listaAArreglo(ArrayList<Object> lista){
		Object[] arr = new Object[lista.size()];

		for(int i = 0; i < lista.size(); i++){
			arr[i] = lista.get(i);
		}

		return arr;
	}

	public static ArrayList<String> arregloAListaTexto(String[] arreglo){
		ArrayList<String> lista = new ArrayList<>(arreglo.length);
		//Lamentablemente esta línea no está para enteros y dobles
		lista.addAll(Arrays.asList(arreglo));
		return lista;
	}

	public static ArrayList<Double> arregloAListaDoble(double[] arreglo){
		ArrayList<Double> lista = new ArrayList<>(arreglo.length);
		for(int i = 0; i < arreglo.length; i++){
			lista.add(arreglo[i]);
		}
		return lista;
	}

	public static ArrayList<Integer> arregloAListaEntero(int[] arreglo){
		ArrayList<Integer> lista = new ArrayList<>(arreglo.length);
		for(int i = 0; i < arreglo.length; i++){
			lista.add(arreglo[i]);
		}
		return lista;
	}

	public static void main(String[] args) {
		ArrayList<String> lista = new ArrayList<>();
		lista.add("hola");
		lista.add("soy");
		lista.add("alumno");

		String[] arreglo = listaAArregloTexo(lista);
		System.out.println(Arrays.toString(arreglo));
	}

	public static boolean contiene(int[] valores, int valor) {
		for(int i = 0; i < valores.length; i++){
			if(valores[i] == valor){
				return true;
			}
		}
		return false;
	}

	public static boolean contiene(double[] valores, double valor) {
		for(int i = 0; i < valores.length; i++){
			if(valores[i] == valor){
				return true;
			}
		}
		return false;
	}
}
