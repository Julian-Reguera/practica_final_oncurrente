package libreria.monitores;

public class ReaderWriterSynchronizedMonitor<Producto> {
	private Object almacen[];
	
	public ReaderWriterSynchronizedMonitor(int size) {
		almacen = new Object[size];
	}
	
	public synchronized Producto read(int pos) {
		return (Producto) almacen[pos];
	}
	
	public synchronized void write(Producto prod,int pos) {
		almacen[pos] = prod;
	}
}
