package mensajes;

public class PearToPearListo extends Mensaje{

    private String ipDestino;
    private String nombreProducto;
    private int puertoEscucha;

    public PearToPearListo(String nombreProducto, int puertoEscucha, String ipDestino) {
        super(TipoMensaje.PEAR_TO_PEAR_LISTO);

        this.nombreProducto = nombreProducto;
        this.puertoEscucha = puertoEscucha;
        this.ipDestino = ipDestino;
    }

    public String getNombreProducto() {
        return this.nombreProducto;
    }

    public int getPuertoEscucha() {
        return this.puertoEscucha;
    }

    public String getIpDestino() {
        return this.ipDestino;
    }

}
