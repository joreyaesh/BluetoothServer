
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class WaitThread implements Runnable{

	/** Constructor */
	public WaitThread() {
	}
	
	@Override
	public void run() {
		waitForConnection();		
	}
	
	/** Waiting for connection from devices */
	private void waitForConnection() {
		// retrieve the local Bluetooth device object
		LocalDevice local = null;
		
		StreamConnectionNotifier notifier;
		StreamConnection connection = null;
		
		// setup the server to listen for connection
		try {
			local = LocalDevice.getLocalDevice();
			// Generate QR code based on the local device's address
			QRImage.create(local.getBluetoothAddress());
			// Display the QR code on the screen
			UI ui = new UI();
			ui.run();
			
			local.setDiscoverable(DiscoveryAgent.GIAC);
			
			UUID uuid = new UUID(2852401); // "002b8631-0000-1000-8000-00805f9b34fb"
			String url = "btspp://localhost:" + uuid.toString() + ";name=BluetoothRemote";
			notifier = (StreamConnectionNotifier)Connector.open(url);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		// get screen size
		Rectangle virtualBounds = new Rectangle();
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        for (int j = 0; j < gs.length; j++) {
            GraphicsDevice gd = gs[j];
            GraphicsConfiguration[] gc = gd.getConfigurations();
            for (int i = 0; i < gc.length; i++) {
                virtualBounds = virtualBounds.union(gc[i].getBounds());
            }
        }
		
       	// waiting for connection
		while(true) {
			try {
				System.out.println("Waiting for connection...");
	            connection = notifier.acceptAndOpen();

				Thread processThread = new Thread(new ProcessConnectionThread(virtualBounds, connection));
				processThread.start();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}
}