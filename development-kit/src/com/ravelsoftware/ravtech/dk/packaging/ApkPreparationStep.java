
package com.ravelsoftware.ravtech.dk.packaging;

import java.io.File;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.ravelsoftware.ravtech.dk.RavTechDK;
import com.ravelsoftware.ravtech.dk.ui.packaging.BuildReporterDialog;

public class ApkPreparationStep extends PackageStep {

	public ApkPreparationStep (BuildReporterDialog buildReporterDialog) {
		super(buildReporterDialog);
	}

	@Override
	public void run () {
		PackageStep firstStep = new CopyStep(buildReporterDialog, getIcon("hdpi"), getIconDst("hdpi"));
		firstStep.setNextStep(new CopyStep(buildReporterDialog, getIcon("xhdpi"), getIconDst("xhdpi")))
			.setNextStep(new CopyStep(buildReporterDialog, getIcon("xxhdpi"), getIconDst("xxhdpi")))
			.setNextStep(new CopyStep(buildReporterDialog, getIcon("mdpi"), getIconDst("mdpi")))
			.setNextStep(
				new CreateFileStep(buildReporterDialog, new File(System.getProperty("user.dir") + "/builder/android/build.gradle"),
					Gdx.files.absolute(System.getProperty("user.dir") + "/build-templates/android/build.gradle").readString()
						.replaceAll("APP_ID", "\"" + RavTechDK.project.appId + "\"").getBytes()))
			.setNextStep(new CreateFileStep(buildReporterDialog,
				new File(System.getProperty("user.dir") + "/builder/android/AndroidManifest.xml"),
				Gdx.files.absolute(System.getProperty("user.dir") + "/build-templates/android/AndroidManifest.xml").readString()
					.replaceAll("APP_ID", RavTechDK.project.appId)
					.replaceAll("VERSION_CODE", String.valueOf(RavTechDK.project.buildVersion)).getBytes()))
			.setNextStep(new CreateFileStep(buildReporterDialog,
				new File(System.getProperty("user.dir") + "/builder/android/res/values/strings.xml"),
				Gdx.files.absolute(System.getProperty("user.dir") + "/build-templates/android/strings.xml").readString()
					.replaceAll("APP_NAME", RavTechDK.project.appName).getBytes()));
		firstStep.run();
		this.executeNext();
	}

	FileHandle getIcon (String name) {
		return RavTechDK.projectHandle.child("icons").child("android").child(name + ".png");
	}

	FileHandle getIconDst (String name) {
		return Gdx.files.absolute(System.getProperty("user.dir") + "/builder/android/res/drawable-" + name + "/ic_launcher.png");
	}

}
