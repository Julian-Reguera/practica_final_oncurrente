package libreria.locks;

import java.util.concurrent.Semaphore;

/*
 * Mantengo Id en las operaciones de take y release para poder usar las mismas funciones de prueba
 */

public class LockSemaforo implements LockId{
	private Semaphore semaforo;
	
	public LockSemaforo() {
		semaforo = new Semaphore(1);
	}
	
	@Override
	public void takeLock(int id) {
		try {
			semaforo.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void releaseLock(int id) {
		semaforo.release();
	}

}
