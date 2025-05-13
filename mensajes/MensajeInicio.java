package mensajes;

public class MensajeInicio extends Mensaje {

    private String nombreUsuario;

    public MensajeInicio(String nombreUsuario) {
        super(TipoMensaje.INICIO);
        this.nombreUsuario = nombreUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

}
