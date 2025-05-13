package mensajes;

public class AnadirArchivo extends Mensaje{
    private String nombreArchivo; //nombre del archivo que se va a añadir al servidor

    public AnadirArchivo(String nombreArchivo) {
        super(TipoMensaje.ANADIR_ARCHIVO);
        this.nombreArchivo = nombreArchivo;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }
}
