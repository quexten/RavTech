
package com.ravelsoftware.ravtech.project;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

public class Project {

	public static Project load (FileHandle handle) {
		return new Json().fromJson(Project.class, handle.child("project.json"));
	}

	// App Meta Data
	public String developerName;
	public String appName;
	public int buildVersion;
	public int majorVersion;
	public int microVersion;
	public int minorVersion;
	// Game Settings
	public String startScene = "scenes/map.map";
	// TODO Plugins
	// TODO Player Settings
	// Desktop
	// Android
	// iOS
	// HTML5
	public String versionName;

	public void save (FileHandle handle) {
		Json json = new Json();
		json.setOutputType(OutputType.json);
		json.setTypeName(null);
		json.setUsePrototypes(false);
		json.setIgnoreUnknownFields(true);
		json.setOutputType(OutputType.json);
		handle.child("project.json").writeString(json.prettyPrint(this), false);
	}
}
