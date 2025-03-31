package utils;

public class EnteroConcurrente {
	public volatile int numero;
	
	public EnteroConcurrente() {
		numero = 0;
	}
	
	public EnteroConcurrente(int ini) {
		numero = ini;
	}
}
