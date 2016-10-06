
package com.quexten.ravtech;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidFiles;
import com.quexten.ravtech.android.AndroidEngineConfiguration;
import com.quexten.ravtech.files.zip.ArchiveFileHandleResolver;
import com.quexten.ravtech.net.kryonet.KryonetTransportLayer;
import com.quexten.ravtech.remoteedit.RemoteEditConnectionScreen;
import com.quexten.ravtech.scripts.lua.LuaJScriptLoader;

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
