package cliente;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import mensajes.FragmentoArchivo;
import mensajes.MensajeConexion;
import mensajes.SolicitaArchivo;

public class Cliente {
	private static final int puertoServidor = 1234;
	private static final String ipServidor = "localhost";

	private Socket conexion;
	private ObjectOutputStream salida;
	private ObjectInputStream entrada;

	private String nombre;
	private String rutaUsuario; // carpeta donde se guardan los archivos que se han compartido
	private MandadorArchivo mandador;

	public Cliente(String nombre) throws UnknownHostException, IOException {
		this.nombre = nombre;
		this.rutaUsuario = "./" + "files_" + nombre;

		conexion = new Socket(ipServidor, puertoServidor);
		salida = new ObjectOutputStream(conexion.getOutputStream());
		entrada = new ObjectInputStream(conexion.getInputStream());

		mandador = new MandadorArchivo(this.rutaUsuario);

		salida.writeObject(new MensajeConexion(nombre, mandador.getPort()));
	}

	private void descargarArchivo(String IP, int port, String nombre, String destino) {
		Socket socket = null;
		ObjectOutputStream salida = null;
		ObjectInputStream entrada = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		FileOutputStream guardadoF = null;
		BufferedOutputStream guardado = null;

		try {
			socket = new Socket(IP, port);

			salida = new ObjectOutputStream(socket.getOutputStream());
			entrada = new ObjectInputStream(socket.getInputStream());

			// Enviar solicitud de archivo
			SolicitaArchivo solicitud = new SolicitaArchivo(nombre);
			salida.writeObject(solicitud);
			salida.flush();

			// Crea archivo de destino
			fos = new FileOutputStream(destino);
			bos = new BufferedOutputStream(fos);

			guardadoF = new FileOutputStream(rutaUsuario + nombre);
			guardado = new BufferedOutputStream(fos);

			// Recibir fragmentos
			FragmentoArchivo fragmento;
			do {
				fragmento = (FragmentoArchivo) entrada.readObject();
				if (fragmento.getsize() > 0) {
					bos.write(fragmento.getData(), 0, fragmento.getsize());
					guardado.write(fragmento.getData(), 0, fragmento.getsize()); // copia de seguridad para cuando la
																					// pidan otros nodos
				}
			} while (!fragmento.isLast());

			System.out.println("Archivo descargado correctamente en: " + destino);

		} catch (FileNotFoundException e) {
			System.err.println("Error: No se pudo crear el archivo en el destino: " + destino);
		} catch (UnknownHostException | ConnectException e) {
			System.err.println("Error: No se ha conseguido conectar con el cliente en la direccion " + IP + ":" + port);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bos != null)
					bos.close();
				if (guardado != null)
					guardado.close();
				if (entrada != null)
					entrada.close();
				if (salida != null)
					salida.close();
				if (socket != null && !socket.isClosed())
					socket.close();
			} catch (IOException e) {
				System.err.println("Error cerrando recursos: " + e.getMessage());
			}
		}
	}
}
