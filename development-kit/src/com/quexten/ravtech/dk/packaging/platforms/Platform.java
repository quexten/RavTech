
package com.quexten.ravtech.dk.packaging.platforms;

import com.kotcrab.vis.ui.widget.VisTable;
import com.quexten.ravtech.dk.packaging.PackageStep;
import com.quexten.ravtech.dk.ui.packaging.BuildReporterDialog;

public abstract class Platform<V extends BuildOptions, K extends VisTable> {

	/** Builds the Project and Copies it to the Project build directory
	 * @param buildReporterDialog - the BuildDialog to log the progress in */
	public abstract void build (
		BuildReporterDialog buildReporterDialog, V options);

	/** Runs the latest build of the project
	 * @param buildReporterDialog - the BuildDialog to log the progress in
	 * @param deviceIdentifier - the Device the Build should be run on */
	public abstract void run (BuildReporterDialog buildReporterDialog,
		V option);

	/** Adds a Packaging step that will build the Engine for the specified target platform
	 * @param dialog - the BuildDialog to log the progress in
	 * @param currentStep - the current build step
	 * @param options - the BuildOptions
	 * @return the last build step */
	public abstract PackageStep addBuildEngineStep (
		BuildReporterDialog dialog, PackageStep currentStep,
		V options);

	/** Creates Build Options */
	public abstract V getOptions ();

	/** Creates an option table for the build dialog */
	public abstract K getOptionsTable (V options);

	/** Applies the options specified in the build dialog option table */
	public abstract void applyOptions (K optionsTable, V options);

}
