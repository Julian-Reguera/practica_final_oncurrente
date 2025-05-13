package servidor;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import libreria.monitores.ReaderWriterLockMonitor;

public class DatosServidor {
    public ReaderWriterLockMonitor monitor; //controla los accesos a usuarios y archivos
    
    public Map<String,Usuario> usuarios; //introduce nombre y devuelve el usuario
	public Map<String,Usuario> conectados; //usuarios conectados
    public Map<String,Set<Usuario>> archivos;

    public DatosServidor(String archivoInicializacion) {
        this.monitor = new ReaderWriterLockMonitor();
        this.usuarios = new java.util.HashMap<>();
        this.conectados = new java.util.HashMap<>();
        this.archivos = new java.util.HashMap<>();

        cargarDatos(archivoInicializacion);
    }

	/*
	 * El archivo de inicialización contiene la información de los usuarios y sus archivos
	 * 
	 * El archivo empieza con un Int(n) que indica el número de usuarios
	 * En la siguiente línea se encuentran n enteros que indican el número de archivos de cada usuario
	 * En la siguiente linea se encuentran n cadenas que indican el nombre de cada usuario
	 * En la siguientes lineas se encuentran las cadenas de texto que indican los nombres de los archivos de cada usuario
	 */
	private void cargarDatos(String archivoInicializacion) {
		try (Scanner scanner = new Scanner(new File(archivoInicializacion))) {
			int numeroUsuarios = scanner.nextInt();
			int[] numeroArchivosPorUsuario = new int[numeroUsuarios];
			String[] nombresUsuarios = new String[numeroUsuarios];

			// Leer número de archivos por usuario
			for(int i = 0; i< numeroUsuarios;i++){
				numeroArchivosPorUsuario[i] = scanner.nextInt();
			}

			// Leer nombres de usuarios
			for(int i = 0; i< numeroUsuarios;i++){
				nombresUsuarios[i] = scanner.next();
				usuarios.put(nombresUsuarios[i], new Usuario(nombresUsuarios[i]));
			}

			for(int i = 0; i< numeroUsuarios;i++){
				for(int j = 0; j< numeroArchivosPorUsuario[i];j++){
					String archivo = scanner.next();
                    usuarios.get(nombresUsuarios[i]).archivos.add(archivo); 
				}
			}

		} catch (IOException | NumberFormatException e) {
			e.printStackTrace();
		}
	}
}
