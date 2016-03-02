
package com.ravelsoftware.ravtech.scripts.lua;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.ravelsoftware.ravtech.scripts.Script;
import com.ravelsoftware.ravtech.scripts.ScriptLoaderParameter;

public class LuaJScriptLoader extends AsynchronousAssetLoader<Script, ScriptLoaderParameter> {

	public LuaJScriptLoader (FileHandleResolver resolver) {
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
		LuaJScript script = new LuaJScript(file.readString(), parameter.selfObject);
		return script;
	}
}
