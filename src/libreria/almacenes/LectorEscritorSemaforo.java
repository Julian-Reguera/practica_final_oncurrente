package libreria.almacenes;

import java.util.concurrent.Semaphore;

public class LectorEscritorSemaforo<Producto> {
	private volatile Object almacen[];
	private int nl; //numero lectores lellendo
	private int ne; //numero escritores escribiendo (max 1)
	private int dl; //lectores esperando
	private int de; //escritores esperando
	private Semaphore lectores,escritores,lockRecursos;
	
	public LectorEscritorSemaforo(int tam) {
		nl = 0;
		ne = 0;
		dl = 0;
		de = 0;
		lectores = new Semaphore(0);
		escritores = new Semaphore(0);
		lockRecursos = new Semaphore(1);
	}
	
	public void escribir(Producto prod, int pos) {
		try {
			lockRecursos.acquire();
			if(nl > 0 | ne > 0) {
				de++;
				lockRecursos.release();
				escritores.acquire(); //PS lockRecursos
				de--;
			}
			ne++;
			lockRecursos.release();
			
			
			almacen[pos] = prod;
			almacen = almacen;
			
			
			lockRecursos.acquire();
			ne--;
			
			if(dl > 0) lectores.release(); //PS lockRecursos
			else if(de > 0) lectores.release(); //PS lockRecursos
			else lockRecursos.release(); 
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
	
	public Producto leer(int prod) {
		Producto lectura = null;
		
		try {
			lockRecursos.acquire();
			if(ne > 0) {
				dl++;
				lockRecursos.release();
				lectores.acquire(); //PS lockRecursos
				dl--;
			}
			
			nl++;
			lockRecursos.release();
			
			lectura = (Producto)almacen[prod];
			
			lockRecursos.acquire();
			nl--;
			
			if(nl == 0 && de > 0) escritores.release(); //PS lockRecursos
			else lockRecursos.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return lectura;
	}
}
