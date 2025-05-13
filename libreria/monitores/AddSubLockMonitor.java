package libreria.monitores;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AddSubLockMonitor implements AddSubMonitor{
	private final Lock cerrojo = new ReentrantLock(true);
	private int contador;
	
	public AddSubLockMonitor() {
		contador = 0;
	}
	
	public void add(int i ) {
		cerrojo.lock();
		contador += i;
		cerrojo.unlock();
	}
	
	public void sub(int i) {
		cerrojo.lock();
		contador -= i;
		cerrojo.unlock();
	}
	
	public int getSumador() {
		return contador;
	}
}
