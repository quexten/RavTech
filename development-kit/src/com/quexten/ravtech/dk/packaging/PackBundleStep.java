
package com.quexten.ravtech.dk.packaging;

import java.io.File;

import com.quexten.ravtech.dk.RavTechDK;
import com.quexten.ravtech.dk.ui.packaging.BuildReporterDialog;
import com.quexten.ravtech.util.Zipper;

public class PackBundleStep extends PackageStep {

	public PackBundleStep (BuildReporterDialog buildReporterDialog) {
		super(buildReporterDialog);
	}

	@Override
	public void run () {
		Zipper zipper = new Zipper(buildReporterDialog);
		new File(System.getProperty("user.dir") + "/temp/").mkdir();
		zipper.zipFolder(RavTechDK.projectHandle.child("assets").path(),
			System.getProperty("user.dir") + "/temp/build.ravpack");
		executeNext();
	}
}
