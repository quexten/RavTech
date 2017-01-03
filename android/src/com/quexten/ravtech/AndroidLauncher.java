
package com.quexten.ravtech;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidFiles;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.quexten.ravtech.android.AndroidEngineConfiguration;
import com.quexten.ravtech.files.zip.ArchiveFileHandleResolver;
import com.quexten.ravtech.net.kryonet.KryonetTransportLayer;
import com.quexten.ravtech.project.Project;
import com.quexten.ravtech.remoteedit.RemoteEditConnectionScreen;
import com.quexten.ravtech.scripts.lua.LuaJScriptLoader;
import com.quexten.ravtech.settings.RavSettings;
import com.quexten.ravtech.util.Debug;

import android.content.Intent;
import android.os.Bundle;

public class AndroidLauncher extends AndroidApplication {

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidFiles files = new AndroidFiles(this.getAssets());
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		AndroidEngineConfiguration engineConfiguration = new AndroidEngineConfiguration();
		// new Json().fromJson(AndroidEngineConfiguration.class,
		// files.getFileHandle("config.json", FileType.Internal).readString());

		int versionCode = 0;
		try {
			versionCode = this.getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		boolean useExternalAssetBundle = engineConfiguration.useAssetBundle;
		
		FileHandleResolver resolver = useExternalAssetBundle
			? new ArchiveFileHandleResolver(
				files.external("Android/obb/" + getPackageName() + "/main." + versionCode + "." + getPackageName() + ".obb"))
			: new InternalFileHandleResolver();
		
		engineConfiguration.assetResolver = resolver;
			
		final RavTech ravtech = new RavTech(engineConfiguration);
		
		engineConfiguration.remoteEdit = true;
		if(!engineConfiguration.remoteEdit)
			HookApi.addHook("onBoot", new Hook() {
				@Override
				public void run() {
					RavTech.files.loadAsset("project.json", Project.class);
					RavTech.files.finishLoading();
					RavTech.project = RavTech.files.getAsset("project.json");
					
					RavTech.files.loadAsset(RavTech.project.startScene, Scene.class);
					RavTech.files.finishLoading();
					RavTech.currentScene = RavTech.files.getAsset(RavTech.project.startScene);
					Debug.log("RavTech", "BootUp");
				}
			});
		else
			HookApi.addHook("onBoot", new Hook() {
				@Override
				public void run() {
					Project project = new Project();
					project.appId = "com.quexten.ravtech.remoteedit";
					project.appName = "RavTech-RemoteEdit";
					project.buildVersion = 1;
					project.developerName = "quexten";
					project.majorVersion = 0;
					project.minorVersion = 0;
					project.microVersion = 1;
					project.startScene = "";
					project.versionName = "Alpha";
					
					RavTech.project = project;					
					RavTech.settings = new RavSettings(project.appName);
					RavTech.net.transportLayers.add(new KryonetTransportLayer(RavTech.net));
					ravtech.setScreen(new RemoteEditConnectionScreen());
					RavTech.ui.debugConsole.setVisible(false);

					((RemoteEditConnectionScreen) ravtech.getScreen()).qrButton.addListener(new ChangeListener() {
						@Override
						public void changed (ChangeEvent event, Actor actor) {
							Intent intent = new Intent(AndroidLauncher.this, SimpleScannerActivity.class);
							startActivity(intent);
						}
					});
				}
			});
		
		/*HookApi.onBootHooks.add(new Hook() {
			@Override
			public void run () {
				RavTech.net.transportLayers.add(new KryonetTransportLayer(RavTech.net));
				ravtech.setScreen(new RemoteEditConnectionScreen());
				RavTech.ui.debugConsole.setVisible(false);
			}
		});*/

		RavTech.scriptLoader = new LuaJScriptLoader();
		initialize(ravtech, config);
	}

}
