
package com.quexten.ravtech.dk.ui.editor;

import java.util.regex.Pattern;

import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisTextField.TextFieldListener;

public class LabelNumberPair extends LabelActorPair<Float> {

	float amplitude = 1.0f;

	public LabelNumberPair (String labelText, float value) {
		super(labelText, new VisTextField(String.valueOf(value)), true);
		((VisTextField)pairedComponent).setFocusTraversal(false);
		((VisTextField)pairedComponent).setTextFieldListener(new TextFieldListener() {
			@Override
			public void keyTyped (VisTextField textField, char key) {
				if (key == '\n' || key == '\r') {
					((VisTextField)LabelNumberPair.this.pairedComponent).focusLost();
					return;
				}

				String text = ((VisTextField)LabelNumberPair.this.pairedComponent).getText();
				if (text.isEmpty() || !Pattern.matches("([0-9]*)\\.([0-9]*)", text) && !Pattern.matches("([0-9]*)", text))
					return;
				LabelNumberPair.this.dragValue = Float
					.valueOf(String.valueOf(((VisTextField)LabelNumberPair.this.pairedComponent).getText()));
				LabelNumberPair.this.draggedListener.run();
			}
		});
	}

	@Override
	Float getValue () {
		return Float.valueOf(((VisTextField)pairedComponent).getText());
	}

	void dragged (float x, float y) {
		dragValue = oldValue + x / 100f * amplitude;
		((VisTextField)pairedComponent).setText(String.valueOf(dragValue));
	}

}
