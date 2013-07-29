package com.example;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.IOException;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class WaitThread implements Runnable{
	// JFrame which will show the QR code
	UI ui = new UI();

	/** Constructor */
	public WaitThread() {
	}
	
	@Override
	public void run() {
		waitForConnection();		
	}
	
	/** Waiting for connection from devices */
	private void waitForConnection() {
		// Retrieve the local Bluetooth device object
		LocalDevice local = null;
		
		StreamConnectionNotifier notifier;
		StreamConnection connection = null;
		
		// Setup the server to listen for connection
		try {
			local = LocalDevice.getLocalDevice();
			// Generate QR code based on the local device's address
			QRImage.create(local.getBluetoothAddress());
			// Display the QR code on the screen
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
		
       	// Waiting for connection
		while(true) {
			try {
				if(!ProcessConnectionThread.isActive){
					ui.restore();
					System.out.println("Waiting for connection...");
		            connection = notifier.acceptAndOpen();
					Thread processThread = new Thread(new ProcessConnectionThread(virtualBounds, connection));
					ui.minimize();
					processThread.start();
				}
			} catch (IOException e) {
				e.printStackTrace();
				return;
			} 
			
			// Only check if the connection is active every 5 seconds
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
	}
}