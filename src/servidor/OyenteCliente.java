package servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class OyenteCliente extends Thread {
	private Socket conexion;
	private DataOutputStream salidaServidor;
	private DataInputStream entradaServidor;
	
	public OyenteCliente(Socket conexion) throws IOException {
		this.conexion = conexion;
		salidaServidor = new DataOutputStream(conexion.getOutputStream());
		entradaServidor = new DataInputStream(conexion.getInputStream());
	}
	
	public void run() {
		boolean acabado = false;
		
		//lee nombre del usuario
		//devuelve ok 
		
        while(!acabado) {
        	
        }
    }
}
