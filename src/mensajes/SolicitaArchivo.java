package mensajes;

public class SolicitaArchivo extends Mensaje{

	private String nombreArchivo;
	public SolicitaArchivo(String nombreArchivo) {
		super(ConstantesMensajes.IdSolicitaArchivo);
		this.nombreArchivo = nombreArchivo;
	}
	
	public String getNombreArchivo() {
		return nombreArchivo;
	}
}
