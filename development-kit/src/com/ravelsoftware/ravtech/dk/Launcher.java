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

package com.ravelsoftware.ravtech.dk;

import java.awt.Color;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3FileHandle;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.ravelsoftware.ravtech.HookApi;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.dk.adb.AdbManager;
import com.ravelsoftware.ravtech.project.Project;
import com.ravelsoftware.ravtech.scripts.Script;
import com.ravelsoftware.ravtech.scripts.lua.LuaJScriptLoader;
import com.ravelsoftware.ravtech.settings.SettingsValueListener;
import com.ravelsoftware.ravtech.util.Debug;

public class Launcher {

	public static void main (String[] args) {
		System.out.println("User Directory: " + System.getProperty("user.dir"));
		configureUILook();
		configureNativesPath();
		Preferences preferences = new Lwjgl3Preferences(new Lwjgl3FileHandle(new File(".prefs/", "RavTech"), FileType.External));
		if (!preferences.getString("RavTechDK.project.path").isEmpty()
			&& new Lwjgl3FileHandle(preferences.getString("RavTechDK.project.path"), FileType.Absolute).child("project.json")
				.exists()) {
			RavTechDK.projectHandle = new Lwjgl3FileHandle(preferences.getString("RavTechDK.project.path"), FileType.Absolute);
			RavTechDK.project = Project.load(RavTechDK.projectHandle);
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run () {
						initializeEngine();
					}
				});
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			// ProjectSettingsWizard wizard = new ProjectSettingsWizard(true);
			// final Project project = new Project();
			/*
			 * wizard.onSaveListener = new ActionListener() {
			 * 
			 * @Override public void actionPerformed (ActionEvent event) { RavTechDK.projectHandle = new
			 * Lwjgl3FileHandle(event.getActionCommand(), FileType.Absolute); RavTechDK.createProject(event.getActionCommand(),
			 * project); RavTechDK.project = project; initializeEngine(); } };
			 */
			// wizard.show(project);
		}
	}

	static void initializeEngine () {
		RavTech.isEditor = true;
		final RavTechDKApplication ravtech = new RavTechDKApplication(new AbsoluteFileHandleResolver() {

			@Override
			public FileHandle resolve (String fileName) {
				fileName = fileName.replace('\\', '/');
				String formattedWorkingDir = RavTechDK.projectHandle.child("assets").path();
				String resolver = fileName.startsWith(formattedWorkingDir) ? fileName : formattedWorkingDir + "/" + fileName;
				return Gdx.files.absolute(resolver);
			}
		}, RavTechDK.project);
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(1280, 720);
		RavTech.files.getAssetManager().setLoader(Script.class, new LuaJScriptLoader(RavTech.files.getResolver()));
		// RavTechDK.initialize(ravtech);
		new Lwjgl3Application(ravtech, config);
		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run () {
				registerSettingsListeners();
			}
		});
		RavTech.isEditor = true;
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run () {
				for (int i = 0; i < HookApi.onShutdownHooks.size; i++)
					HookApi.onShutdownHooks.get(i).run();
			}
		});
	}

	static void configureNativesPath () {
		// Sets Native Libraries Load path
		String separator = System.getProperty("file.separator");
		String libsPath = System.getProperty("user.dir") + separator + "libs" + separator + "natives" + separator;
		System.setProperty("java.library.path", libsPath);
		try {
			Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
			fieldSysPath.setAccessible(true);
			fieldSysPath.set(null, null);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	static void configureUILook () {
		// Fixes color selector being rendered under scene view
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
		// System Look and Feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		UIManager.put("Ravtech.foreground", new Color(196, 96, 0, 255));
		// Fixes Unwanted Borders rendering
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			UIManager.get(key);
			if (String.valueOf(key).endsWith(".focus")) UIManager.put(key, new Color(0, 0, 0, 0));
		}
		UIManager.put("ProgressBar.selectionForeground", Color.black);
		UIManager.put("ProgressBar.selectionBackground", Color.gray);
	}

	public static void registerSettingsListeners () {
		RavTech.settings.addValueListener("RavTechDK.android.sdk.dir", new SettingsValueListener() {

			@Override
			public void settingChanged (Object oldValue, Object newValue) {
				Gdx.files.local("builder/local.properties")
					.writeString("# Location of the android SDK\n" + "sdk.dir=" + String.valueOf(newValue), false);
				Debug.log("Adb", Gdx.files.local("builder/local.properties").path());
				AdbManager.adbLocation = new File(String.valueOf(newValue) + System.getProperty("file.separator") + "platform-tools");
				AdbManager.initAdbConnection();
			}
		});
	}
}
