package com.example;

public class BluetoothServer {	
	
	/**
	 * Bluetooth Server for Android to Java communication
	 * @param args
	 * @author Josh Whaley
	 */
	public static void main(String[] args) {
		Thread waitThread = new Thread(new WaitThread());
		waitThread.start();
	}

}
