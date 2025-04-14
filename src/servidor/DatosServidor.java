package servidor;

import java.net.Socket;
import java.util.Map;
import java.util.Set;

public class DatosServidor {
	private Map<String,Usuario> usuarios; //introduce nombre y devuelve el usuario
	private Map<String,Set<Usuario>> archivos; //Para cada archivo que usuarios conectados pueden mandarlo.
	
	public void userConected(String name, Socket conection) {
		if(usuarios.containsKey(name)) {
			usuarios.get(name).conectar(conection);
		}
	}
}
