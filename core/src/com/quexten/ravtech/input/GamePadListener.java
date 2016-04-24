
package com.quexten.ravtech.input;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.quexten.ravtech.util.Debug;

public class GamePadListener implements ControllerListener {

	Array<Controller> controllerMap = new Array<Controller>();

	public GamePadListener () {
		Array<Controller> controllers = Controllers.getControllers();
		Debug.logError("controllercount", controllers.size);
		for (int i = 0; i < controllers.size; i++)
			Debug.logError("Controller", controllers.get(i).getName());
	}

	@Override
	public void connected (Controller controller) {
	}

	@Override
	public void disconnected (Controller controller) {
	}

	@Override
	public boolean buttonDown (Controller controller, int buttonCode) {
		// RavTech.input.controllerStates.get(indexFor(controller)).buttonValues.put(buttonCode, true);
		return false;
	}

	@Override
	public boolean buttonUp (Controller controller, int buttonCode) {
		// RavTech.input.controllerStates.get(indexFor(controller)).buttonValues.put(buttonCode, false);
		return false;
	}

	@Override
	public boolean axisMoved (Controller controller, int axisCode,
		float value) {
		// RavTech.input.controllerStates.get(indexFor(controller)).axisValues.put(axisCode, value);
		return false;
	}

	@Override
	public boolean povMoved (Controller controller, int povCode,
		PovDirection value) {
		// RavTech.input.controllerStates.get(indexFor(controller)).povDirection = value;
		return false;
	}

	@Override
	public boolean xSliderMoved (Controller controller, int sliderCode,
		boolean value) {
		return false;
	}

	@Override
	public boolean ySliderMoved (Controller controller, int sliderCode,
		boolean value) {
		return false;
	}

	@Override
	public boolean accelerometerMoved (Controller controller,
		int accelerometerCode, Vector3 value) {
		return false;
	}
}
