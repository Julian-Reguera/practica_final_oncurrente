package mensajes;

public enum TipoMensaje {
CONEXION, CONFIRMACION_CONEXION, SOLICITAR_ARCHIVO, FRAGMENTO_ARCHIVO, PETICION_USUARIOS_CONECTADOS, ENVIO_USUARIOS_CONECTADOS, PRODUCTO_PREPARADO,
INICIO, CIERRE_CONEXION, ERROR, PETICION_CORRECTA, ANADIR_ARCHIVO, PEAR_TO_PEAR_LISTO, PEDIR_LISTA_ARCHIVOS, ENVIO_LISTA_ARCHIVOS
}

//CONEXION: inicia la conexion entre cliente y servidor
//CONFIRMACION_CONEXION: confirma la conexion si el usuario no está conectado ya
//SOLICITAR_ARCHIVO: el cliente solicita un archivo al servidor
//FRAGMENTO_ARCHIVO: En el pear to pear un cliente envia un fragmento del archivo al servidor
//PETICION_USUARIOS_CONECTADOS: el cliente solicita la lista de usuarios conectados al servidor
//PRODUCTO_PREPARADO: el cliente al que se le ha solicitado un producto indica en que puerto se inicia la red p2p
//INICIO: mensaje de conexion para la red p2p
//CIERRE_CONEXION: mensaje de cierre de conexion tanto para la red p2p como para el cliente-servidor
//PEAR_TO_PEAR_LISTO: el servidor avisa al cliente de a que puerto se tiene que conectar para recibir el archivo