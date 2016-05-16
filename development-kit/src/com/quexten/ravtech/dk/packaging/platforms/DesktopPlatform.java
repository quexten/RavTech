
package com.quexten.ravtech.dk.packaging.platforms;

import com.badlogic.gdx.Gdx;
import com.quexten.ravtech.dk.RavTechDK;
import com.quexten.ravtech.dk.shell.Shell;
import com.quexten.ravtech.dk.ui.packaging.BuildReporterDialog;

public class DesktopPlatform implements Platform {

	@Override
	public void build (BuildReporterDialog dialog, BuildOptions options) {
		GradleInvoker.Invoke(dialog, "desktop:dist");
	}

	@Override
	public void run (BuildReporterDialog dialog, BuildOptions options) {
		Shell.executeCommand(RavTechDK.projectHandle.child("builds").child("desktop").file(), 
			Gdx.files.absolute(System.getProperty("java.home")).child("bin").child("java.exe").path() +" -jar build.jar", false);
	}
}
