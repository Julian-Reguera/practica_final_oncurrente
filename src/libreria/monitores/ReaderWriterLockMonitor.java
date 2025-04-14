package libreria.monitores;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ReaderWriterLockMonitor{
	private final ReentrantLock lock;
	private Condition okToRead, okToWrite;
	private int nr,nw,dr,dw;
	
	public ReaderWriterLockMonitor() {
		lock = new ReentrantLock(true);
		nr=0;
		nw=0;
		dw=0;
		dr=0;
		okToRead = lock.newCondition();
		okToWrite = lock.newCondition();
	}
	
	public void request_read() {
		lock.lock();
		
		try {
			if(nw > 0) {
				dr++;
				okToRead.await(); // Paso de condicion. ya se ha hecho dr-- y br++
				
				//Despierto a los lectores en cadena
				if(dr>0) {
					dr--;
					nr++;
					okToRead.signal();
				}
			}
			else {
				nr++;
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		lock.unlock();
	}
	
	public void release_read() {
		lock.lock();
		
		nr--;
		if(nr == 0 && dw > 0) {
			dw--;
			nw++;
			okToWrite.signal();
		}
			
		lock.unlock();
	}
	
	public void request_write() {
		lock.lock();
		
		try {
			if(nr>0 || nw > 0) {
				dw++;
				okToWrite.await();//Paso de condición dw-- y nw++
			}
			else {
				nw++;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		};
		
		lock.unlock();
	}
	
	public void release_write() {
		lock.lock();
		
		nw--;
		
		if(dw > 0) {
			dw--;
			nw++;
			okToWrite.signal();
		}
		else if(dr > 0) {
			dr--;
			nr++;
			okToRead.signal();
		}
		
		lock.unlock();
	}
}
