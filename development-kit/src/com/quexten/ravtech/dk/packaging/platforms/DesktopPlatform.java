
package com.quexten.ravtech.dk.packaging.platforms;

import com.badlogic.gdx.Gdx;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.dk.RavTechDK;
import com.quexten.ravtech.dk.packaging.BuildPlatformStep;
import com.quexten.ravtech.dk.packaging.CopyStep;
import com.quexten.ravtech.dk.packaging.PackageStep;
import com.quexten.ravtech.dk.packaging.platforms.BuildOptions.AssetType;
import com.quexten.ravtech.dk.shell.Shell;
import com.quexten.ravtech.dk.ui.packaging.BuildReporterDialog;

public class DesktopPlatform extends Platform<DesktopBuildOptions, DesktopBuildOptionsTable> {

	@Override
	public void build (BuildReporterDialog dialog, DesktopBuildOptions options) {
		GradleInvoker.Invoke(dialog, "desktop:dist");
	}

	@Override
	public void run (BuildReporterDialog dialog, DesktopBuildOptions options) {
		Shell.executeCommand(RavTech.files.getAssetHandle("").parent().child("builds").child("desktop").file(),
			Gdx.files.absolute(System.getProperty("java.home")).child("bin").child("java.exe").path() + " -jar build.jar", false);
	}

	@Override
	public DesktopBuildOptions getOptions () {
		return new DesktopBuildOptions(AssetType.External);
	}

	@Override
	public PackageStep addBuildEngineStep (BuildReporterDialog dialog, PackageStep currentStep, DesktopBuildOptions options) {
		PackageStep tempStep = null;

		tempStep = currentStep.setNextStep(new BuildPlatformStep(dialog, new DesktopPlatform(), options));

		return tempStep.setNextStep(new CopyStep(dialog, RavTechDK.getLocalFile("builder/desktop/build/libs/desktop-1.0.jar"),
			RavTech.files.getAssetHandle("").parent().child("builds").child("desktop").child("build.jar")));
	}

	@Override
	public DesktopBuildOptionsTable getOptionsTable (DesktopBuildOptions options) {
		return new DesktopBuildOptionsTable(options);
	}

	@Override
	public void applyOptions (DesktopBuildOptionsTable optionsTable, DesktopBuildOptions options) {

	}

}
