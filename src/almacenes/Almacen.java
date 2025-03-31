package almacenes;

public interface Almacen<Producto> {
	public void almacenar(Producto producto);
	
	public Producto extraer();
}
