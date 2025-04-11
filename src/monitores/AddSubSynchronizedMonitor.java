package monitores;

public class AddSubSynchronizedMonitor implements AddSubMonitor{
	private int sumador;
	
	public AddSubSynchronizedMonitor() {
		sumador = 0;
	}
	
	public synchronized void add(int i) {
		sumador += i;
	}
	
	public synchronized void sub(int i) {
		sumador -= i;
	}
	
	public synchronized int getSumador() {
		return sumador;
	}
}
