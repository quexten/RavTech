
package com.quexten.ravtech.dk.adb;

import java.io.File;
import java.io.IOException;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.dk.RavTechDK;
import com.quexten.ravtech.dk.shell.Shell;
import com.quexten.ravtech.util.Debug;

import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.managers.PackageManager;

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
				if (device.getSerial().equals(deviceId))
					return device;
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
				if (end < 0)
					end = line.length();

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
				if (initialized)
					return;
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

	public static void installBuild (String deviceId) {
		try {
			new PackageManager(getDevice(deviceId)).forceInstall(RavTechDK.getLocalFile("builds/android/build.apk").file());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void launchBuild (String deviceId) {
		try {
			new PackageManager(getDevice(deviceId)).launch(new se.vidstige.jadb.managers.Package(RavTechDK.project.appId));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void initializeAdb () {
		Debug.log("InitializeAdb",
			RavTech.settings.getString("RavTechDK.android.sdk.dir") + System.getProperty("file.separator") + "platform-tools");
		adbLocation = new File(
			RavTech.settings.getString("RavTechDK.android.sdk.dir") + System.getProperty("file.separator") + "platform-tools");
		initAdbConnection();
		RavTechDK.getLocalFile("builder/local.properties")
			.writeString("sdk.dir=" + RavTech.settings.getString("RavTechDK.android.sdk.dir"), false);
	}

	public static void onBoot () {
		onBoot = true;
		initializeAdb();
	}
}
