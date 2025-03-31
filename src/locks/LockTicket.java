package locks;

import java.util.concurrent.atomic.AtomicInteger;

public class LockTicket implements LockId{
	private int numHilos;
	private int turno[];
	private volatile int siguiente;
	private AtomicInteger numero;
	
	public LockTicket(int numHilos){
		this.numHilos = numHilos;
		turno = new int[numHilos];
		numero = new AtomicInteger(0);
		siguiente = 0;
	}
	
	@Override
	public void takeLock(int id) {
		turno[id] = numero.getAndAdd(1);
		turno[id] = turno[id]%numHilos;
		while(siguiente != turno[id]);
	}

	@Override
	public void releaseLock(int id) {
		if(numero.get() >= numHilos)
			numero.getAndAdd(-numHilos);
		
		if(siguiente == numHilos-1)
			siguiente = 0;
		else
			siguiente++;
	}
}
