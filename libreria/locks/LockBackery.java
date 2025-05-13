package libreria.locks;

import libreria.utils.*;

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
		for(int i = 0; i< numHilos;i++) {
			int num = turno[i].numero;
			if(max < num)
				max = num;
		}
		
		turno[id].numero = max+1;
		
		for(int i = 0; i<numHilos;i++)
			while(comprueboTruno(turno[i].numero, turno[id].numero, i ,id));
	}
	
	private boolean comprueboTruno(int turnoI,int turnoId,int i,int id) {
		return (turnoI == turnoId && i<id) || (turnoI != 0 && turnoI < turnoId);
	}

	@Override
	public void releaseLock(int id) {
		turno[id].numero = 0;
	}
}
