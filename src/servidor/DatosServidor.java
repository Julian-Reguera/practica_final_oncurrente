package servidor;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import libreria.monitores.ReaderWriterLockMonitor;

public class DatosServidor {
	private volatile Map<String,Usuario> usuarios; //introduce nombre y devuelve el usuario
	private volatile Map<String,Usuario> conectados; //usuarios conectados
	
	private volatile Map<String,Set<Usuario>> archivos; //Archivos que pueden ser mandados por un usuario
	
	private ReaderWriterLockMonitor monitor; //controla los accesos a usuarios y archivos
	
	public DatosServidor() {
		usuarios = new HashMap<String,Usuario>();
		archivos = new HashMap<String,Set<Usuario>>();
		conectados = new HashMap<String,Usuario>();
		
		monitor = new ReaderWriterLockMonitor();
	}
	
	public UsuarioConst userConected(String name, Socket conection, ObjectOutputStream out, ObjectInputStream in) {
		monitor.request_write();
		Usuario u;
		
		if(usuarios.containsKey(name)) {
			u = usuarios.get(name);
			u.conectar(conection);
			
			//Los archivos que puede compartir
			for(String archivo: u.getFiles()) {
				if(!archivos.containsKey(archivo))
					archivos.put(name, new HashSet<Usuario>());
				archivos.get(archivo).add(u);
			}
		}
		else {
			u = new Usuario(name);
			u.conectar(conection);
			usuarios.put(name,u );
		}
		
		conectados.put(name, u);
		
		//Actualizo la memoria compartida
		usuarios = usuarios;
		archivos = archivos;
		conectados = conectados;
		
		monitor.release_write();
		
		return u;
	}
	
	public void userDisconect(String name) {
		monitor.request_write();
		Usuario u = usuarios.get(name);
		conectados.remove(u);
		u.desconectar();
		
		//Los archivos que ya no puede compartir
		for(String archivo:u.getFiles()) {
			Set<Usuario> tenedores = archivos.get(archivo);
			tenedores.remove(u);
			
			//si ninguna otra persona conectada puede compartir el archivo
			if(tenedores.size() == 0)
				archivos.remove(archivo);
		}
		
		//Actualizo la memoria compartida
		usuarios = usuarios;
		archivos = archivos;
		conectados = conectados;
		
		monitor.release_write();
	}
	
	public String[] getUsuariosConectados() {
		String salida[] = null;

		monitor.request_read();
		salida = conectados.keySet().toArray(new String[0]);
		monitor.release_read();
		
		return salida;
	}
	
	public String[] getArchivos() {
		String salida[] = null;
		
		monitor.request_read();
		salida = archivos.keySet().toArray(new String[0]);
		monitor.release_read();
		
		return salida;
	}
	
	public UsuarioConst getTenedorArchivo(String archivo) {
		Usuario u = null;
		
		monitor.request_read();
		if(archivos.containsKey(archivo))
			u = archivos.get(archivo).iterator().next(); //escoge al primer usuario que puede mandar el archivo
		monitor.release_read();
		
		return u;
	}
}
