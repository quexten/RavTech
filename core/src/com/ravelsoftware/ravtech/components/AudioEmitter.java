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
package com.ravelsoftware.ravtech.components;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ravelsoftware.ravtech.RavTech;

public class AudioEmitter extends GameComponent implements Json.Serializable {

    public ComponentType getType () {
        return ComponentType.AudioEmitter;
    }

    public String getName () {
        return getType().toString();
    }

    public String filePath;
    public boolean isMusic = false;
    public boolean playOnCreate = false;
    public boolean loop = false;
    public long id;

    public AudioEmitter() {
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void load (Array<AssetDescriptor> dependencies) {
        AssetDescriptor<Sound> assetDescriptor = new AssetDescriptor<Sound>(RavTech.files.getAssetHandle(filePath), Sound.class);
        dependencies.add(assetDescriptor);
    }

    @Override
    public void finishedLoading () {
    }

    @Override
    public void update () {
        if (id == 0L && playOnCreate) {
            play();
            playOnCreate = false;
        }
    }

    @Override
    public void draw (SpriteBatch batch) {
    }

    @Override
    public void dispose () {
        Sound sound = (Sound)RavTech.files.getAsset(filePath);
        sound.stop(id);
    }

    public void setClip (String path) {
        path = path.replaceAll("\\\\", "/");
        this.filePath = path;
        RavTech.files.loadAsset(this.filePath, Sound.class);
    }

    public void setPitch (float pitch) {
        ((Sound)RavTech.files.getAsset(this.filePath)).setPitch(id, pitch);
    }

    public void play () {
        id = ((Sound)RavTech.files.getAsset(this.filePath)).play();
    }

    @Override
    public void write (Json json) {
        json.writeValue("path", filePath);
        json.writeValue("isMusic", isMusic);
        json.writeValue("playOnCreate", playOnCreate);
        json.writeValue("loop", loop);
    }

    @Override
    public void read (Json json, JsonValue jsonData) {
        filePath = jsonData.getString("path");
        isMusic = false;
        loop = jsonData.getBoolean("loop");
        playOnCreate = jsonData.getBoolean("playOnCreate");
    }

    @Override
    public String[] getVariableNames () {
        return null;
    }

    @Override
    public void setVariable (int variableID, Object value) {
    }

    @Override
    public int getVariableId (String variableName) {
        return 0;
    }

    @Override
    public Object getVariable (int variableID) {
        return 0;
    }

    @Override
    public Object[] getValiables () {
        return null;
    }
}
