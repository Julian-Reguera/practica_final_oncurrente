package cliente;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ContentHandlerFactory;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

import libreria.almacenes.AlmacenSemaphoreUnico;
import libreria.locks.LockBackery;
import libreria.locks.LockRompeEmpate;
import libreria.monitores.AlmacenSynchronizedMonitor;
import mensajes.CierreConexion;
import mensajes.Mensaje;
import mensajes.MensajeConexion;
import mensajes.MensajeError;
import mensajes.PedirListaArchivos;
import mensajes.PedirListaConectados;
import mensajes.PedirProducto;
import mensajes.TipoMensaje;

public class Cliente {

    private static final int PUERTO = 1234;

    private AlmacenSemaphoreUnico<String> consola; //permite esperar a que llegue un mensaje del servidor
    private AlmacenSynchronizedMonitor<Integer> manejadorIDs; //Se encargara de dar los IDs a los hilos.
    private LockBackery lockEntradaSalida; //permite poder enviar mensajes al servidor desde varios hilos
    private LockRompeEmpate lockConsola; //Controla la consola y solo se va a usar para los hilos que descargan información. 
    private ContextoReceptor contextoReceptor; //permite esperara los receptores antes de acabar el programa

    private String nombreUsuario; //nombre del usuario que se conecta al servidor

    public Cliente() {
        this.consola = new AlmacenSemaphoreUnico<>();
        iniciarManejadorIDs(25); //como maximo 25 hilos
        this.lockEntradaSalida = new LockBackery(25);
        this.lockConsola = new LockRompeEmpate(25);
        this.contextoReceptor = new ContextoReceptor(25);
    }

