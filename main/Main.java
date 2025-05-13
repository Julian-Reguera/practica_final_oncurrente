package main;
import java.util.Scanner;

import cliente.Cliente;
import cliente.MandadorArchivo;
import cliente.ReceptorArchivos;
import libreria.PruebaLibreria;
import servidor.Servidor;

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
            Servidor servidor = new Servidor();
			servidor.startServer();
			break;
		case 2:
			System.out.print("\nEjecutando cliente");
            Cliente	cliente = new Cliente();
			cliente.ejecutar();
			break;
		}
		
		sc.close();
	}
}
