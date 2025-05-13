package libreria.almacenes.test;

import libreria.almacenes.Almacen;

public class ConsumidorInt extends Thread{
	private Almacen<Integer> almacen;
	private volatile Integer salida[];
	private int numProductos;
	
	public ConsumidorInt(Almacen<Integer> almacen, int numProductos) {
		this.almacen = almacen;
		this.numProductos = numProductos;
		salida = new Integer[numProductos];
	}
	
	public void run(){
		for(int i=0;i<numProductos;i++)
			salida[i] = almacen.extraer();
		
		salida = salida;
	}
	
	public Integer[] getSalida() {
		return salida;
	}

}
