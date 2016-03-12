
package com.ravelsoftware.ravtech.scripts.luajs;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.ravelsoftware.ravtech.scripts.Script;
import com.ravelsoftware.ravtech.scripts.ScriptLoaderParameter;

public class MoonshineJSScriptLoader extends AsynchronousAssetLoader<Script, ScriptLoaderParameter> {

    public MoonshineJSScriptLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Array<AssetDescriptor> getDependencies (String fileName, FileHandle file, ScriptLoaderParameter parameter) {
        return new Array<AssetDescriptor>();
    }

    @Override
    public void loadAsync (AssetManager manager, String fileName, FileHandle file, ScriptLoaderParameter parameter) {
    }

    @Override
    public Script loadSync (AssetManager manager, String fileName, FileHandle file, ScriptLoaderParameter parameter) {
        MoonshineJSScript script = new MoonshineJSScript(file.readString(), parameter.selfObject);
        return script;
    }
}
