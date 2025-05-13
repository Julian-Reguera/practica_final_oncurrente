package libreria.monitores;


public class ReaderWriterSynchronizedMonitor{
	int nw,nr;
	
	public ReaderWriterSynchronizedMonitor(int size) {
		nw = 0;
		nr = 0;
	}
	
	public synchronized void request_read() {
		try {
			while(nw>0) wait();
			nr++;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void release_read() {
		nr--;
		if(nr == 0) notifyAll();
	}
	
	public synchronized void request_write() {
		try {
			while(nr > 0 || nw > 0) wait();
			nw++;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void release_write() {
		nw--;
		notifyAll();
	}
}
