package libreria.monitores;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import libreria.almacenes.Almacen;

public class AlmacenLockMonitor<Producto> implements Almacen<Producto>{
	private final Object[] buf;
    private int colocar, sacar;
    private int numProduct, numProdAct;

    private final Lock lock;
    private final Condition noLleno;
    private final Condition noVacio;

    public AlmacenLockMonitor(int numProduct) {
    	buf = new Object[numProduct];
		sacar = 0;
		colocar = 0;
		this.numProduct = numProduct;
		numProdAct = 0;
		
        lock = new ReentrantLock(true);
        noLleno = lock.newCondition();
        noVacio = lock.newCondition();
    }

    public void almacenar(Producto producto) {
	     try {  
    		lock.lock();
	        while (numProdAct == numProduct)
	            noLleno.await();  
	
	        buf[colocar] = producto;
	        colocar++;
			colocar = colocar%numProduct;
			numProdAct++;
	
	        noVacio.signal();
	        lock.unlock();
	    } catch (InterruptedException e) {
			e.printStackTrace(); 
	    }
    }

    public Producto extraer() {
    	Producto ret = null;
    	try { 
	        lock.lock();
	        while (numProdAct == 0)
	            noVacio.await(); 
	
	        ret = (Producto) buf[sacar];
	        sacar++;
			sacar = sacar%numProduct;
			numProdAct--;
	
	        noLleno.signal();  
	        lock.unlock();
	    } catch (InterruptedException e) {
			e.printStackTrace();
	    }
        return ret;   
    }
}
