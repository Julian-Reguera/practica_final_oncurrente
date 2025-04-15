package servidor;

import java.util.HashSet;
import java.util.Set;

public class Usuario implements UsuarioConst{
	private String name;
	private Set<String> files;
	private boolean conectado;
	private String ip;
	private int port; //puerto para descargar archivos
	
	public Usuario(String nombre) {
		this.name = nombre;
		this.files = new HashSet<String>();
		this.conectado = false;
		this.ip = "";
		this.port = 0;
	}
	
	public String getName() {
		return name;
	}
	
	public void conectar(String ip, int port) {
		this.conectado = true;
		this.ip = ip;
		this.port = port;
	}
	
	public void desconectar() {
		this.conectado = false;
		this.ip = "";
		this.port = 0;
	}
	
	public boolean isConected() {
		return conectado;
	}
	
	public String getIp() {
		return ip;
	}
	
	public int getPort() {
		return port;
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
