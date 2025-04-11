package utils;

public interface LectorEscritor<Producto> {

	public void escribir(Producto p, int pos);
	
	public Producto leer(int pos);

}
