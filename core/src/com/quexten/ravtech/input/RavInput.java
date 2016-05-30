
package com.quexten.ravtech.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.util.Debug;

public class RavInput {

	InputMultiplexer multiplexer = new InputMultiplexer();
	Array<InputDevice> inputDevices = new Array<InputDevice>();
	Array<ActionMap> actionMaps = new Array<ActionMap>();
	Array<Player> players = new Array<Player>();

	public RavInput () {
		Gdx.input.setInputProcessor(multiplexer);
		Controllers.addListener(new ControllerListener() {
			@Override
			public void connected (Controller controller) {
				GamePadDevice device = new GamePadDevice(controller);
				players.get(0).assignDevice(device, RavInput.this.getActionMapForDevice(device));
			}

			@Override
			public void disconnected (Controller controller) {
			}

			@Override
			public boolean buttonDown (Controller controller, int buttonCode) {
				return true;
			}

			@Override
			public boolean buttonUp (Controller controller, int buttonCode) {
				return true;
			}

			@Override
			public boolean axisMoved (Controller controller, int axisCode, float value) {
				return true;
			}

			@Override
			public boolean povMoved (Controller controller, int povCode, PovDirection value) {
				return true;
			}

			@Override
			public boolean xSliderMoved (Controller controller, int sliderCode, boolean value) {
				return true;
			}

			@Override
			public boolean ySliderMoved (Controller controller, int sliderCode, boolean value) {
				return true;
			}

			@Override
			public boolean accelerometerMoved (Controller controller, int accelerometerCode, Vector3 value) {
				return true;
			}
		});
		inputDevices.add(new KeyboardMouseDevice(multiplexer));
		for (int i = 0; i < Controllers.getControllers().size; i++)
			inputDevices.add(new GamePadDevice(Controllers.getControllers().get(i)));
		ActionMap keyboardMouseMap = new ActionMap("KeyboardMouse");
		keyboardMouseMap.setMapping("Jump", 6 + Keys.SPACE);
		actionMaps.add(keyboardMouseMap);

		ActionMap gamePadMap = new ActionMap("GamePad");
		gamePadMap.setMapping("Jump", 5);
		actionMaps.add(gamePadMap);

		Player player = new Player();
		for (int i = 0; i < inputDevices.size; i++)
			player.assignDevice(inputDevices.get(i), this.getActionMapForDevice(inputDevices.get(i)));
		players.add(player);
	}

	public void update () {
		for (int i = 0; i < inputDevices.size; i++)
			inputDevices.get(i).update();
		if (players.get(0).justPressed("Jump"))
			Debug.log("Jump", players.get(0).getValue("Jump"));
	}

	// Mouse - Touch input
	public int getX () {
		return Gdx.input.getX();
	}

	public int getX (int pointer) {
		return Gdx.input.getX(pointer);
	}

	public int getDeltaX () {
		return Gdx.input.getDeltaX();
	}

	public int getDeltaX (int pointer) {
		return Gdx.input.getDeltaX();
	}

	public int getY () {
		return Gdx.input.getY();
	}

	public int getY (int pointer) {
		return Gdx.input.getY(pointer);
	}

	public int getDeltaY () {
		return Gdx.input.getDeltaY();
	}

	public int getDeltaY (int pointer) {
		return Gdx.input.getDeltaY(pointer);
	}

	public Vector2 getWorldPosition () {
		return RavTech.sceneHandler.worldCamera.unproject(new Vector2(getX(), getY()));
	}

	public Vector2 getWorldPosition (int pointer) {
		return RavTech.sceneHandler.worldCamera.unproject(new Vector2(getX(pointer), getY(pointer)));
	}

	public boolean isTouched () {
		return Gdx.input.isTouched();
	}

	public boolean justTouched () {
		return Gdx.input.justTouched();
	}

	public boolean isTouched (int pointer) {
		return Gdx.input.isTouched(pointer);
	}

	// Keyboard input
	public boolean isKeyPressed (int key) {
		return Gdx.input.isKeyPressed(key);
	}

	public boolean isKeyJustPressed (int key) {
		return Gdx.input.isKeyJustPressed(key);
	}

	public ActionMap getActionMapForDevice (InputDevice... devices) {
		for (int i = 0; i < actionMaps.size; i++) {
			if (actionMaps.get(i).isFor(devices))
				return actionMaps.get(i);
		}
		return null;
	}

	public void addInputProcessor (InputProcessor processor) {
		this.multiplexer.addProcessor(processor);
	}

	public void removeInputProcessor (InputProcessor processor) {
		this.multiplexer.removeProcessor(processor);
	}

}
