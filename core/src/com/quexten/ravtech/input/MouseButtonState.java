
package com.quexten.ravtech.input;

import com.badlogic.gdx.Gdx;

public class MouseButtonState {

	int id;
	public boolean lastValue;
	public boolean newValue;

	public MouseButtonState (int id) {
		this.id = id;
	}

	public void update () {
		lastValue = newValue;
		newValue = Gdx.input.isButtonPressed(id);
	}
}
