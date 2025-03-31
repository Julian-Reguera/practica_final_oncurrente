package locks;

import utils.EnteroConcurrente;

public class LockBackery implements LockId{
	private EnteroConcurrente turno[]; //el turno de cada hilo
	private int numHilos;
	
	
	public LockBackery(int numHilos) {
		this.numHilos = numHilos;
		turno = new EnteroConcurrente[numHilos];
		
		for(int i = 0; i< numHilos;i++)
			turno[i] = new EnteroConcurrente(0);
	}
	
	@Override
	public void takeLock(int id) {
		turno[id].numero = 1;
		
		int max = 0;
		for(int i = 0; i< numHilos;i++)
			if(max > turno[i].numero)
				max = turno[i].numero;
		
		turno[id].numero = max+1;
		
		for(int i = 0; i<numHilos;i++)
			while(turno[i].numero == turno[id].numero && i<id)
				while(turno[i].numero != 0 && turno[i].numero < turno[id].numero);
	}

	@Override
	public void releaseLock(int id) {
		turno[id].numero = 0;
	}
}
