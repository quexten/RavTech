
package com.quexten.ravtech.dk.packaging.platforms;

import com.quexten.ravtech.dk.ui.packaging.BuildReporterDialog;

public interface Platform {

	/** Builds the Project and Copies it to the Project build directory
	 * @param buildReporterDialog - the BuildReporterDialog to pipe the console log into */
	void build (BuildReporterDialog buildReporterDialog,
		BuildOptions options);

	/** Runs the latest build of the project
	 * @param buildReporterDialog - the BuildReporterDialog to pipe the console log into
	 * @param deviceIdentifier - the Device the Build should be run on */
	void run (BuildReporterDialog buildReporterDialog,
		BuildOptions option);

	
}
