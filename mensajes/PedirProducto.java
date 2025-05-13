package mensajes;

public class PedirProducto extends mensajes.Mensaje {

    private String nombreProducto;
    private String nombreUsuario;

    public PedirProducto(String nombreProducto, String nombreUsuario) {
        super(TipoMensaje.SOLICITAR_ARCHIVO);
        this.nombreProducto = nombreProducto;
        this.nombreUsuario = nombreUsuario;
    }

    public String getNombreProducto() {
        return this.nombreProducto;
    }

    public String getNombreUsuario() {
        return this.nombreUsuario;
    }
}
