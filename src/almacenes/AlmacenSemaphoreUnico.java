package almacenes;

import java.util.concurrent.Semaphore;

public class AlmacenSemaphoreUnico<Producto> implements Almacen<Producto>{
	private Semaphore consumidor;
	private Semaphore productor;
	private volatile Producto espacio;
	
	public AlmacenSemaphoreUnico() {
		consumidor = new Semaphore(0);
		productor = new Semaphore(1);
	}
	
	@Override
	public void almacenar(Producto producto) {
		try {
			productor.acquire();
			espacio = producto;
			consumidor.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Producto extraer() {
		Producto res = null;
		
		try {
			consumidor.acquire();
			res = espacio;
			productor.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return res;
	}

}
