
package com.ravelsoftware.ravtech;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidFiles;
import com.ravelsoftware.ravtech.files.zip.ArchiveFileHandleResolver;
import com.ravelsoftware.ravtech.scripts.Script;
import com.ravelsoftware.ravtech.scripts.lua.LuaJScriptLoader;

import android.os.Bundle;

public class AndroidLauncher extends AndroidApplication {

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidFiles files = new AndroidFiles(this.getAssets());
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		int versionCode = 0;
		try {
			versionCode = this.getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		RavTech ravtech = new RavTech(
			new ArchiveFileHandleResolver(files.external("Android/obb/" + getPackageName() + "/main." + versionCode + "." + getPackageName() + ".obb")));
		RavTech.files.getAssetManager().setLoader(Script.class, new LuaJScriptLoader(RavTech.files.getResolver()));
		initialize(ravtech, config);
	}

}
