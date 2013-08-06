package com.example;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;

public class RemoteCommand implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1111L;
	/**
	 * 
	 */
	public int command = 0;
	public int parameter1 = 0; // X coordinate change/scroll amount/file size
	public int parameter2 = 0; // Y coordinate change
	public String string1 = ""; // String for textbox

	public RemoteCommand() {
	}
	
	public RemoteCommand(int command){
		this.command = command;
	}

	public static RemoteCommand getRemoteCommand(byte data[]) throws StreamCorruptedException {
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(bis);
			RemoteCommand ret = (RemoteCommand) in.readObject();
			return ret;
		} 
		catch (IOException e) {
			// read the stream header to check if connection is lost, or image was received
			for(int i = 0; i < 8; i++){
				if(bis.read() != 0){ // if any of the header bytes != 0, an image was received
					throw new StreamCorruptedException();
				}
			}
		} 
		catch (ClassNotFoundException e) {}
		try {
			bis.close();
			if(in != null)in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return null;
	}


	public byte[] getByteArray() {
		byte[] retval = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(this);
			return bos.toByteArray();
		} catch (IOException e) {
			//e.printStackTrace();
		} finally {
			try {
				out.close();
				bos.close();
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
		return retval;
	}
}
