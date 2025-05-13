package mensajes;

public class FragmentoArchivo extends Mensaje{

    private byte[] data;
	private int size;
	private boolean last;
	
	public FragmentoArchivo(byte[] data, int size, boolean last) {
		super(TipoMensaje.FRAGMENTO_ARCHIVO);
		this.data = data;
		this.size = size;
		this.last = last;
	}
	
	public int getsize() {
		return size;
	}
	
	public boolean isLast() {
		return last;
	}
	
	public byte[] getData() {
		return data;
	}

}
