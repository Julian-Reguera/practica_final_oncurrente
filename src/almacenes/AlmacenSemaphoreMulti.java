package almacenes;

import java.util.concurrent.Semaphore;

public class AlmacenSemaphoreMulti<Producto> implements Almacen<Producto>{
	private int capacidad;
	private volatile int inicio,fin;
	private volatile Object buffer[];
	private Semaphore productores, consumidores, productos, espacios; 
	
	public AlmacenSemaphoreMulti(int capacidad) {
		this.capacidad = capacidad;
		inicio = 0; 
		fin = 0;
		buffer = new Object[capacidad];
		productores = new Semaphore(1);
		consumidores = new Semaphore(1);
		productos = new Semaphore(0);
		espacios = new Semaphore(capacidad);
	}
	
	@Override
	public void almacenar(Producto producto) {

		try {
			espacios.acquire();
			productores.acquire();
			buffer[fin] = producto;
			buffer = buffer;
			fin = (fin+1)%capacidad;
			productores.release();
			productos.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Producto extraer() {
		Producto res = null;
		
		try {
			productos.acquire();
			consumidores.acquire();
			res = (Producto)buffer[inicio];
			inicio = (inicio+1)%capacidad;
			consumidores.release();
			espacios.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return res;
	}

}
