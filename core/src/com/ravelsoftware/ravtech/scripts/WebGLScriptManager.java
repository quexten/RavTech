package com.ravelsoftware.ravtech.scripts;

import com.badlogic.gdx.utils.Array;

public class WebGLScriptManager {

    static Array<Script> scripts = new Array<Script>();
    public static boolean initialized;

    public static boolean areLoaded () {
        boolean loaded = true;
        for (int i = 0; i < scripts.size; i++)
            if (!scripts.get(i).isLoaded()) {
                loaded = false;
                break;
            }
        return loaded;
    }

    public static void initialize () {
        initialized = true;
        for (int i = 0; i < scripts.size; i++)
            scripts.get(i).init();
    }

    public static void registerScript (Script script) {
        scripts.add(script);
    }
}
