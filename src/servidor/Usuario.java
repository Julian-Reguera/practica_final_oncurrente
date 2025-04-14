package servidor;

import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Usuario {
	private String name;
	private Set<String> files;
	private Socket conection;
	
	public Usuario(String nombre) {
		this.name = nombre;
		conection = null;
		files = new HashSet<String>();
	}
	
	public String getName() {
		return name;
	}
	
	public void conectar(Socket conexion) {
		this.conection = conexion;
	}
	
	public void desconectar() {
		this.conection = null;
	}
	
	public boolean isConected() {
		return !(conection == null);
	}
	
	public Socket getConection() {
		return conection;
	}
	
	public void addFile(String archivo) {
		if(!files.contains(archivo)) {
			files.add(archivo);
		}
	}
	
	public void removeFile(String archivo) {
		if(files.contains(archivo)) {
			files.remove(archivo);
		}
	}
	
	public boolean hasFile(String archivo) {
		return files.contains(archivo);
	}
	
	public Iterable<String> getFiles() {
	    return files;
	}
}
