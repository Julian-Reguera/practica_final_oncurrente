package libreria.monitores;

import libreria.almacenes.Almacen;

public class AlmacenSynchronizedMonitor<Producto> implements Almacen<Producto> {
	private int inicio, fin, tamAlmacen, numProductos;
	private Object buffer[];

	public AlmacenSynchronizedMonitor(int espacio) {
		this.tamAlmacen = espacio;
		buffer = new Object[espacio];
		inicio = 0;
		fin = 0;
	}

	@Override
	public synchronized void almacenar(Producto producto) {
		try {
			while (numProductos == tamAlmacen)
				wait();

			if (numProductos == 0) // solo notifica si hay hilos esperando
				notifyAll();

			buffer[fin] = producto;
			fin++;
			fin = fin % tamAlmacen;
			numProductos++;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized Producto extraer() {
		Producto prod = null;
		try {
			while (numProductos == 0)
				wait();

			if (numProductos == tamAlmacen)
				notifyAll();

			prod = (Producto) buffer[inicio];
			inicio++;
			inicio = inicio % tamAlmacen;
			numProductos--;

		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return prod;
	}
}