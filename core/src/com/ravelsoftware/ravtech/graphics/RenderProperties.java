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
package com.ravelsoftware.ravtech.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.ravelsoftware.ravtech.util.JsonUtil;

public class RenderProperties implements Serializable {

    public Color backgroundColor = new Color(0.5f, 0.5f, 0.5f, 1);
    public Color ambientLightColor = new Color(0.1f, 0.1f, 0.1f, 0.5f);
    public Array<SortingLayer> sortingLayers = new Array<SortingLayer>();

    public RenderProperties() {
        backgroundColor = Color.GRAY;
        SortingLayer layer = new SortingLayer("Default");
        sortingLayers.add(layer);
        sortingLayers.add(new SortingLayer("Foreground"));
    }

    @Override
    public void write (Json json) {
        json.writeArrayStart("sortingLayers");
        for (int i = 0; i < sortingLayers.size; i++)
            json.writeValue(sortingLayers.get(i));
        json.writeArrayEnd();
        JsonUtil.writeColorToJson(json, backgroundColor, "backgroundColor");
        JsonUtil.writeColorToJson(json, ambientLightColor, "ambientLightColor");
    }

    @Override
    public void read (Json json, JsonValue jsonData) {
        sortingLayers.clear();
        JsonValue sortingLayersValue = jsonData.get("sortingLayers");
        JsonValue currentLayerValue = sortingLayersValue.child();
        while (currentLayerValue != null) {
            sortingLayers.add(json.fromJson(SortingLayer.class, currentLayerValue.toString()));
            currentLayerValue = currentLayerValue.next();
        }
        this.backgroundColor = JsonUtil.readColorFromJson(jsonData, "backgroundColor");
        this.ambientLightColor = JsonUtil.readColorFromJson(jsonData, "ambientLightColor");
    }
}
