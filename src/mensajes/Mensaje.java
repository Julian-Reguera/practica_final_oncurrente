package mensajes;

public abstract class Mensaje {
	private int tipoMensaje;
	
	public Mensaje(int tipo) {
		tipoMensaje = tipo;
	}
	
	public int getTipo() {
		return tipoMensaje;
	}
}
