
package com.quexten.ravtech.dk.ui.packaging;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.highlight.Highlight;
import com.kotcrab.vis.ui.util.highlight.HighlightRule;
import com.kotcrab.vis.ui.util.highlight.Highlighter;
import com.kotcrab.vis.ui.util.highlight.RegexHighlightRule;
import com.kotcrab.vis.ui.util.highlight.WordHighlightRule;
import com.kotcrab.vis.ui.widget.HighlightTextArea;
import com.kotcrab.vis.ui.widget.VisTable;

public class BuildReporterDialog extends VisTable {

	public Array<PrinterListener> printerListeners = new Array<PrinterListener>();
	HighlightTextArea textArea;

	public BuildReporterDialog () {
		textArea = new HighlightTextArea("");
		textArea.setDisabled(true);
		
		Field[] fields = ClassReflection.getDeclaredFields(HighlightTextArea.class);
		for(Field field : fields) {
			System.out.println("field"+field.getName());
			if(field.getName().equals("defaultColor")) {
				try {
					field.setAccessible(true);
					field.set(textArea, Color.DARK_GRAY);
				} catch (ReflectionException e) {
					e.printStackTrace();
				}
			}
		}
		
		Highlighter highlighter = new Highlighter();
		
		highlighter.addRule(new WordHighlightRule(Color.GREEN, "BUILD SUCCESSFUL"));		
		highlighter.addRule(new HighlightRule() {
			@Override
			public void process (HighlightTextArea textArea, Array<Highlight> highlights) {
				String content = textArea.getText();
				String[] array = content.split("\n", -1);
				int[] lengthsArray = new int[array.length];
				
				for(int i = 0; i < array.length; i++) {
					lengthsArray[i] = array[i].length() + (i > 0 ? lengthsArray[i-1] : 0) + 1;
					System.out.println("lengthsArray["+i+"]="+lengthsArray[i]);
				}
				
				for(int i = 0; i < array.length; i++) {
					if(content.contains("[Error]")) {
						int start = lengthsArray[i];
						int end = (i < array.length - 1 ? lengthsArray[i+1] : textArea.getText().length());
						if(start < end)
							highlights.add(new Highlight(Color.RED, start, end));
					}
				}
			}			
		});
		
		textArea.setHighlighter(highlighter);
		this.add(textArea).grow();
	}

	public void log (String message) {
		Gdx.app.log("BuildReporterDialog", message);
		for (int i = 0; i < printerListeners.size; i++)
			printerListeners.get(i).onPrint(message);
		textArea.appendText("[Log]" + message + "\n");
	}

	public void logError (String message) {
		Gdx.app.error("BuildReporterDialog", message);
		textArea.appendText("[Error]" + message + "\n");
	}

	@Override
	public float getPrefHeight () {
		return textArea.getLines() * VisUI.getSkin().getFont("default-font").getLineHeight();
	}

}
