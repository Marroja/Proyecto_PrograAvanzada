package Utils;

import java.util.Arrays;

/**
 * Biblioteca con la que uno puede realizar análisis estadísticos
 * @author Rodrigo Aragón
 */
public final class Estadisticas {

	public static double media(double[] valores){
		double acum = 0;
		for(int i = 0; i < valores.length; i++){
			acum += valores[i];
		}
		return acum / (double) valores.length;
	}

	public static double mediana(double[] valores){
		double[] vals = Arrays.copyOf(valores, valores.length);

		Arrays.sort(vals);

		if(vals.length == 0){
			return 0;
		}
		if(vals.length % 2 == 0){
			return (vals[vals.length / 2] + vals[vals.length/2 - 1]) / 2.0;
		}
		return vals[vals.length / 2];
	}

	public static double moda(double[] valores){
		double valorModa = 0;
		double cuentaMaxima = 0;

		for(int i = 0; i < valores.length; i++){
			int cuenta = 0;
			for(int j = 0; j < valores.length; j++){
				if(valores[j] == valores[i]){
					cuenta ++;
				}
			}

			if(cuenta > cuentaMaxima){
				cuentaMaxima = cuenta;
				valorModa = valores[i];
			}
		}
		return valorModa;
	}


	public static double varianza(double[] valores){
		double media = media(valores);
		double acum = 0;

		for(int i = 0; i < valores.length; i++){
			double x = (valores[i] - media);
			acum += (x*x);
		}

		return acum / (double) valores.length;
	}

	public static double desviacionTipica(double[] valores){
		return Math.sqrt(varianza(valores));
	}

	public static double covarianza(double[] valoresX, double[] valoresY){
		assert (valoresX.length == valoresY.length);

		double mediaX = media(valoresX);
		double mediaY = media(valoresY);

		double acum = 0;
		for(int i = 0; i < valoresX.length; i++){
			acum += (valoresX[i] - mediaX) * (valoresY[i] - mediaY);
		}

		return acum / (double) valoresY.length;
	}

	public static double correlacionPearson(double[] valoresX, double[] valoresY){
		double cova = covarianza(valoresX, valoresY);
		double varianzaX = varianza(valoresX);
		double varianzaY = varianza(valoresY);
		double denominadorCauchySchwartz = Math.sqrt(varianzaX * varianzaY);
		return cova/denominadorCauchySchwartz;
	}

	public static void main(String[] args){
		double[] valores = new double[]{1,2,3,4,5,6};
		System.out.println("media: " + media(valores));
		System.out.println("mediana: " + mediana(valores));
		System.out.println("moda: " + moda(valores));
		System.out.println("varianza: " + varianza(valores));
		System.out.println("desviacion tipica: " + desviacionTipica(valores));

		double[] x = new double[]{1,2,3,4,5,6};
		double[] y = new double[]{1,2,3,4,5,6};
		System.out.println("covarianza: "+ covarianza(x, y));
		System.out.println("correlación: " + correlacionPearson(x,y));
	}

}
