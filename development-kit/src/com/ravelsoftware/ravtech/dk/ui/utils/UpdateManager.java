
package com.ravelsoftware.ravtech.dk.ui.utils;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.ravelsoftware.ravtech.dk.RavTechDK;
import com.ravelsoftware.ravtech.util.Debug;

public class UpdateManager {

	static ObjectMap<String, Updater> updaters = new ObjectMap<String, Updater>();
	public static ObjectMap<String, String> versions = new ObjectMap<String, String>();
	static boolean finishedCheckingRemoteVersions;

	public static void addUpdater (String key, Updater value) {
		updaters.put(key, value);
		value.currentVersion = versions.get(key);
	}

	@SuppressWarnings("unchecked")
	public static void loadCurrentVersions () {
		versions = new Json().fromJson(ObjectMap.class, RavTechDK.getLocalFile("versions.json").readString());
		Debug.log("Loaded Udpate file", versions);
	}

	public static void saveCurrentVersions () {
		Entries<String, Updater> entries = updaters.iterator();
		while (entries.hasNext) {
			Entry<String, Updater> entry = entries.next();
			versions.put(entry.key, entry.value.currentVersion);
		}
		RavTechDK.getLocalFile("versions.json").writeString(new Json().prettyPrint(versions), false);
	}

	public static void update () {
		if (!finishedCheckingRemoteVersions) {
			Entries<String, Updater> entries = updaters.iterator();
			
			boolean finished = true;
			
			while (entries.hasNext) {
				Entry<String, Updater> entry = entries.next();
				if (entry.value.getRemoteVersion() == null) {
					finished = false;
					break;
				}
			}
			
			if (finished) {
				finishedCheckingRemoteVersions = true;
				entries = updaters.iterator();
				while (entries.hasNext) {
					Entry<String, Updater> entry = entries.next();
					if (entry.value.isNewVersionAvalible()) {
						Debug.logDebug("Update Manager", "New Version Avalible For: " + entry.key + " " + entry.value.currentVersion() + "/"
							+ entry.value.getRemoteVersion());
					}
				}
			}
		}
	}

	public static void checkForUpdates () {
		finishedCheckingRemoteVersions = false;
		Entries<String, Updater> entries = updaters.iterator();
		while (entries.hasNext) {
			Entry<String, Updater> entry = entries.next();
			entry.value.checkRemoteVersion();
		}
	}

	public static ObjectMap<String, Updater> getUpdaters () {
		return updaters;
	}

}
