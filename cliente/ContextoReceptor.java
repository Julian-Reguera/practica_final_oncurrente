package cliente;

import java.util.concurrent.Semaphore;

import libreria.locks.LockTicket;


/*
 * Cuando el programa haya acabado el hilo cliente se queda esperando en el semaforo esperaTerminación
 * a que todos los receptores hayan acabado de recibir sus archivos. El ultimo receptor es el encargado de 
 * norificar y despues de eso se envia el mensaje de terminación al servidor.
*/
public class ContextoReceptor {
    public LockTicket lockReceptores;
    public int numReceptores; //receptores que aun no han acabado
    public volatile boolean programaAcabado;
    public Semaphore esperaTerminacion;

    public ContextoReceptor(int numHilos) {
        this.lockReceptores = new LockTicket(numHilos);
        this.numReceptores = 0;
        programaAcabado = false;
        esperaTerminacion = new Semaphore(0); 
    }
}
