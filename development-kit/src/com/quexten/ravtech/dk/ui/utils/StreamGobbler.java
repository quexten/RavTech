
package com.quexten.ravtech.dk.ui.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamGobbler extends Thread {

	public static abstract class Printer implements Runnable {

		protected String line;

		abstract public void run ();
	}

	InputStream is;
	Printer printer;

	public StreamGobbler (InputStream is, Printer printer) {
		this.is = is;
		this.printer = printer;
	}

	@Override
	public void run () {
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				printer.line = line;
				printer.run();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
