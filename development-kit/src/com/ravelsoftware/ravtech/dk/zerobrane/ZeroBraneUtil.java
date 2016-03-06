
package com.ravelsoftware.ravtech.dk.zerobrane;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.ravelsoftware.ravtech.dk.RavTechDK;
import com.ravelsoftware.ravtech.util.Debug;
import com.ravelsoftware.ravtech.util.Zipper;

public class ZeroBraneUtil {

	final static String repositoryUrl = "https://codeload.github.com/pkulchenko/ZeroBraneStudio/zip/";
	final static String versionUrl = "https://api.github.com/repos/pkulchenko/ZeroBraneStudio/tags";
	final static String versionPath = System.getProperty("user.dir") + "/zbstudio/version.txt";

	public static void checkForUpdates () {
		HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
		HttpRequest httpRequest = requestBuilder.newRequest().method(HttpMethods.GET).url(versionUrl).build();
		Gdx.net.sendHttpRequest(httpRequest, new HttpResponseListener() {

			@Override
			public void handleHttpResponse (HttpResponse httpResponse) {
				String httpResponseMessage = httpResponse.getResultAsString();
				int versionStart = httpResponseMessage.indexOf(':');
				String versionName = httpResponseMessage.substring(versionStart + 2, versionStart + 10);
				versionName = versionName.substring(0, versionName.indexOf('"'));
				float versionNumber = Float.valueOf(versionName);
				if (currentVersion() < versionNumber) {
					Debug.log("ZeroBrane", "New Version Avalible!");
					Debug.log("CurrentVersion", currentVersion());
					Debug.log("New Version", versionNumber);
					update(versionName);
					return;
				}
				Debug.log("ZeroBrane", "No New Version Found");
			}

			@Override
			public void failed (Throwable t) {
				t.printStackTrace();
			}

			@Override
			public void cancelled () {
				Debug.log("ZeroBrane", "Cancelled");
			}
		});
	}

	static float currentVersion () {
		try {
			return Float.valueOf(Gdx.files.absolute(versionPath).readString());
		} catch (Exception ex) {
			return 0f;
		}
	}

	public static void update (String version) {
		try {
			Debug.log("ZeroBrane Updating To Version", version);
			FileUtils.deleteDirectory(RavTechDK.getLocalFile("zbstudio/"));
			FileUtils.copyURLToFile(new URL(repositoryUrl + version), RavTechDK.getLocalFile("temp-zerobrane.zip"));
			Debug.log("ZeroBrane", "Done Downloading");
			Zipper.extract(RavTechDK.getLocalFile("temp-zerobrane.zip"), RavTechDK.getLocalFile(""));
			RavTechDK.getLocalFile("ZeroBraneStudio-" + version).renameTo(RavTechDK.getLocalFile("zbstudio/"));
			Gdx.files.absolute(versionPath).writeString(version, false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void openFile (File file) {
		ProcessBuilder b = new ProcessBuilder(
			RavTechDK.getLocalFile("/zbstudio/zbstudio." + RavTechDK.getSystemExecutableEnding()).getPath(),
			RavTechDK.projectHandle.child("assets").path(), file.getPath());
		try {
			b.start();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
