package mensajes;

public class PedirListaConectados extends Mensaje {

    public PedirListaConectados() {
        super(TipoMensaje.PETICION_USUARIOS_CONECTADOS);
    }
    
    @Override
    public String toString() {
        return "PedirListaConectados []";
    }

}
