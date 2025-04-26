package main;

import libreria.PruebaLibreria;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		//PruebaLibreria.prueba();
		Scanner sc = new Scanner(System.in);
		int entrada = -1;
		
		System.out.print("1. servidor\n2. cliente");
		System.out.print("\n\nIntroduce el numero correspondiente al programa que quieras usar: ");
		entrada = sc.nextInt();
		while(entrada > 2 || entrada <= 0) {
			System.out.print("Numero incorrecto, introduzca un numero valido: ");
			entrada = sc.nextInt();
		}
		switch(entrada) {
		case 1:
			System.out.print("\nEjecutando servidor");
			break;
		case 2:
			System.out.print("\nEjecutando cliente");
			break;
		}
		
		sc.close();
	}
}
