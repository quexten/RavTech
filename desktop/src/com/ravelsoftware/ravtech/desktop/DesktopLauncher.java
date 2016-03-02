package com.ravelsoftware.ravtech.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowListener;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.files.zip.ArchiveFileHandleResolver;
import com.ravelsoftware.ravtech.scripts.Script;
import com.ravelsoftware.ravtech.scripts.lua.LuaJScriptLoader;

public class DesktopLauncher {
    public static void main(String[] arg) {
        final Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("test");
        config.setWindowedMode(1280, 720);
        config.setDecorated(true);
        config.setWindowListener(new Lwjgl3WindowListener() {
            @Override
            public void iconified() {
            }

            @Override
            public void deiconified() {
            }

            @Override
            public void focusLost() {
            }

            @Override
            public void focusGained() {
            }

            @Override
            public boolean windowIsClosing() {
                System.exit(0);
                return false;
            }

        });
        config.setTitle("RavTech - " + RavTech.majorVersion + "." + RavTech.minorVersion + "." + RavTech.microVersion);
        RavTech ravtech = new RavTech(new ArchiveFileHandleResolver(new Lwjgl3Files().internal("resourcepack.ravpack")));
        RavTech.files.getAssetManager().setLoader(Script.class, new LuaJScriptLoader(RavTech.files.getResolver()));
        new Lwjgl3Application(ravtech, config);
    }
}
