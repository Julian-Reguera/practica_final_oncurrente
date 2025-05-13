package cliente;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;

import libreria.almacenes.AlmacenSemaphoreUnico;
import libreria.locks.LockBackery;
import libreria.locks.LockRompeEmpate;
import libreria.monitores.AlmacenSynchronizedMonitor;
import mensajes.EnvioListaArchivos;
import mensajes.EnvioListaUsuarios;
import mensajes.Mensaje;
import mensajes.MensajeError;
import mensajes.MensajeInicio;
import mensajes.PearToPearListo;
import mensajes.PedirProducto;
import mensajes.ProductoPreparado;
import mensajes.TipoMensaje;

public class OyenteServidor extends Thread{
    private AlmacenSynchronizedMonitor<Integer> manejadorIDs;
    private AlmacenSemaphoreUnico<String> almacen = null; //permite avisar al cliente de que ya se ha recibido el mensaje del servidor
    private LockBackery lockEntradaSalida; //Este lock que permite controlar que 2 hilos no envien mensajes al servidor al mismo tiempo

    // variables para pasar a los receptores
    private LockRompeEmpate lockConsola; 
    private ContextoReceptor contextoReceptor;

    private ObjectInputStream input = null; //permite leer mensajes del servidor
    private ObjectOutputStream output = null; //permite enviar mensajes al servidor
    private String nombreUsuario; //nombre del usuario que se conecta al servidor

    public OyenteServidor(AlmacenSynchronizedMonitor<Integer> manejadorIDs, AlmacenSemaphoreUnico<String> almacen, LockBackery lockEntradaSalida,
        ObjectInputStream input, ObjectOutputStream output, String nombreUsuario, LockRompeEmpate lockConsola, ContextoReceptor contextoReceptor) {
        this.manejadorIDs = manejadorIDs;
        this.almacen = almacen;
        this.lockEntradaSalida = lockEntradaSalida;
        this.input = input;
        this.output = output;
        this.nombreUsuario = nombreUsuario;
        this.lockConsola = lockConsola;
        this.contextoReceptor = contextoReceptor;
    }

    public void run(){
        int id = manejadorIDs.extraer();

        try{
            Mensaje mensaje = null;
        
            do{
                mensaje = (Mensaje) input.readObject();
                switch (mensaje.getTipo()) {
                    case ERROR:
                        MensajeError mensajeError = (MensajeError) mensaje;
                        almacen.almacenar("ERROR: "+ mensajeError.getErrorMessage());
                        break;
                    case PETICION_CORRECTA:
                        almacen.almacenar("Peticion correcta, en breves se iniciara la descarga del archivo.");
                        break;
                    case SOLICITAR_ARCHIVO:
                        crearEmisor((PedirProducto) mensaje, id);
                        break;
                    case PEAR_TO_PEAR_LISTO:
                        PearToPearListo pearToPear = (PearToPearListo) mensaje;
                        ReceptorArchivos receptor = new ReceptorArchivos(pearToPear.getNombreProducto(), pearToPear.getIpDestino(), pearToPear.getPuertoEscucha(), nombreUsuario, output, contextoReceptor, lockConsola, lockEntradaSalida, manejadorIDs);
                        receptor.start();
                        break;
                    case ENVIO_USUARIOS_CONECTADOS:
                        EnvioListaUsuarios mensajeListaUsuarios = (EnvioListaUsuarios) mensaje;
                        almacen.almacenar(formatearListaUsuarios(mensajeListaUsuarios.getListaUsuariosConectados()));
                        break;
                    case ENVIO_LISTA_ARCHIVOS:
                        EnvioListaArchivos mensajeListaArchivos = (EnvioListaArchivos) mensaje;
                        almacen.almacenar(formatearListaArchivos(mensajeListaArchivos.getListaArchivos()));
                        break;
                    default:
                        break;
                }
            }while(mensaje.getTipo() != TipoMensaje.CIERRE_CONEXION);
        }catch(Exception e){
            e.printStackTrace();
        }
        finally{
            manejadorIDs.almacenar(id); //libera el id para que lo pueda usar otro hilo
        }
    }

    private void crearEmisor(PedirProducto mensaje, int id){
        try {
            ServerSocket serverSocket = new ServerSocket(0); //crea un socket para recibir el archivo

            lockEntradaSalida.takeLock(id); 
            output.writeObject(new ProductoPreparado(mensaje.getNombreProducto(), serverSocket.getLocalPort(), mensaje.getNombreUsuario())); //envia el mensaje al servidor para que sepa que el cliente esta preparado para recibir el archivo
            lockEntradaSalida.releaseLock(id);

            MandadorArchivo emisor = new MandadorArchivo("clientes/" + nombreUsuario+"/"+mensaje.getNombreProducto(), mensaje.getNombreUsuario(), serverSocket);
            emisor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String formatearListaUsuarios(String[] listaUsuariosConectados) {
        StringBuilder sb = new StringBuilder();
        sb.append("Lista de usuarios conectados:\n");
        for (String usuario : listaUsuariosConectados) {
            sb.append("\t").append(usuario).append("\n");
        }

        return sb.toString();
    }

    private String formatearListaArchivos(String[] listaArchivos){
        StringBuilder sb = new StringBuilder();
        sb.append("Lista de archivos disponibles: ");
        for (String archivo : listaArchivos) {
            sb.append(archivo).append(", ");
        }

        sb.append("\n");

        return sb.toString();
    }
}
