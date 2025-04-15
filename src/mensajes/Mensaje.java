package mensajes;

import java.io.Serializable;

public abstract class Mensaje implements Serializable {
	private static final long serialVersionUID = 1L; //sino me da warning 
	
	private int tipoMensaje;
	
	public Mensaje(int tipo) {
		tipoMensaje = tipo;
	}
	
	public int getTipo() {
		return tipoMensaje;
	}
}
