
package com.ravelsoftware.ravtech.dk.ui.editor;

import com.badlogic.gdx.graphics.Color;

public class LabelColorPair extends LabelActorPair<Color> {

	public LabelColorPair (String labelText, Color value) {
		super(labelText, new ColorPanel(value), false);
	}

	@Override
	Color getValue () {
		return ((ColorPanel)this.pairedComponent).getColor();
	}

}
