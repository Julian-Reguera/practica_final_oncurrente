package mensajes;

public class MensajeConexion extends Mensaje{

	private String nombre;
	private int puertoMandador;
	
	public MensajeConexion(String nombre, int puertoMandador) {
		super(ConstantesMensajes.IdConexion);
		
		this.nombre = nombre;
		this.puertoMandador = puertoMandador;
	}
	
	public int getPuertoMandador() {
		return this.puertoMandador;
	}
	
	public String getNombre() {
		return this.nombre;
	}
	
	

}
