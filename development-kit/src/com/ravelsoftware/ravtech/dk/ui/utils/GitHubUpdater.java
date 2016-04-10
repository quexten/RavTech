
package com.ravelsoftware.ravtech.dk.ui.utils;

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

public class GitHubUpdater extends Updater {

	String repositoryUrl;
	String versionUrl;
	String remoteVersion;

	String user;
	String archive;

	public GitHubUpdater (String currentVersion, String user, String archive) {
		super(currentVersion);
		this.user = user;
		this.archive = archive;
		this.repositoryUrl = "https://codeload.github.com/" + user + "/" + archive + "/zip/";
		this.versionUrl = "https://api.github.com/repos/" + user + "/" + archive + "/tags";
		this.checkRemoteVersion();
	}

	@Override
	public String getRemoteVersion () {
		return remoteVersion;
	}

	@Override
	public boolean isNewVersionAvalible () {
		return Float.valueOf(getRemoteVersion()) > Float.valueOf(this.currentVersion);
	}

	@Override
	public void update (String version) {
		try {
			Debug.logDebug("GitHub", "Updating " + archive + " to Version " + version + "...");
			RavTechDK.getPluginsFile(archive + "/").delete();
			FileUtils.copyURLToFile(new URL(repositoryUrl + version),
				RavTechDK.getDownloadsFile("temp-" + user + "-" + archive + ".zip").file());
			Debug.logDebug("GitHub", "Finished Downloading " + archive + ".");
			Zipper.extract(RavTechDK.getDownloadsFile("temp-" + user + "-" + archive + ".zip").file(), RavTechDK.getDownloadsFile("").file());
			RavTechDK.getDownloadsFile(archive + "-" + version).moveTo(RavTechDK.getPluginsFile(archive + "/"));
			RavTechDK.getDownloadsFile("temp-" + user + "-" + archive + ".zip").delete();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void checkRemoteVersion () {
		HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
		HttpRequest httpRequest = requestBuilder.newRequest().method(HttpMethods.GET).url(versionUrl).build();
		Gdx.net.sendHttpRequest(httpRequest, new HttpResponseListener() {
			@Override
			public void handleHttpResponse (HttpResponse httpResponse) {
				String httpResponseMessage = httpResponse.getResultAsString();
				int versionStart = httpResponseMessage.indexOf(':');
				String versionName = httpResponseMessage.substring(versionStart + 2, versionStart + 10);
				versionName = versionName.substring(0, versionName.indexOf('"'));
				GitHubUpdater.this.remoteVersion = versionName;
			}

			@Override
			public void failed (Throwable t) {
				t.printStackTrace();
			}

			@Override
			public void cancelled () {
				Debug.logDebug("GitHub", GitHubUpdater.this.archive + " - Failed Checking For Version.");
			}
		});
	}

}
