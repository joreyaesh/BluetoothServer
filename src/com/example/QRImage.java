package com.example;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

public class QRImage {
	public QRImage(String str){
		ByteArrayOutputStream out = QRCode.from(str)
				.to(ImageType.PNG).withSize(512, 512).stream();

		try {
			FileOutputStream fout = new FileOutputStream(new File(
					"QR_Code.png"));

			fout.write(out.toByteArray());

			fout.flush();
			fout.close();

		} catch (FileNotFoundException e) {
			System.err.println("Unable to create QR code.");
		} catch (IOException e2) {
			System.err.println("Error writing QR code.");
		}
	}
	
	public static void create(String str){
		new QRImage(str);
	}
}
