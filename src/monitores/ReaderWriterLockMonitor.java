package monitores;

import java.util.concurrent.locks.ReentrantLock;

public class ReaderWriterLockMonitor <Producto> {
	private final ReentrantLock lock;
	private Object almacen[];
	
	public ReaderWriterLockMonitor(int size) {
		lock = new ReentrantLock(true);
		almacen = new Object[size];
	}
	
	public Producto read(int pos) {
		Producto prod = null;
		lock.lock();
		prod = (Producto)almacen[pos];
		lock.unlock();
		
		return prod;
	}
	
	public void write(Producto prod, int size) {
		lock.lock();
		almacen[size] = prod;
		lock.unlock();
	}
}
