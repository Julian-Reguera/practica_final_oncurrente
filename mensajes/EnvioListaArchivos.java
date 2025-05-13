package mensajes;

public class EnvioListaArchivos extends Mensaje {
    private String[] listaArchivos;

    public EnvioListaArchivos(String[] listaArchivos) {
        super(TipoMensaje.ENVIO_LISTA_ARCHIVOS);
        this.listaArchivos = listaArchivos;
    }

    public String[] getListaArchivos() {
        return listaArchivos;
    }
}
