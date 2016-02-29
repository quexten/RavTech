/*******************************************************************************
 * Copyright 2014-2016 Bernd Schoolmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ravelsoftware.ravtech.dk.adb;

import java.io.File;
import java.io.IOException;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.dk.shell.Shell;

import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;

public class AdbManager {

	public static File adbLocation;
	public static ObjectMap<JadbDevice, String> deviceNames = new ObjectMap<JadbDevice, String>();
	public static boolean initialized;
	public static JadbConnection jadbConnection;
	private static boolean onBoot;

	public static String executeAdbCommand (String arguments) {
		return Shell.executeCommand(adbLocation, "adb " + arguments);
	}

	public static JadbDevice getDevice (String deviceId) {
		try {
			for (JadbDevice device : jadbConnection.getDevices())
				if (device.getSerial().equals(deviceId)) return device;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static String getDeviceName (JadbDevice device) {
		return deviceNames.get(device);
	}

	public static Array<JadbDevice> getDevices () {
		Array<JadbDevice> devices = new Array<JadbDevice>();
		try {
			for (JadbDevice device : jadbConnection.getDevices())
				devices.add(device);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (devices.size > 0) {
			String deviceShellOutput = executeAdbCommand("devices -l");
			String[] deviceShellOutputLines = deviceShellOutput.split("\\n");
			for (int i = 1; i < deviceShellOutputLines.length; i++) {
				String line = deviceShellOutputLines[i];
				int start = line.indexOf("model:") + "model:".length();
				int end = line.indexOf("model:") + "model:".length() + line.substring(start).indexOf(' ');
				if (end < 0) end = line.length();
				deviceNames.put(getDevice(line.substring(0, line.indexOf(' '))), line.substring(start, end));
			}
		}
		return devices;
	}

	public static void initAdbConnection () {
		new Thread() {

			@Override
			public void run () {
				if (adbLocation.getPath().startsWith("null") && !onBoot)
					return;
				else if (adbLocation.getPath().startsWith("null") && onBoot) {
					onBoot = false;
					return;
				}
				onBoot = false;
				if (initialized) return;
				try {
					jadbConnection = new JadbConnection();
				} catch (IOException e) {
					executeAdbCommand("start-server");
				}
				initialized = true;
				try {
					this.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public static void initializeAdb () {
		adbLocation = new File(
			RavTech.settings.getString("RavTechDK.android.sdk.dir") + System.getProperty("file.separator") + "platform-tools");
		initAdbConnection();
	}

	public static void onBoot () {
		onBoot = true;
		initializeAdb();
	}
}
