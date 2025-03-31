package locks;

public class DualLock implements LockId{
	private volatile int ultimo;
	private volatile boolean espera[];
	
	public DualLock() {
		espera = new boolean[2];
		ultimo = 0;
	}
	
	public void takeLock(int id) {
		int otro = (id-1) * -1;
		espera[id] = true;
		ultimo = id;
		
		while(espera[otro] && ultimo == id);
	}
	
	public void releaseLock(int id) {
		espera[id] = false;
	}
}
