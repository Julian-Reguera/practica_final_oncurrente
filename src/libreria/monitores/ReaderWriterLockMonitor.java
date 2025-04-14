package libreria.monitores;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ReaderWriterLockMonitor <Producto> {
	private final ReentrantLock lock;
	private Object almacen[];
	private Condition okToRead, okToWrite;
	private int nr,nw;
	
	public ReaderWriterLockMonitor(int size) {
		lock = new ReentrantLock(true);
		almacen = new Object[size];
		nr=0;
		nw=0;
		okToRead = lock.newCondition();
		okToWrite = lock.newCondition();
	}
	
	public void request_read() {
			try {
				while(nr>0) okToRead.await();
				nr++;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void release_read() {
		nr--;
		if(nr == 0) okToWrite.signal();
	}
	
	public void request_write() {
		try {
			while(nr>0 || nw >0) okToWrite.await();
			nw++;
		} catch (InterruptedException e) {
			e.printStackTrace();
		};
	}
	
	public void release_write() {
		nw--;
		okToWrite.signal();
		okToRead.signal();
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
