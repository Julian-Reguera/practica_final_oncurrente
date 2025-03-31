package locks;

public interface LockId {
	public void takeLock(int id);
	public void releaseLock(int id);
}
