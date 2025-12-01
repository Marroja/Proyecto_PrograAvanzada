package ArchivosDatos;

import Utils.Config;

import java.time.LocalDate;
import java.util.Arrays;


public class RenglonDatos {

	//qph traer el nombre, el estado y el municipio al renglón de datos para su escritura al final
	public static final String VALOR_CENTINELA = ""+Double.NaN;

	private final String nombreArchivo;
	private final int numLinea;
	private final Object[] columnas;

    public static RenglonDatos digiereRenglon(String nombreArchivo, int numLinea, String renglonLeido){
        return new RenglonDatos(nombreArchivo, numLinea,  renglonLeido);
    }

	//OJO la clase es privada para que SIEMPRE se genere un renglón a partir del método estático de arriba
	private RenglonDatos(String nombreArchivo, int numLinea, String renglonLeido){
		String separador = Config.separador();
		char[] tiposColumna = Config.columnas();

		this.numLinea = numLinea;
		this.nombreArchivo = nombreArchivo;

		renglonLeido = renglonLeido.replace("NULO", VALOR_CENTINELA);

		String[] textoColumnas = renglonLeido.split(separador);	// "\\s+" indica espacio blanco (habrá que generalizar)
		columnas = new Object[textoColumnas.length];

		for(int i = 0; i < columnas.length; i++){
			switch (tiposColumna[i]){
				case 'f':
					columnas[i] = LocalDate.parse(textoColumnas[i]);
					break;
				case 'd':
					try{
						columnas[i] = Double.parseDouble(textoColumnas[i]);
					}catch (NumberFormatException e){
						columnas[i] = Double.NaN;
					}
					break;
				case 'i':
					try{
						columnas[i] = Integer.parseInt(textoColumnas[i]);
					}catch (NumberFormatException e){
						columnas[i] = Double.NaN;
					}
					break;
				case 'c':
					columnas[i] = textoColumnas[i].charAt(0);
					break;
				case 't':
					columnas[i] = textoColumnas[i];
					break;
			}
		}
	}

	public Object columna(int indice){
		return columnas[indice];
	}

	Object[] columnas(){
		return this.columnas;
	}

    @Override
    public String toString() {
        // Primera “columna”: etiqueta de estación
		return this.nombreArchivo + "::" + numLinea + " - " + Arrays.toString(columnas);
    }

    public static void main(String[] args) throws Exception {
        // Esto compila correctamente gracias a la versión de 1 argumento
        RenglonDatos r = digiereRenglon("Prueba", 1,"1982-02-16\t0\t4.81\t25.6\t1.4");
        System.out.println(r.toString());
    }
}
