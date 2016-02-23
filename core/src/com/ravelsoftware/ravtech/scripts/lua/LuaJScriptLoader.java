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

    public LuaJScriptLoader(FileHandleResolver resolver) {
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
