package main;

import almacenes.Almacen;
import almacenes.AlmacenSemaphoreMulti;
import almacenes.AlmacenSemaphoreUnico;
import almacenes.almacenes.test.ConsumidorInt;
import almacenes.almacenes.test.ProductorInt;
import locks.DualLock;
import locks.LockBackery;
import locks.LockId;
import locks.LockRompeEmpate;
import locks.LockSemaforo;
import locks.LockTicket;
import utils.EnteroConcurrente;

public class Main {

	public static void main(String[] args) {
		int numNucleos = Runtime.getRuntime().availableProcessors();
		int aux;
		boolean aux2;
		
		System.out.println("PRUEBA DE LOS LOCK (practica2)");
		aux = testLock(2,200000,new DualLock());
		System.out.println("lock de 2: " + aux);
		aux = testLock(numNucleos,100000,new LockRompeEmpate(numNucleos));
		System.out.println("lock rompe empate: " + aux);
		aux = testLock(numNucleos,1000,new LockTicket(numNucleos));
		System.out.println("lock ticket: " + aux);
		aux = testLock(numNucleos,1000,new LockBackery(numNucleos));
		System.out.println("lock backery: " + aux);
		aux = testLock(numNucleos,100000,new LockSemaforo());
		System.out.println("lock semaforo: " + aux);
		
		System.out.println("\nPRUEBA DE LOS ALMACENES (practica3)");
		aux2 = testAlmacen(numNucleos, 1000,new AlmacenSemaphoreUnico<Integer>());
		System.out.println("almacen con semaforo unico: " + aux2);
		aux2 = testAlmacen(numNucleos, 1000,new AlmacenSemaphoreMulti<Integer>(30));
		System.out.println("almacen con semaforo multiple: " + aux2);
		
	}
	
	private static int testLock(int numNucleos, int iteraciones, LockId lock) {
		Thread hilos[] = new Thread[numNucleos];
		EnteroConcurrente contador = new EnteroConcurrente(0);
		
		numNucleos = numNucleos - numNucleos%2; //siempre tiene que ser par
		
		for(int i = 0; i< numNucleos;i++) {
			int id = i;
			
			if(i%2 == 0) { //id par para restadores
				hilos[i] = new Thread(()->{
					for(int j = 0; j<iteraciones;j++) {
						lock.takeLock(id);
						contador.numero--;
						lock.releaseLock(id);
					}
				});
			}
			else { // id impar para sumadores
				hilos[i] = new Thread(()->{
					for(int j = 0; j<iteraciones;j++) {
						lock.takeLock(id);
						contador.numero++;
						lock.releaseLock(id);
					}
				});
			}
		}
		
		for(int i= 0; i< numNucleos; i++) {
			hilos[i].start();
		}
		
		for(int i= 0; i< numNucleos; i++) {
			try {
				hilos[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return contador.numero;
	}
	
	private static boolean testAlmacen(int numHilos, int elemPerProductor,Almacen<Integer> almacen){
		numHilos = numHilos - numHilos%2; //tiene que ser par
		
		int N = (numHilos/2) * elemPerProductor; //elementos totales que se van a escribir
		boolean res = true;
		boolean marcado[] = new boolean[N];
		Integer numeros[][] = new Integer[numHilos/2][elemPerProductor];
		
		ProductorInt productores[] = new ProductorInt[numHilos/2];
		ConsumidorInt consumidores[] = new ConsumidorInt[numHilos/2];
		
		//No se ha leido ningún numero
		for(int i = 0; i<N;i++) {
			marcado[i] = false;
		}
		
		//se cargan en la matriz todos los numeros del 0 al N
		for(int i = 0; i< numHilos/2;i++) {
			for(int j = 0; j< elemPerProductor;j++) {
				numeros[i][j] = i*elemPerProductor + j;
			}
		}
		
		for(int i = 0; i< numHilos/2;i++) {
			productores[i] = new ProductorInt(almacen,numeros[i]);
			consumidores[i] = new ConsumidorInt(almacen,elemPerProductor);
		}
		
		//LANZAMOS LOS HILOS
		
		for(int i = 0; i< numHilos/2;i++) {
			productores[i].start();
			consumidores[i].start();
		}
		
		for(int i = 0; i< numHilos/2;i++) {
			try {
				productores[i].join();
				consumidores[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//CHEQUEAMOS QUE SE HA HECHO CORRECTAMENTE
		
		for(int i = 0; i < numHilos/2; i++) {
			for(Integer num: consumidores[i].getSalida())
				if(marcado[num]) 
					res = false; //se ha leido 2 veces el mismo numero
				else
					marcado[num] = true;
		}
		
		for(int i = 0; i< N;i++) {
			if(!marcado[i])
				res = false; //no se ha leido ese numero
		}
		
		return res;	
	}

}
