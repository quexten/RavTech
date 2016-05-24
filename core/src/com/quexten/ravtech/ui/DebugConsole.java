
package com.quexten.ravtech.ui;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisTextField.TextFieldListener;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.scripts.Script;

public class DebugConsole extends VisWindow {

	ColoredTextArea textArea;
	Script script;
	boolean visible;

	public DebugConsole () {
		super("Debug Console");

		textArea = new ColoredTextArea();
		textArea.setFocusTraversal(false);
		textArea.setDisabled(true);
		VisScrollPane scrollPane = new VisScrollPane(textArea);

		VisTextField textField = new VisTextField();
		textField.setFocusTraversal(false);
		textField.setTextFieldListener(new TextFieldListener() {
			@Override
			public void keyTyped (VisTextField textField, char c) {
				if (script == null)
					script = RavTech.scriptLoader.createScript("", null);

				if (c == '\n' || c == '\r') {
					script.loadChunk("function init() \n " + textField.getText() + "\n end");
					script.init();
					textField.setText("");
				}
			}
		});
		this.add(scrollPane).grow();
		row();
		this.add(textField).growX();
		setSize(600, 300);
		addCloseButton();
		this.fadeOut(0);
	}

	public void log (String tag, String message) {
		textArea.log(com.quexten.ravtech.util.Debug.logColor, "[" + tag + "] : " + message + "\n");
	}

	public void logError (String tag, String message) {
		textArea.log(com.quexten.ravtech.util.Debug.errorColor, "[" + tag + "] : " + message + "\n");
	}

	public void logDebug (String tag, String message) {
		textArea.log(com.quexten.ravtech.util.Debug.debugColor, "[" + tag + "] : " + message + "\n");
	}

	public void toggleVisible () {
		if (getZIndex() != getParent().getChildren().size - 1 || !isVisible() || !visible) {
			toFront();
			this.fadeIn();
			visible = true;
		} else {
			toBack();
			this.fadeOut();
		}
	}

	/** Fade outs this window, when fade out animation is completed, window is removed from Stage */
	public void fadeOut (float time) {
		addAction(Actions.sequence(Actions.fadeOut(time, Interpolation.fade)));
		visible = false;
	}

}
