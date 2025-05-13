package mensajes;

public class MensajeError extends Mensaje {

    private String errorMessage;

    public MensajeError(String errorMessage) {
        super(TipoMensaje.ERROR);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
