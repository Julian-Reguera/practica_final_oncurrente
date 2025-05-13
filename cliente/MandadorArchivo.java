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
import mensajes.MensajeError;
import mensajes.MensajeInicio;

public class MandadorArchivo extends Thread {
    private String rutaArchivo;
    private String nombreUsuarioEntrante;
    private ServerSocket serverSocket;
    
    public MandadorArchivo(String rutaArchivo, String nombreUsuario, ServerSocket serverSocket) {
        this.rutaArchivo = rutaArchivo;
        this.nombreUsuarioEntrante = nombreUsuario;
        this.serverSocket = serverSocket;
    }
    
    public void run() {
        ServerSocket ss = serverSocket;
        Socket cliente;
        String nombreRecibido="";

        try {

            while(!nombreRecibido.equals(nombreUsuarioEntrante)) {
                    cliente = ss.accept();
                    
                    ObjectOutputStream salida = new ObjectOutputStream(cliente.getOutputStream());
                    salida.flush();
                    ObjectInputStream entrada = new ObjectInputStream(cliente.getInputStream());
                    
                    MensajeInicio solicitud = (MensajeInicio)entrada.readObject();
                    nombreRecibido = solicitud.getNombreUsuario();
                    if(nombreRecibido.equals(nombreUsuarioEntrante)) {
                       mandarArchivo(salida);
                    } else {
                        salida.writeObject(new MensajeError("El usuario no es el correcto."));
                        salida.flush();
                        cliente.close();
                        continue;
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mandarArchivo(ObjectOutputStream salida) throws IOException {
        File archivo = new File(rutaArchivo);
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
        fis.close();
    }
}
