
package com.quexten.ravtech.dk.packaging;

import java.io.File;

import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.dk.RavTechDK;
import com.quexten.ravtech.dk.ui.packaging.BuildReporterDialog;
import com.quexten.ravtech.util.ZipUtil;

public class PackBundleStep extends PackageStep {

	public PackBundleStep (BuildReporterDialog buildReporterDialog) {
		super(buildReporterDialog);
	}

	@Override
	public void run () {
		buildReporterDialog.log("Packaging...");
		ZipUtil zipper = new ZipUtil();
		new File(System.getProperty("user.dir") + "/temp/").mkdir();
		zipper.zipFolder(RavTech.files.getAssetHandle("").parent().child("assets").path(), System.getProperty("user.dir") + "/temp/build.ravpack");
		buildReporterDialog.log("Packaged.");
		executeNext();
	}
}
