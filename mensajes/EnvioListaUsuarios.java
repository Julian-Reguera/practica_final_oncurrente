package mensajes;

public class EnvioListaUsuarios extends Mensaje {

    private String[] listaUsuariosConectados;

    public EnvioListaUsuarios(String[] listaUsuariosConectados) {
        super(TipoMensaje.ENVIO_USUARIOS_CONECTADOS);
        this.listaUsuariosConectados = listaUsuariosConectados;
    }

    public String[] getListaUsuariosConectados() {
        return this.listaUsuariosConectados;
    }
}
