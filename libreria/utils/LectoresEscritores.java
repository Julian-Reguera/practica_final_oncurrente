package libreria.utils;

public interface LectoresEscritores<Producto> {

    public void escribir(Producto p, int pos);
	
	public Producto leer(int pos);

}
