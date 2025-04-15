package cliente;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import mensajes.FragmentoArchivo;
import mensajes.SolicitaArchivo;

public class MandadorArchivo extends Thread{
	private ServerSocket ss;
	private int puerto;
	private String ruta;
	
	public MandadorArchivo(String ruta) {
		try {
			//0 para que elija el puerto que prefiera (si 2 clientes están en la misma maquina se pisarían los puertos)
			ss = new ServerSocket(0); 
			puerto = ss.getLocalPort();
			this.ruta = ruta;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		while(true) {
			try {
				Socket cliente = ss.accept();
				
				new Thread(()->{
					try {
						ObjectOutputStream salida = new ObjectOutputStream(cliente.getOutputStream());
						ObjectInputStream entrada = new ObjectInputStream(cliente.getInputStream());
						
						SolicitaArchivo solicitud = (SolicitaArchivo)entrada.readObject();
						File archivo = new File(ruta+solicitud.getNombreArchivo());
						FileInputStream fis = new FileInputStream(archivo);
						BufferedInputStream bis = new BufferedInputStream(fis);
						
						byte[] buffer = new byte[4096];
						int leidos;

						while ((leidos = bis.read(buffer)) != -1) {
						    FragmentoArchivo fragmento = new FragmentoArchivo(buffer.clone(), leidos, false);
						    salida.writeObject(fragmento);
						    salida.flush();
						}
						
						FragmentoArchivo fin = new FragmentoArchivo(new byte[0], 0, true);
					    salida.writeObject(fin);
					    salida.flush();
						
						bis.close();
						salida.close();
				        cliente.close();
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int getPort() {
		return puerto;
	}
	
}
