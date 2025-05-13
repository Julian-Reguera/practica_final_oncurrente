package mensajes;

import java.io.Serializable;

public abstract class Mensaje implements Serializable {
	private static final long serialVersionUID = 1L; //sino me da warning 
	
	private TipoMensaje tipoMensaje;
	
	public Mensaje(TipoMensaje tipo) {
		tipoMensaje = tipo;
	}
	
	public TipoMensaje getTipo() {
		return tipoMensaje;
	}
}
