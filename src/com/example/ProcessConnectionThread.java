package com.example;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.microedition.io.StreamConnection;

public class ProcessConnectionThread implements Runnable{

	private StreamConnection mConnection;
	
	// Constants that indicate command from devices
	private static final int CHECK_CONNECTION = -1;
	private static final int TYPE = 0;
	private static final int MOUSE_LEFT = 1;
	private static final int MOUSE_RIGHT = 2;
	private static final int MOUSE_SCROLL = 5;
	private static final int MOVE_MOUSE_BY = 10;
	private static final int BACKSPACE = 25;
	private static final int ENTER = 20;
	private static final int NEW_TAB = 30;
	
	// Robot to control the computer
	Robot robot = null;
	
	// Rectangle representing the screen dimensions
	Rectangle rectScreen;
	
	// Parameters used to track touches on the connected Android device
	static int x = 0;
	static int y = 0;
	
	// Boolean to keep track of whether or not the bluetooth connection is active
	public static boolean isActive = false;
	
	public ProcessConnectionThread(Rectangle screen, StreamConnection connection)
	{
		rectScreen = screen;
		mConnection = connection;
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
			InputStream inputStream = mConnection.openDataInputStream();
			System.out.println("Connected!");
			isActive = true;
			byte[] data = new byte[1024];
			while (isActive) {
				Arrays.fill(data, (byte) 0);
				try{
					inputStream.read(data);
				}catch(NullPointerException e){
					e.printStackTrace();
				}catch(IOException e2){
					e2.printStackTrace();
				}
				RemoteCommand rc = RemoteCommand.getRemoteCommand(data);
				processCommand(rc);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * Process the command from client
	 * @param command the command code
	 */
	private void processCommand(RemoteCommand rc) {
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

			default:
				break;
			}
		} catch(NullPointerException e) {
			System.out.println("Connection lost...");
			isActive = false;
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
		byte[] bytes = s.getBytes();
		for (byte b : bytes) {
			int code = b;
			if (code > 96 && code < 123){ // Lower case
				code = code - 32;
			}
			try{
				robot.keyPress(code);
				robot.keyRelease(code);
			}catch(Exception e){
				//System.err.println("Invalid character code: \"" + code + "\"");
				if (code == 58){ // ':' character
					robot.keyPress(KeyEvent.VK_SHIFT);
					robot.keyPress(KeyEvent.VK_SEMICOLON);
					robot.keyRelease(KeyEvent.VK_SEMICOLON);
					robot.keyRelease(KeyEvent.VK_SHIFT);
				}

			}
		}
	}
}