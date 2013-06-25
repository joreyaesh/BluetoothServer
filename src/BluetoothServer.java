
public class BluetoothServer {

	/**
	 * @param args
	 * @author Josh Whaley
	 * Bluetooth test program for Android to Java communication
	 */
	public static void main(String[] args) {
		Thread waitThread = new Thread(new WaitThread());
		waitThread.start();

	}

}
