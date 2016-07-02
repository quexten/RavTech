
package com.quexten.ravtech.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.quexten.ravtech.RavTech;

public class RavInput {

	InputMultiplexer multiplexer = new InputMultiplexer();
	public Array<InputDevice> inputDevices = new Array<InputDevice>();
	public ObjectMap<String, ActionMap> actionMaps = new ObjectMap<String, ActionMap>();
	Array<Player> players = new Array<Player>();

	public RavInput () {
		Gdx.input.setInputProcessor(multiplexer);
		Controllers.addListener(new ControllerListener() {
			@Override
			public void connected (Controller controller) {
				GamePadDevice device = new GamePadDevice(controller);
				RavInput.this.inputDevices.add(device);
				players.get(0).assignDevice(device, RavInput.this.getActionMapForDevice(device));
			}

			@Override
			public void disconnected (Controller controller) {
				for (int i = 0; i < RavInput.this.inputDevices.size; i++) {
					InputDevice inputDevice = RavInput.this.inputDevices.get(i);
					if (inputDevice.getType().equals("GamePad") && ((GamePadDevice)inputDevice).gamePad == controller) {
						inputDevice.assignedPlayer.unAssignDevice(inputDevice);
						RavInput.this.inputDevices.removeValue(inputDevice, true);
					}
				}
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
			
		if (!RavTech.isEditor) {
			reload();
		}

	}

	public void update () {
		for (int i = 0; i < inputDevices.size; i++)
			inputDevices.get(i).update();
	}

	@SuppressWarnings("unchecked")
	public void reload () {
		if ((!Gdx.files.local("keybindings.json").exists()) || RavTech.isEditor)
			Gdx.files.local("keybindings.json").writeString(RavTech.files.getAssetHandle("keybindings.json").readString(), false);
		this.actionMaps.clear();
		Json json = new Json();

		ObjectMap<String, JsonValue> serializedActionMaps = json.fromJson(ObjectMap.class,
			Gdx.files.local("keybindings.json").readString());
		for (ObjectMap.Entry<String, JsonValue> entry : (ObjectMap.Entries<String, JsonValue>)serializedActionMaps.entries()) {
			ActionMap actionMap = new ActionMap();
			actionMap.read(json, entry.value);
			this.actionMaps.put(entry.key, actionMap);
		}
		this.actionMaps.putAll(actionMaps);

		this.players.clear();
		Player player = new Player();
		for (int i = 0; i < inputDevices.size; i++)
			player.assignDevice(inputDevices.get(i), this.getActionMapForDevice(inputDevices.get(i)));
		players.add(player);
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

	public ObjectIntMap<String> getActionMapForDevice (InputDevice device) {
		if (this.actionMaps.containsKey(device.getType()))
			return this.actionMaps.get(device.getType());
		return null;
	}

	public void addInputProcessor (InputProcessor processor) {
		this.multiplexer.addProcessor(processor);
	}

	public void removeInputProcessor (InputProcessor processor) {
		this.multiplexer.removeProcessor(processor);
	}

	public InputDevice getDevice (String device) {
		for (int i = 0; i < this.inputDevices.size; i++)
			if (this.inputDevices.get(i).getType().equals(device))
				return inputDevices.get(i);
		return null;
	}
	
	public Player getPlayer() {
		return players.first();
	}

}
