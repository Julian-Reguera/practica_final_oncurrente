package servidor;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
	public static int PUERTO = 1234;
	private ServerSocket ss;
	protected DataOutputStream salidaServidor; //Flujo entrada
	
	public void startServer()//Método para iniciar el servidor
    {
		while(true) {
			try {
				Socket cliente = ss.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }
}
