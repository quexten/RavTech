
package com.quexten.ravtech.dk.ui.packaging;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.highlight.Highlighter;
import com.kotcrab.vis.ui.util.highlight.WordHighlightRule;
import com.kotcrab.vis.ui.widget.HighlightTextArea;
import com.kotcrab.vis.ui.widget.ScrollableTextArea;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextArea;

public class BuildReporterDialog extends VisTable {

	public Array<PrinterListener> printerListeners = new Array<PrinterListener>();
	HighlightTextArea textArea;

	public BuildReporterDialog () {
		textArea = new HighlightTextArea("");
		textArea.setDisabled(true);
		Highlighter highlighter = new Highlighter();
		highlighter.addRule(new WordHighlightRule(Color.GREEN, "BUILD SUCCESSFUL"));
		textArea.setHighlighter(highlighter);
		this.add(textArea).grow();
	}

	public void log (String string) {
		Gdx.app.log("BuildReporterDialog", string);
		for (int i = 0; i < printerListeners.size; i++)
			printerListeners.get(i).onPrint(string);
		textArea.setText(textArea.getText() + string + "\n");
	}

	public void logError (String message) {
		Gdx.app.error("BuildReporterDialog", message);
		textArea.setText(textArea.getText() + "[Error]" + message + "\n");
	}

	@Override
	public float getPrefHeight () {
		return textArea.getLines() * VisUI.getSkin().getFont("default-font").getLineHeight();
	}

}
