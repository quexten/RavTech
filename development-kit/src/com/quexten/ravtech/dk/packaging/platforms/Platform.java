
package com.quexten.ravtech.dk.packaging.platforms;

import com.quexten.ravtech.dk.packaging.PackageStep;
import com.quexten.ravtech.dk.ui.packaging.BuildReporterDialog;

public abstract class Platform {

	/** Builds the Project and Copies it to the Project build directory
	 * @param buildReporterDialog - the BuildReporterDialog to pipe the console log into */
	public abstract void build (BuildReporterDialog buildReporterDialog,
		BuildOptions options);

	/** Runs the latest build of the project
	 * @param buildReporterDialog - the BuildReporterDialog to pipe the console log into
	 * @param deviceIdentifier - the Device the Build should be run on */
	public abstract void run (BuildReporterDialog buildReporterDialog,
		BuildOptions option);

	public abstract PackageStep addBuildEngineStep (
		BuildReporterDialog dialog, PackageStep currentStep,
		BuildOptions options);

	public abstract BuildOptions getOptions ();

}
