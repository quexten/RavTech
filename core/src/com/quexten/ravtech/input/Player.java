
package com.quexten.ravtech.input;

import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;

public class Player {

	ObjectMap<InputDevice, ObjectIntMap<String>> inputDevices = new ObjectMap<InputDevice, ObjectIntMap<String>>();
	InputDevice primaryDevice;

	public Player () {
	}

	public void assignDevice (InputDevice device, ObjectIntMap<String> actionMap) {
		inputDevices.put(device, actionMap);
		if (primaryDevice == null)
			setPrimaryDevice(device);
		device.assignPlayer(this);
	}

	public void unAssignDevice (InputDevice inputDevice) {		
		inputDevices.remove(inputDevice);
		if (primaryDevice == inputDevice) {
			if(inputDevices.size > 0) {
				primaryDevice = inputDevices.keys().next();
			} else {
				primaryDevice = null;
			}
		}
	}

	public void setPrimaryDevice (InputDevice device) {
		this.primaryDevice = device;
	}

	public float getValue (String key) {
		return primaryDevice != null ? primaryDevice.getValue(inputDevices.get(primaryDevice).get(key, 0)) : 0;
	}

	public float getLastValue (String key) {
		return primaryDevice != null ? primaryDevice.getLastValue(inputDevices.get(primaryDevice).get(key, 0)) : 0;
	}

	public boolean justPressed (String key) {
		return primaryDevice != null ? primaryDevice.justPressed(inputDevices.get(primaryDevice).get(key, 0)) : false;
	}

}
