package com.ravelsoftware.ravtech.dk.ui.editor;

import com.kotcrab.vis.ui.widget.VisTextField;

public class LabelTextPair extends LabelActorPair<String> {

	public LabelTextPair (String labelText, String text) {
		super(labelText, new VisTextField(text), false);
	}

	@Override
	public String getValue () {
		return ((VisTextField) pairedComponent).getText();
	}

}
