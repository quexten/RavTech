/*******************************************************************************
 * Copyright 2014-2016 Bernd Schoolmann
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


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
