package servidor;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

import mensajes.AnadirArchivo;
import mensajes.CierreConexion;
import mensajes.ConfirmacionConexion;
import mensajes.EnvioListaArchivos;
import mensajes.EnvioListaUsuarios;
import mensajes.Mensaje;
import mensajes.MensajeConexion;
import mensajes.MensajeError;
import mensajes.PearToPearListo;
import mensajes.PedirProducto;
import mensajes.PeticionCorrecta;
import mensajes.ProductoPreparado;
import mensajes.TipoMensaje;

public class OyenteCliente extends Thread{
    private Socket socket;
    private DatosServidor datosServidor;
    private Semaphore lockConsola;

    public OyenteCliente(Socket socket, Semaphore lockConsola, DatosServidor datosServidor) {
        this.datosServidor = datosServidor;
        this.socket = socket;
        this.lockConsola = lockConsola;
    }

    public void run(){
        try{
            String nombreUsuario;
            Semaphore semaforo;
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            MensajeConexion conexion = (MensajeConexion) input.readObject();

            datosServidor.monitor.request_read();
            if(datosServidor.conectados.containsKey(conexion.getNombre())){
                datosServidor.monitor.release_read();
                output.writeObject(new MensajeError("El usuario ya esta conectado"));
                input.close();
                output.close();
                socket.close();
                return;
            } 
            datosServidor.monitor.release_read();

            nombreUsuario = conexion.getNombre();
            conectarUsuario(nombreUsuario, output);

            lockConsola.acquire();
            System.out.println("El usuario " + nombreUsuario + " se ha conectado desde la IP " + socket.getInetAddress().getHostAddress() + " y el puerto " + socket.getPort());
            lockConsola.release();

            datosServidor.monitor.request_read();
            semaforo = datosServidor.conectados.get(nombreUsuario).lockSalida;
            semaforo.acquire();
            output.writeObject(new ConfirmacionConexion());
            semaforo.release();
            datosServidor.monitor.release_read();
            
            Mensaje mensajeRecibido = null;
            do{
                mensajeRecibido = (Mensaje) input.readObject();
                
                switch (mensajeRecibido.getTipo()) {
                    case PETICION_USUARIOS_CONECTADOS:
                        lockConsola.acquire();
                        System.out.println("El usuario " + nombreUsuario + " ha solicitado la lista de usuarios conectados.");
                        lockConsola.release();

                        datosServidor.monitor.request_read();
                        semaforo = datosServidor.conectados.get(nombreUsuario).lockSalida;
                        String[] usuariosConectados = datosServidor.conectados.keySet().toArray(new String[0]);
                        semaforo.acquire();
                        output.writeObject(new EnvioListaUsuarios(usuariosConectados));
                        semaforo.release();
                        datosServidor.monitor.release_read();
                        break;
                    
                    case ANADIR_ARCHIVO:
                        AnadirArchivo anadirArchivo = (AnadirArchivo) mensajeRecibido;

                        lockConsola.acquire();
                        System.out.println("El usuario " + nombreUsuario + " ha añadido el archivo " + anadirArchivo.getNombreArchivo() + ".");
                        lockConsola.release();
                        anadirArchivo(anadirArchivo.getNombreArchivo(), nombreUsuario);
                        break;
                    
                    case SOLICITAR_ARCHIVO:
                        lockConsola.acquire();
                        System.out.println("El usuario " + nombreUsuario + " ha solicitado el archivo " + ((PedirProducto) mensajeRecibido).getNombreProducto() + ".");
                        lockConsola.release();

                        pedirProducto((PedirProducto) mensajeRecibido, nombreUsuario);
                        break;

                    case PRODUCTO_PREPARADO:
                        ProductoPreparado productoPreparado = (ProductoPreparado) mensajeRecibido;

                        lockConsola.acquire();
                        System.out.println("El usuario " + nombreUsuario + " ha preparado el archivo " + productoPreparado.getNombreProducto() + ".");
                        lockConsola.release();

                        productoPreparado(productoPreparado, socket.getInetAddress().getHostAddress());
                        break;
                
                    case PEDIR_LISTA_ARCHIVOS:
                        lockConsola.acquire();
                        System.out.println("El usuario " + nombreUsuario + " ha solicitado la lista de archivos.");
                        lockConsola.release();
                        
                        datosServidor.monitor.request_read();
                        semaforo = datosServidor.conectados.get(nombreUsuario).lockSalida;
                        String[] archivosDisponibles = datosServidor.archivos.keySet().toArray(new String[0]);
                        semaforo.acquire();
                        output.writeObject(new EnvioListaArchivos(archivosDisponibles));
                        semaforo.release();
                        datosServidor.monitor.release_read();
                        break;
                    default:
                        break;
                }

            }while(mensajeRecibido.getTipo() != TipoMensaje.CIERRE_CONEXION);

            desconectarUsuario(nombreUsuario);

            //En este punto ningun otro hilo puede acceder al socket de salida del usuario
            output.writeObject(new CierreConexion());

            input.close();
            output.close();
            socket.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    private void pedirProducto(PedirProducto mensaje, String nombreUsuario){
        datosServidor.monitor.request_read();
        if(!datosServidor.archivos.containsKey(mensaje.getNombreProducto())){
            Semaphore semaforo = datosServidor.conectados.get(nombreUsuario).lockSalida;
            ObjectOutputStream salidaServidor = datosServidor.conectados.get(nombreUsuario).salida;

            try {
                semaforo.acquire();
                salidaServidor.writeObject(new MensajeError("Ninguna persona conectada tiene el archivo " + mensaje.getNombreProducto()));
                semaforo.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            Set<Usuario> posiblesUsuarios = datosServidor.archivos.get(mensaje.getNombreProducto());
            List<Usuario> usuarios = new java.util.ArrayList<>(posiblesUsuarios);
            Usuario usuario = usuarios.get((int) (Math.random() * usuarios.size())); //elijo un usuario al azar
            Usuario origen = datosServidor.conectados.get(nombreUsuario);

            ObjectOutputStream salidaServidor = usuario.salida;
            Semaphore semaforo = usuario.lockSalida;

            try {
                semaforo.acquire();
                salidaServidor.writeObject(new PedirProducto(mensaje.getNombreProducto(), nombreUsuario));
                semaforo.release();

                origen.lockSalida.acquire();
                origen.salida.writeObject(new PeticionCorrecta());
                origen.lockSalida.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        datosServidor.monitor.release_read();
    }

    private void productoPreparado(ProductoPreparado mensaje, String ipOrigen){
        datosServidor.monitor.request_read();

        Usuario destino = datosServidor.conectados.get(mensaje.getUsuarioDestino());
        try {
            destino.lockSalida.acquire();
            destino.salida.writeObject(new PearToPearListo(mensaje.getNombreProducto(), mensaje.getPuertoEscucha(), ipOrigen));
            destino.lockSalida.release();
        } catch (Exception e) {
            e.printStackTrace();
        }

        datosServidor.monitor.release_read();
    }

    private void anadirArchivo(String nombreArchivo, String nombreUsuario){
        datosServidor.monitor.request_write();
        datosServidor.conectados.get(nombreUsuario).archivos.add(nombreArchivo);

        if(datosServidor.archivos.containsKey(nombreArchivo)){
            Set<Usuario> usuarios = datosServidor.archivos.get(nombreArchivo);
            usuarios.add(datosServidor.conectados.get(nombreUsuario));
        }
        else{
            Set<Usuario> usuarios = new java.util.HashSet<>();
            usuarios.add(datosServidor.conectados.get(nombreUsuario));
            datosServidor.archivos.put(nombreArchivo, usuarios);
        }

        datosServidor.monitor.release_write();
    }

    private void conectarUsuario(String nombreUsuario, ObjectOutputStream salidaServidor){
        datosServidor.monitor.request_write();
        Usuario usuario;
        if(datosServidor.usuarios.containsKey(nombreUsuario))
            usuario = datosServidor.usuarios.get(nombreUsuario);
        else{
            usuario = new Usuario(nombreUsuario);
            datosServidor.usuarios.put(nombreUsuario, usuario);
        }

        usuario.conectado = true;
        usuario.salida = salidaServidor;
        datosServidor.conectados.put(nombreUsuario, usuario);

        for(String archivo: usuario.archivos){
            if(datosServidor.archivos.containsKey(archivo)){
                datosServidor.archivos.get(archivo).add(usuario);
            }
            else{
                Set<Usuario> usuarios = new java.util.HashSet<>();
                usuarios.add(usuario);
                datosServidor.archivos.put(archivo, usuarios);
            }
        }

        datosServidor.monitor.release_write();
    }

    private void desconectarUsuario(String nombreUsuario){
        datosServidor.monitor.request_write();

        Usuario usuario = datosServidor.conectados.get(nombreUsuario);
        if(usuario != null){
            usuario.conectado = false;
            datosServidor.conectados.remove(nombreUsuario);
            for(String archivo: usuario.archivos){
                Set<Usuario> usuarios = datosServidor.archivos.get(archivo);
                if(usuarios != null){
                    usuarios.remove(usuario);
                    if(usuarios.isEmpty()){
                        datosServidor.archivos.remove(archivo);
                    }
                }
            }
        }

        datosServidor.monitor.release_write();
    }
}
