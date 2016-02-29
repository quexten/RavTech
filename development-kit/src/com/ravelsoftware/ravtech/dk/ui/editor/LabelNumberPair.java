
package com.ravelsoftware.ravtech.dk.ui.editor;

import com.kotcrab.vis.ui.widget.VisTextField;

public class LabelNumberPair extends LabelActorPair<Float> {

	public LabelNumberPair (String labelText, float value) {
		super(labelText, new VisTextField(String.valueOf(value)), true);
	}

	@Override
	Float getValue () {
		return Float.valueOf(((VisTextField)this.pairedComponent).getText());
	}

	void dragged (float x, float y) {
		this.dragValue = this.oldValue + x / 100f;
		((VisTextField)this.pairedComponent).setText(String.valueOf(this.dragValue));
	}

}
