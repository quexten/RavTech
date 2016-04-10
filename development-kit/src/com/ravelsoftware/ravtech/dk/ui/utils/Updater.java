
package com.ravelsoftware.ravtech.dk.ui.utils;

public abstract class Updater {

	String currentVersion;

	public Updater (String currentVersion) {
		this.currentVersion = currentVersion;
	}

	/** @return the current Version of the Plugin */
	public String currentVersion () {
		return this.currentVersion();
	}

	/** Gets the Remote Version of the Plugin.
	 * @return the remote version of the Plugin */
	public abstract String getRemoteVersion ();

	/** Checks whether a new version is available.
	 * @return whether a new version is available */
	public abstract boolean isNewVersionAvalible ();

	/** Updates the plugin to the specified version.
	 * @param version - the Version to be updated to */
	public abstract void update (String version);

}
