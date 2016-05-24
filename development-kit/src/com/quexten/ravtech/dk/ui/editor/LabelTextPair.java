
package com.quexten.ravtech.dk.ui.editor;

import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisTextField.TextFieldListener;

public class LabelTextPair extends LabelActorPair<String> {

	public LabelTextPair (String labelText, String text) {
		super(labelText, new VisTextField(text), false);
		((VisTextField)pairedComponent).setFocusTraversal(false);
		((VisTextField)pairedComponent).setTextFieldListener(new TextFieldListener() {
			@Override
			public void keyTyped (VisTextField textField, char key) {
				if (key == '\n' || key == '\r')
					((VisTextField)LabelTextPair.this.pairedComponent).focusLost();
			}
		});
	}

	@Override
	public String getValue () {
		return ((VisTextField)pairedComponent).getText();
	}

}
