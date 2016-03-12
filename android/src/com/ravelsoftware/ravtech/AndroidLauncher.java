
package com.ravelsoftware.ravtech;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidFiles;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.files.zip.ArchiveFileHandleResolver;
import com.ravelsoftware.ravtech.scripts.Script;
import com.ravelsoftware.ravtech.scripts.lua.LuaJScriptLoader;

public class AndroidLauncher extends AndroidApplication {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidFiles files = new AndroidFiles(this.getAssets());
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        RavTech ravtech = new RavTech(new ArchiveFileHandleResolver(
            files.external("Android/obb/com.ravelsoftware.ravtech.android/main.1.com.ravelsoftware.ravtech.android.obb")));
        RavTech.files.getAssetManager().setLoader(Script.class, new LuaJScriptLoader(RavTech.files.getResolver()));
        initialize(ravtech, config);
    }
}
