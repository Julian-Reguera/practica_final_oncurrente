package servidor;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import libreria.monitores.ReaderWriterLockMonitor;

public class DatosServidor {
	private Map<String,Usuario> usuarios; //introduce nombre y devuelve el usuario
	private Map<String,Set<Usuario>> archivos; //Para cada archivo que usuarios conectados pueden mandarlo.
	private ReaderWriterLockMonitor monitor;
	
	public DatosServidor() {
		usuarios = new HashMap<String,Usuario>();
		archivos = new HashMap<String,Set<Usuario>>();
		monitor = new ReaderWriterLockMonitor();
	}
	
	public void userConected(String name, Socket conection) {
		if(usuarios.containsKey(name)) {
			usuarios.get(name).conectar(conection);
		}
	}
}
