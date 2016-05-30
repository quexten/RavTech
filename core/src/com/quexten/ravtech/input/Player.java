
package com.quexten.ravtech.input;

import com.badlogic.gdx.utils.ObjectMap;
import com.quexten.ravtech.util.Debug;

public class Player {

	ObjectMap<InputDevice, ActionMap> inputDevices = new ObjectMap<InputDevice, ActionMap>();
	InputDevice primaryDevice;

	public Player () {
	}

	public void assignDevice (InputDevice device, ActionMap actionMap) {
		inputDevices.put(device, actionMap);
		if (primaryDevice == null)
			setPrimaryDevice(device);
		device.assignPlayer(this);
	}

	public void setPrimaryDevice (InputDevice device) {
		Debug.log("SetPrimaryDevice", device);
		this.primaryDevice = device;
	}

	public float getValue (String key) {
		return primaryDevice != null ? primaryDevice.getValue(inputDevices.get(primaryDevice).getId(key)) : 0;
	}

	public float getLastValue (String key) {
		return primaryDevice != null ? primaryDevice.getLastValue(inputDevices.get(primaryDevice).getId(key)) : 0;
	}

	public boolean justPressed (String key) {
		return primaryDevice != null ? primaryDevice.justPressed(inputDevices.get(primaryDevice).getId(key)) : false;
	}
}
