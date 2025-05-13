package servidor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class Usuario{
    public String nombre;
    public Set<String> archivos;
    public ObjectOutputStream salida;
    public boolean conectado;
    public Semaphore lockSalida; //lock para que 2 procesos no puedan escribir al mismo tiempo en la salida del socket

    public Usuario(String nombre){
        this.nombre = nombre;
        this.archivos = new java.util.HashSet<>();
        this.conectado = false;
        this.lockSalida = new Semaphore(1);
    }
}