    public void ejecutar(){
        Scanner scanner = new Scanner(System.in);
        int id = manejadorIDs.extraer();

        System.out.print("Introduce la ip del servidor: ");
        String host = scanner.nextLine();

        System.out.print("Introduzca su nombre de usuario: ");
        nombreUsuario = scanner.nextLine();

        java.io.File directorioUsuario = new java.io.File("clientes");
        if (!directorioUsuario.exists()) {
            if (!directorioUsuario.mkdir()) {
                System.err.println("No se pudo crear el directorio para los usuarios.");
                return;
            }
        }

        directorioUsuario = new java.io.File("clientes/"+nombreUsuario);
        if (!directorioUsuario.exists()) {
            if (!directorioUsuario.mkdir()) {
                System.err.println("No se pudo crear el directorio para el usuario.");
                return;
            }
        }

        try(Socket conexion = new Socket(host, PUERTO);
            ObjectInputStream input = new ObjectInputStream(conexion.getInputStream());
            ObjectOutputStream output = new ObjectOutputStream(conexion.getOutputStream());) {

            System.out.println("Esperando a que el servidor acepte la conexion...");
            output.writeObject(new MensajeConexion(nombreUsuario)); //se envia el mensaje de conexion al servidor
            Mensaje primerMensaje = (Mensaje)input.readObject();

            if(primerMensaje.getTipo() == TipoMensaje.ERROR){
                System.err.println(((MensajeError)primerMensaje).getErrorMessage());
                return;
            }else
                System.out.println("Conexion con el servidor establecida");

            //LA CONCURRENCIA EMPIEZA EN ESTE PUNTO (La primera conexion se hace antes)
            OyenteServidor oyente = new OyenteServidor(manejadorIDs, consola, lockEntradaSalida, input, output, nombreUsuario, lockConsola, contextoReceptor);
            oyente.start(); //se inicia el hilo que escucha al servidor
            
            int opcion = -1;
            while (opcion != 5) {

                lockConsola.takeLock(id);
                imprimirOpciones();
                opcion = scanner.nextInt();
                scanner.nextLine(); // Consumir el salto de línea
                lockConsola.releaseLock(id);

                String resultado;

                switch (opcion) {
                    case 1:
                        lockEntradaSalida.takeLock(id); 
                        output.writeObject(new PedirListaConectados());
                        lockEntradaSalida.releaseLock(id);
                        
                        resultado = consola.extraer();

                        lockConsola.takeLock(id);
                        System.out.println(resultado); 
                        lockConsola.releaseLock(id);
                        break;
                    case 2:
                        lockEntradaSalida.takeLock(id); 
                        output.writeObject(new PedirListaArchivos());
                        lockEntradaSalida.releaseLock(id);
                        
                        resultado = consola.extraer();

                        lockConsola.takeLock(id);
                        System.out.println(resultado); 
                        lockConsola.releaseLock(id);
                        break;
                    case 3:
                        String rutaArchivo = null;
                        String nombre = null;

                        lockConsola.takeLock(id);
                        System.out.print("Introduce la ruta completa del archivo a compartir: ");
                        rutaArchivo = scanner.nextLine();
                        lockConsola.releaseLock(id);

                        //Primero se copia el archivo al directorio de archivos compartidos del usuario
                        nombre = copiarArchivo(rutaArchivo, nombreUsuario);

                        if(nombre != null){
                            //Una vez añadido el archivo al directorio se notifica al servidor
                            lockEntradaSalida.takeLock(id);
                            output.writeObject(new mensajes.AnadirArchivo(nombre));
                            lockEntradaSalida.releaseLock(id);
                        }
                        else{
                            lockConsola.takeLock(id);
                            System.out.println("El archivo no existe o no se ha podido copiar.");
                            lockConsola.releaseLock(id);
                        }

                        break;
                    case 4:
                        lockConsola.takeLock(id);
                        System.out.print("Introduce el nombre del archivo a descargar: ");
                        String nombreArchivo = scanner.nextLine();
                        lockConsola.releaseLock(id);

                        lockEntradaSalida.takeLock(id);
                        output.writeObject(new PedirProducto(nombreArchivo, nombreUsuario)); 
                        lockEntradaSalida.releaseLock(id);

                        resultado = consola.extraer(); //si el archivo existe o no

                        lockConsola.takeLock(id);
                        System.out.println(resultado); 
                        lockConsola.releaseLock(id);
                        break;
                    default:
                        break;
                }
            }

            lockConsola.takeLock(id);
            System.out.println("Esperando a que se acaben todas las descargas...");
            lockConsola.releaseLock(id);

            contextoReceptor.lockReceptores.takeLock(id);
            contextoReceptor.programaAcabado = true;
            if(contextoReceptor.numReceptores != 0){
                contextoReceptor.lockReceptores.releaseLock(id);
                contextoReceptor.esperaTerminacion.acquire();
            }
            else{
                contextoReceptor.lockReceptores.releaseLock(id);
            }
            
            lockConsola.takeLock(id);
            System.out.println("Todas las descargas han terminado.");
            lockConsola.releaseLock(id);
            
            //Mando el mensaje de cierre de conexion
            lockEntradaSalida.takeLock(id);
            output.writeObject(new CierreConexion()); 
            lockEntradaSalida.releaseLock(id);
            
            //espero a que el hilo que escucha al servidor reciba la confimacion
            oyente.join();
        }catch(Exception e){
            System.err.println(e.getMessage());
        }
    }

    private void iniciarManejadorIDs(int numHilosMax){
        manejadorIDs = new AlmacenSynchronizedMonitor<>(numHilosMax);

        //Se almacenan todos los IDs disponibles que posteriormente extraen los hilos
        for(int i = 0; i < numHilosMax; i++){
            manejadorIDs.almacenar(i);
        }
    }

    private void imprimirOpciones(){
        System.out.println("1. Ver usuarios conectados");
        System.out.println("2. Ver lista de archivos descargables");
        System.out.println("3. Añadir archivo compartido");
        System.out.println("4. Descargar archivo");
        System.out.println("5. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private String copiarArchivo(String nombreArchivo, String nombreUsuario) {
        java.io.File archivoOrigen = new java.io.File(nombreArchivo);
        String nombre = archivoOrigen.getName();

        if (!archivoOrigen.exists()) {
            return null;
        }

        java.io.File archivoDestino = new java.io.File("clientes/"+nombreUsuario + "/" + archivoOrigen.getName());

        try (java.io.FileInputStream fis = new java.io.FileInputStream(archivoOrigen);
             java.io.FileOutputStream fos = new java.io.FileOutputStream(archivoDestino)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
            fos.write(buffer,0, bytesRead);
            }

            return nombre;
        } catch (IOException e) {
            return null;
        }

    }
}
