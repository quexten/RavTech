
package com.quexten.ravtech.dk.ui.utils;

import java.net.URL;

import org.apache.commons.io.FileUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.quexten.ravtech.dk.RavTechDK;
import com.quexten.ravtech.util.Debug;

public class GitHubUpdater extends Updater {

	String repositoryUrl;
	String versionUrl;
	String remoteVersion;

	String user;
	String archive;

	public GitHubUpdater (String user, String archive) {
		super();
		this.user = user;
		this.archive = archive;
		repositoryUrl = "https://codeload.github.com/" + user + "/" + archive + "/zip/";
		versionUrl = "https://api.github.com/repos/" + user + "/" + archive + "/tags";
	}

	@Override
	public String getRemoteVersion () {
		return remoteVersion;
	}

	@Override
	public boolean isNewVersionAvalible () {
		if (currentVersion == null)
			return true;
		return Float.valueOf(getRemoteVersion() != null ? getRemoteVersion() : "0") > Float.valueOf(currentVersion);
	}

	@Override
	public void update (String version) {
		try {
			Debug.logDebug("GitHub", "Updating " + archive + " to Version " + version + "...");
			RavTechDK.getPluginsFile(archive + "/").delete();
			FileUtils.copyURLToFile(new URL(repositoryUrl + version),
				RavTechDK.getDownloadsFile("temp-" + user + "-" + archive + ".zip").file());
			Debug.logDebug("GitHub", "Finished Downloading " + archive + ".");
			// ZipUtil.extract(RavTechDK.getDownloadsFile("temp-" + user + "-" + archive + ".zip").file(),
			// RavTechDK.getDownloadsFile("").file());
			RavTechDK.getDownloadsFile(archive + "-" + version).moveTo(RavTechDK.getPluginsFile(archive + "/"));
			RavTechDK.getDownloadsFile("temp-" + user + "-" + archive + ".zip").delete();
			currentVersion = version;
			if (updaterEntry != null)
				updaterEntry.finishedUpdating();
			UpdateManager.saveCurrentVersions();
			if (postUpdateHook != null)
				postUpdateHook.run();
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
				remoteVersion = versionName;
				if (GitHubUpdater.this.updaterEntry != null)
					GitHubUpdater.this.updaterEntry.gotRemoteVersion();
			}

			@Override
			public void failed (Throwable t) {
				t.printStackTrace();
			}

			@Override
			public void cancelled () {
				Debug.logDebug("GitHub", archive + " - Failed Checking For Version.");
			}
		});
	}

}
