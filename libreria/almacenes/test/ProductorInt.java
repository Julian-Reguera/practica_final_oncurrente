package libreria.almacenes.test;

import libreria.almacenes.Almacen;

public class ProductorInt extends Thread{
	private Almacen<Integer> almacen;
	private Integer productos[];
	
	public ProductorInt(Almacen<Integer> almacen, Integer productos[]) {
		this.almacen = almacen;
		this.productos = productos;
	}
	
	public void run() {
		for(Integer num:productos)
			almacen.almacenar(num);
	}

}
