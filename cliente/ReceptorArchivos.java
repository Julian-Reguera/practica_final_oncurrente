package cliente;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import libreria.locks.LockBackery;
import libreria.locks.LockRompeEmpate;
import libreria.monitores.AlmacenSynchronizedMonitor;
import mensajes.AnadirArchivo;
import mensajes.FragmentoArchivo;
import mensajes.Mensaje;
import mensajes.MensajeInicio;
import mensajes.TipoMensaje;

public class ReceptorArchivos extends Thread{
    private AlmacenSynchronizedMonitor<Integer> manejadorIDs; //coge su ID para los locks y despues lo devuelve
    private ContextoReceptor contexto; //notifica cuando acaba
    private LockRompeEmpate lockConsola; //escribir mensaje al acabar de recibir el archivo
    private LockBackery lockEntradaSalida; //para poder usar outputServer
    private ObjectOutputStream outputServer; //para notificar el final de la descarga al servidor

    private String nombreArchivo;
    private String ipServidor;
    private int puertoServidor;
    private String nombreUsuario;

    public ReceptorArchivos(String rutaArchivo, String ipServidor, int puertoServidor, String nombreUsuario, ObjectOutputStream outputServer,
     ContextoReceptor contexto, LockRompeEmpate lockConsola, LockBackery lockEntradaSalida,AlmacenSynchronizedMonitor<Integer> manejadorIDs) {
        this.nombreArchivo = rutaArchivo;
        this.ipServidor = ipServidor;
        this.puertoServidor = puertoServidor;
        this.nombreUsuario = nombreUsuario;
        this.outputServer = outputServer;
        this.contexto = contexto;
        this.lockConsola = lockConsola;
        this.lockEntradaSalida = lockEntradaSalida;
        this.manejadorIDs = manejadorIDs;
    }

    public void run() {
        int id = manejadorIDs.extraer(); 

        contexto.lockReceptores.takeLock(id);
        if(contexto.programaAcabado){
            contexto.lockReceptores.releaseLock(id);
            manejadorIDs.almacenar(id);
            return; //si el programa ha terminado no se hace nada
        }
        else{
            contexto.numReceptores++;
            contexto.lockReceptores.releaseLock(id);
        }

        try (Socket socket = new Socket(ipServidor, puertoServidor);
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            FileOutputStream fileOutput = new FileOutputStream("clientes/"+nombreUsuario+"/"+nombreArchivo)) {

            output.writeObject(new MensajeInicio(nombreUsuario));
            boolean fin = false;
            boolean error = false;

            while (!fin) {
                Mensaje lectura = (Mensaje)input.readObject();
                if(lectura.getTipo() == TipoMensaje.ERROR) {
                    fileOutput.close();
                    new java.io.File("clientes/"+nombreUsuario+"/"+nombreArchivo).delete();
                    error = true;
                    break;
                }
                else{
                    FragmentoArchivo fragmento = (FragmentoArchivo) lectura;
                    int longitud = fragmento.getsize();
                    fin = fragmento.isLast();
                    byte[] buffer = fragmento.getData();
                    fileOutput.write(buffer);
                }
            }

            lockConsola.takeLock(id);
            if(error){
                System.out.println("HA HABIDO UN ERROR AL DESCARGAR EL ARCHIVO "+ nombreArchivo.toUpperCase());
            }
            else{
                System.out.println("EL ARCHIVO " + nombreArchivo.toUpperCase() + " SE HA DESCARGADO CORRECTAMENTE.");
            }
            lockConsola.releaseLock(id);

            if(!error){
                lockEntradaSalida.takeLock(id);
                outputServer.writeObject(new AnadirArchivo(nombreArchivo)); //notifica al servidor que ha terminado de recibir el archivo
                lockEntradaSalida.releaseLock(id);
            }

            contexto.lockReceptores.takeLock(id);
            contexto.numReceptores--;
            if(contexto.programaAcabado && contexto.numReceptores == 0)
                contexto.esperaTerminacion.release(); // notifico al hilo principal que ya han terminado todos los receptores
            contexto.lockReceptores.releaseLock(id);
        } catch (Exception e) {
            lockConsola.takeLock(id);
            System.err.println("Error al recibir el archivo: " + e.getMessage());
            lockConsola.releaseLock(id);
        }
        finally{
            manejadorIDs.almacenar(id); //libera el id para que lo pueda usar otro hilo
        }
    }
}
