
package com.ravelsoftware.ravtech.input;

import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.utils.IntFloatMap;
import com.badlogic.gdx.utils.IntMap;

public class ControllerState {

	public IntFloatMap axisValues = new IntFloatMap();
	public IntMap<Boolean> lastButtonValues = new IntMap<Boolean>();
	public IntMap<Boolean> buttonValues = new IntMap<Boolean>();
	public PovDirection povDirection = PovDirection.center;

	public ControllerState () {
		for (int i = 0; i < 20; i++) {
			lastButtonValues.put(i, false);
			buttonValues.put(i, false);
		}
	}

	public void update () {
		for (int i = 0; i < buttonValues.size; i++)
			lastButtonValues.put(i, buttonValues.get(i));
	}
}
