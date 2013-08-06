package com.example;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;

import javax.microedition.io.StreamConnection;

public class ProcessConnectionThread implements Runnable{

	private StreamConnection mConnection;
	
	// Constants that indicate command from devices
	public static final int CHECK_CONNECTION = -1;
	public static final int MOUSE_LEFT = 1;
	public static final int MOUSE_RIGHT = 2;
	public static final int MOUSE_SCROLL = 5;
	public static final int MOVE_MOUSE_BY = 10;
	public static final int TYPE = 15;	
	public static final int BACKSPACE = 25;
	public static final int ENTER = 20;
	public static final int NEW_TAB = 30;
	public static final int SHARE_IMAGE = 50;
	public static final int SHARE_IMAGE_FILE_SIZE = 51;
	
	// Robot to control the computer
	private Robot robot = null;
	
	// Rectangle representing the screen dimensions
	Rectangle rectScreen;
	
	// Parameters used to track touches on the connected Android device
	static int x = 0;
	static int y = 0;
	
	// Parameter to hold file size of incoming file
	static int fileSize = 0;
	
	// Boolean to keep track of whether or not the bluetooth connection is active
	public static boolean isActive = false;
	
	// InputStream
	InputStream inputStream = null;
	
	public ProcessConnectionThread(Rectangle screen, StreamConnection connection)
	{
		rectScreen = screen;
		mConnection = connection;
	}
	
	private void connectionLost(){
		System.out.println("Connection lost...");
		//robot.keyPress(KeyEvent.VK_ALT);
		//robot.keyPress(KeyEvent.VK_F4);
		//robot.keyRelease(KeyEvent.VK_F4);
		//robot.keyRelease(KeyEvent.VK_ALT);
		isActive = false;
	}
	
	/**
	 * Processes the RemoteCommand object from the {@link InputStream}
	 */
	@Override
	public void run() {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		try {
			// prepare to receive data
			inputStream = mConnection.openDataInputStream();
			System.out.println("Connected!");
			isActive = true;
			while (isActive) {
				byte[] data = new byte[1024];
				try{
					inputStream.read(data);
				}catch(NullPointerException e){
					e.printStackTrace();	
				}catch(IOException e2){
					e2.printStackTrace();
				}
				try{
					RemoteCommand rc = RemoteCommand.getRemoteCommand(data);
					processCommand(rc, data);
				} catch (StreamCorruptedException e){
					// Handle an incoming image
					processCommand(new RemoteCommand(SHARE_IMAGE), data);
				}
				
			}
		} catch (IOException e) {
			//e.printStackTrace();
			System.err.println("Error downloading object.");
		} 
	}
	
	/**
	 * Process the command from client
	 * @param command the command code
	 */
	private void processCommand(RemoteCommand rc, byte[] data) {
		try {
			switch (rc.command) {
			case MOUSE_RIGHT:
				rightClick();
				//System.out.println("Right");
				break;
			case MOUSE_LEFT:
				leftClick();
				//System.out.println("Left");
				break;
			case ENTER:
				enter();
				//System.out.println("Enter");
				break;
			case TYPE:
				type(rc.string1);
				//System.out.println("Type");
				break;
			case MOUSE_SCROLL:
				scroll(rc.parameter1);
				//System.out.println("Scroll");
				break;
			case MOVE_MOUSE_BY:
				moveMouse(rc.parameter1, rc.parameter2);
				//System.out.println("Move Mouse");
				break;
			case BACKSPACE:
				backspace();
				//System.out.println("Backspace");
				break;
			case NEW_TAB:
				newTab();
				//System.out.println("New Tab");
				break;
			case CHECK_CONNECTION:
				// Do nothing
				break;
			case SHARE_IMAGE_FILE_SIZE:
				fileSize = rc.parameter1;
				break;
			case SHARE_IMAGE:
				handlePicture(fileSize);
				break;

			default:
				break;
			}
		} catch(NullPointerException e) {
			connectionLost();
		}
	}
	
//	private void handlePicture(byte[] data) {
//		try {
//			File file = new File("tmp.jpg");
//			if(!file.delete())
//				file.createNewFile();
//			FileOutputStream fos = new FileOutputStream(file);
//			fos.flush();
//			fos.write(data, 0, 3101);
//			
//			fos.flush();
//			fos.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		// TODO display the file
//	}
	
	private void handlePicture(int fileSize) {
		try {
			File file = new File("tmp.jpg");
        	if(!file.delete())
				file.createNewFile();
            OutputStream out = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead = 0, counter = 0;
 
            while ((bytesRead = inputStream.read(buffer)) != -1) {
            	out.write(buffer, 0, bytesRead);
            	counter += bytesRead;
            	System.out.println("total bytes read: " + counter);
            	if(counter >= fileSize){
            		break;
            	}
            }
 
            System.out.println("Download Successfully!");
            System.out.println("Filesize: " + fileSize);
            fileSize = 0;
            out.flush();
            out.close();
 
        } catch (Exception e) {
        	e.printStackTrace();
            System.out.println("Error on downloading file!");
        }
    }

	private void newTab(){
		// simulates a CTRL+T event
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_T);
		robot.keyRelease(KeyEvent.VK_T);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.delay(100);
	}
	
	private void backspace() {
		// simulates a CTRL+Backspace event
		robot.delay(20);
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_BACK_SPACE);
		robot.keyRelease(KeyEvent.VK_BACK_SPACE);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		
	}

	private void enter() {
		// presses the enter key
		robot.delay(20);
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
		
	}

	private void moveMouse(int dx, int dy) {
		// get current position
		Point p0 = MouseInfo.getPointerInfo().getLocation();

		// determine if new position is on the screen
		Point p1 = p0;
		p1.translate(dx, dy);
		if (rectScreen.contains(p1)) {
			x += dx;
			y += dy;
			robot.mouseMove(x, y);
		} else { // if not, move to corner
			robot.mouseMove(Math.min(p1.x, rectScreen.width),
					Math.min(p1.y, rectScreen.height));
		}
	}


	private void leftClick() {
		robot.mousePress(InputEvent.BUTTON1_MASK);
		robot.delay(20);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
		robot.delay(20);
	}

	private void rightClick() {
		robot.mousePress(InputEvent.BUTTON3_MASK);
		robot.delay(20);
		robot.mouseRelease(InputEvent.BUTTON3_MASK);
		robot.delay(20);
	}
	
	private void scroll(int i) {
		if(i>0){
			robot.mouseWheel(i);
		}
		else if(i<0){
			robot.mouseWheel(i);
		}
	}
	
	private void type(String s) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    StringSelection characters = new StringSelection(s);
	    ClipboardOwner instance = null;
		clipboard.setContents(characters, instance);

	    robot.keyPress(KeyEvent.VK_CONTROL);
	    robot.keyPress(KeyEvent.VK_V);
	    robot.keyRelease(KeyEvent.VK_V);
	    robot.keyRelease(KeyEvent.VK_CONTROL);
	}
}