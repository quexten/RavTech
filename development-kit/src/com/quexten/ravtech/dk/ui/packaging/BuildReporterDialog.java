
package com.quexten.ravtech.dk.ui.packaging;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextArea;

public class BuildReporterDialog extends VisTable {

	public Array<PrinterListener> printerListeners = new Array<PrinterListener>();
	VisTextArea textArea;

	public BuildReporterDialog () {
		textArea = new VisTextArea() {
			@Override
			protected boolean continueCursor (int index, int offset) {
				return false;
			}
		};
		textArea.setDisabled(true);
		this.add(textArea).grow();
	}

	public void log (String string) {
		Gdx.app.log("BuildReporterDialog", string);
		for (int i = 0; i < printerListeners.size; i++)
			printerListeners.get(i).onPrint(string);
		textArea.setText(textArea.getText() + string + "\n");
	}

	public void logError (String message) {
		textArea
			.setText(textArea.getText() + "[Error]" + message + "\n");
	}

	@Override
	public float getPrefHeight () {
		return textArea.getLines()
			* VisUI.getSkin().getFont("default-font").getLineHeight();
	}

}
