package libreria.utils;

public class Entero {
	public volatile int _valor;
	
	public Entero(int valor){
		_valor = valor;
	}
	
	public int get_valor() {
		return _valor;
	}
	
	public void set_valor(int num) {
		_valor = num;
	}
	
	public void incrementar() {
		_valor = _valor + 1;
	}
	
	public void decrementar() {
		_valor = _valor - 1;
	}
}
