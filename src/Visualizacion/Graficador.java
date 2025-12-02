package Visualizacion;

import Utils.Bitacora;

import javax.swing.*;

public class Graficador {

	private Graficador(){

	}

	//Graficación de X contra Y
	public static void dibuja(Object[] columna1, Object[] columna2){
		if(columna1.length != columna2.length){
			Bitacora.reportaExcepcion("El tamaño de los arreglos a graficar es diferente");
			return;
		}

		//qph
	}

}
