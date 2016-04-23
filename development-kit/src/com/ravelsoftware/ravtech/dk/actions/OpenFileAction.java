
package com.ravelsoftware.ravtech.dk.actions;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import com.ravelsoftware.ravtech.dk.zerobrane.ZeroBraneUtil;

public class OpenFileAction implements Runnable {

	File file;

	public OpenFileAction (File file) {
		this.file = file;
	}

	@Override
	public void run () {
		switch (file.getName().substring(
			file.getName().lastIndexOf('.'), file.getName().length())) {
			case ".scene":
				break;
			case ".lua":
				ZeroBraneUtil.openFile(file);
				break;
			case ".particle":
				new Thread() {

					@Override
					public void run () {
						try {
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}.start();
				break;
			default:
				try {
					Desktop.getDesktop().open(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
		}
	}
}
