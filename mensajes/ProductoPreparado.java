package mensajes;

public class ProductoPreparado extends Mensaje {

    private String nombreProducto;
    private String usuarioDestino;
    private int puertoEcucha;
    
    public ProductoPreparado(String nombreProducto, int puertoEcucha, String usuarioDestino) {
        super(TipoMensaje.PRODUCTO_PREPARADO);
        
        this.nombreProducto = nombreProducto;
        this.puertoEcucha = puertoEcucha;
        this.usuarioDestino = usuarioDestino;
    }
    
    public String getNombreProducto() {
        return this.nombreProducto;
    }

    public String getUsuarioDestino() {
        return this.usuarioDestino;
    }

    public int getPuertoEscucha() {
        return this.puertoEcucha;
    }
}
