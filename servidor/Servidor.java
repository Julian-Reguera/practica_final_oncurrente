package servidor;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class Servidor {
    public static int PUERTO = 1234;
	private ServerSocket ss;
	private static final String ARCHIVO_INICIALIZACION = "baseDeDatos.txt"; //Archivo de inicialización
	private DataOutputStream salidaServidor; //Flujo entrada
	
	public void startServer()//Método para iniciar el servidor
    {
		DatosServidor datosServidor = new DatosServidor(ARCHIVO_INICIALIZACION);
		Semaphore lockConsola = new Semaphore(1); //lock para la consola
		
		try {
			ss = new ServerSocket(PUERTO);

			while(true) {
				try {
					Socket cliente = ss.accept();
					OyenteCliente oyente = new OyenteCliente(cliente, lockConsola,datosServidor);
					oyente.start(); //Inicia el hilo para atender al cliente
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //Crea el socket del servidor
    }
}
