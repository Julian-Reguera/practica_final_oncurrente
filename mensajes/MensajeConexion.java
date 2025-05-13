package mensajes;

public class MensajeConexion extends Mensaje{

	private String nombre;
	
	public MensajeConexion(String nombre) {
		super(TipoMensaje.CONEXION);
		
		this.nombre = nombre;
	}
	
	public String getNombre() {
		return this.nombre;
	}
}
