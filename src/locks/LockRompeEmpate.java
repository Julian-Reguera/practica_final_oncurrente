package locks;

import utils.EnteroConcurrente;

public class LockRompeEmpate implements LockId{
	private EnteroConcurrente ultimo[];
	private EnteroConcurrente fase[];
	private volatile int numHilos;
	
	public LockRompeEmpate(int numHilos) {
		this.numHilos = numHilos;
		ultimo = new EnteroConcurrente[numHilos];
		fase = new EnteroConcurrente[numHilos];
		
		for(int i = 0; i< numHilos;i++) {
			ultimo[i] = new EnteroConcurrente(-1);
			fase[i] = new EnteroConcurrente(-1);
		}
	}
	
	
	@Override
	public void takeLock(int id) {
		for(int i = 0; i<numHilos;i++) {
			ultimo[i].numero = id;
			fase[id].numero = i;
			
			for(int j = 0; j<numHilos;j++)
				while(j != id && fase[j].numero >= fase[id].numero && ultimo[i].numero == id);
		}
	}

	@Override
	public void releaseLock(int id) {
		fase[id].numero = -1;
	}

}
