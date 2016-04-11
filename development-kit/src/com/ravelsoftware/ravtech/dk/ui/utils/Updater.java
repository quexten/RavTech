
package com.ravelsoftware.ravtech.dk.ui.utils;

import com.ravelsoftware.ravtech.dk.ui.editor.UpdaterWidget.UpdaterEntry;

public abstract class Updater {

	String currentVersion;
	String description = "";
	String projectPage = "";

	UpdaterEntry updaterEntry;
	
	public Updater () {
	}
	
	public void setCurrentVersion(String version) {
		this.currentVersion = version;
	}
	
	/** @return the current Version of the Plugin */
	public String currentVersion () {
		return this.currentVersion;
	}

	/** Gets the Remote Version of the Plugin.
	 * @return the remote version of the Plugin */
	public abstract String getRemoteVersion ();

	/** Initiates asynchronous checking of the Remote Version of the Plugin */
	public abstract void checkRemoteVersion ();

	/** Checks whether a new version is available.
	 * @return whether a new version is available */
	public abstract boolean isNewVersionAvalible ();

	/** Updates the plugin to the specified version.
	 * @param version - the Version to be updated to */
	public abstract void update (String version);

	/** Sets the description of this Updater.
	 * @param description - the description
	 * @return - the Updater for method chaining */
	public Updater setDescription (String description) {
		this.description = description;
		return this;
	}

	/** Gets the description of this Updater. */
	public String getDescription () {
		return this.description;
	}

	/** Sets the project page of this Updater.
	 * @param projectPage - the description
	 * @return - the Updater for method chaining */
	public Updater setProjectPage (String projectPage) {
		this.projectPage = projectPage;
		return this;
	}

	/** Gets the project page of this Updater. */
	public String getProjectPage () {
		return this.projectPage;
	}
	
	public Updater setUpdaterEntry (UpdaterEntry updaterEntry) {
		this.updaterEntry = updaterEntry;
		return this;
	}

}
