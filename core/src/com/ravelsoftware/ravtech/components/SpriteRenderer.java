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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.animation.VariableAccessor;
import com.ravelsoftware.ravtech.util.JsonUtil;

public class SpriteRenderer extends Renderer implements Json.Serializable, VariableAccessor {

    @Override
    public ComponentType getType () {
        return ComponentType.SpriteRenderer;
    }

    @Override
    public String getName () {
        return getType().toString();
    }

    public String texturePath;
    Texture texture;
    public float width, height;
    private Color color = Color.WHITE;
    public String regionName;
    public int srcX, srcY;
    public int srcWidth, srcHeight;
    boolean useCustomSrc = false;
    public float originX, originY;
    public TextureFilter minFilter = TextureFilter.Linear;
    public TextureFilter magFilter = TextureFilter.Linear;
    public TextureWrap uWrap = TextureWrap.ClampToEdge;
    public TextureWrap vWrap = TextureWrap.ClampToEdge;

    @SuppressWarnings("rawtypes")
    public SpriteRenderer() {
        this("textures/error.png", 2, 2);
        Array<AssetDescriptor> dependencies = new Array<AssetDescriptor>();
        this.load(dependencies);
        RavTech.files.loadAssets(dependencies);
        RavTech.files.finishLoading();
    }
    
    public SpriteRenderer(String texturePath, float width, float height) {
        super();
        this.width = width;
        this.height = height;
        this.texturePath = texturePath;
    }

    @Override
    public void load (@SuppressWarnings("rawtypes") Array<AssetDescriptor> dependencies) {
        dependencies.add(new AssetDescriptor<Texture>(this.texturePath, Texture.class));
    }

    @Override
    public void finishedLoading () {
        this.texture = RavTech.files.getAsset(texturePath);
        srcX = 0;
        srcY = 0;
        srcWidth = this.texture.getWidth();
        srcHeight = this.texture.getHeight();
    }

    @Override
    public void update () {
    }

    @Override
    public void draw (SpriteBatch batch) {
        batch.setColor(getColor());
        if (texture != null) batch.draw(texture,
            getParent().transform.getPosition().x - width / 2 * (getParent().transform.isFlippedX() ? 1 : -1)
                - originX * width / 2 * (getParent().transform.isFlippedX() ? 1 : -1),
            getParent().transform.getPosition().y - height / 2 * (getParent().transform.flippedY ? 1 : -1)
                - originY * height / 2 * (getParent().transform.flippedY ? 1 : -1),
            originX * width * (getParent().transform.isFlippedX() ? 1 : -1) / 2
                + width * (getParent().transform.isFlippedX() ? 1 : -1) / 2,
            originY * height * (getParent().transform.flippedY ? 1 : -1) / 2
                + height * (getParent().transform.flippedY ? 1 : -1) / 2,
            width * (getParent().transform.isFlippedX() ? 1 : -1), height * (getParent().transform.flippedY ? 1 : -1),
            (getParent().transform.isFlippedX() ? 1 : -1) * 1, getParent().transform.isFlippedX() ? 1 : -1 * 1,
            getParent().transform.getRotation(), srcX, srcY, srcWidth, srcHeight, false, false);
        batch.setColor(Color.WHITE);
    }

    @Override
    public void dispose () {
    }

    public void setTexture (final String texturePath) {
        Gdx.app.postRunnable(new Runnable() {

            @Override
            public void run () {
                SpriteRenderer.this.texturePath = texturePath;
                RavTech.files.loadAsset(SpriteRenderer.this.texturePath, Texture.class, false);
                RavTech.files.finishLoading();
                SpriteRenderer.this.texture = RavTech.files.getAsset(SpriteRenderer.this.texturePath);
            }
        });
    }

    /*
     * public void loadTexture() {
     *
     * /* Gdx.app.postRunnable(new Runnable() {
     *
     * @Override public void run() { FileHandleResolver fileHandleResolver = RavTech.files.getResolver(); // RavTech.isEditor ?
     * new InternalFileHandleResolver() : new // ArchiveFileHandleResolver(RavTech.files.getFileHandle()); if
     * (texturePath.endsWith(".png") || texturePath.endsWith(".jpg")) { RavTech.assetManager.setLoader(Texture.class, new
     * TextureLoader(fileHandleResolver)); RavTech.assetManager.load(RavTech.isEditor ? texturePath : texturePath, Texture.class);
     * RavTech.assetManager.finishLoading(); texture = RavTech.assetManager.get(RavTech.isEditor ? texturePath : texturePath,
     * Texture.class); if (!useCustomSrc || srcWidth == 0 && srcHeight == 0) { srcX = 0; srcY = 0; srcWidth = texture.getWidth();
     * srcHeight = texture.getHeight(); } } else if (texturePath.endsWith(".atlas")) {
     * RavTech.assetManager.setLoader(TextureAtlas.class, new TextureAtlasLoader(fileHandleResolver));
     * RavTech.assetManager.load(RavTech.isEditor ? texturePath : texturePath, TextureAtlas.class);
     * RavTech.assetManager.finishLoading(); TextureAtlas atlas = RavTech.assetManager.get(RavTech.isEditor ? texturePath :
     * texturePath, TextureAtlas.class); TextureRegion region = atlas.findRegion(regionName); texture = region.getTexture(); if
     * (!useCustomSrc) { srcX = region.getRegionX(); srcY = region.getRegionY(); srcWidth = region.getRegionWidth(); srcHeight =
     * region.getRegionHeight(); } minFilter = texture.getMinFilter(); magFilter = texture.getMagFilter(); } else {
     * SpriteRenderer.this.sortingLayerName = "Foreground"; if (!useCustomSrc) { srcX = 0; srcY = 0; srcWidth =
     * texture.getWidth(); srcHeight = texture.getHeight(); } } texture.setFilter(minFilter, magFilter); texture.setWrap(uWrap,
     * vWrap); SpriteRenderer.this.sortingLayerName = "Default"; } });
     *
     * }
     */
    public void setColor (Color color) {
        this.color = color;
    }

    public Color getColor () {
        return color;
    }

    // Serialize//
    @Override
    public void write (Json json) {
        super.write(json);
        json.writeValue("width", width);
        json.writeValue("height", height);
        json.writeValue("texture", texturePath);
        if (regionName != null) json.writeValue(regionName);
        json.writeValue("srcX", srcX);
        json.writeValue("srcY", srcY);
        json.writeValue("srcWidth", srcWidth);
        json.writeValue("srcHeight", srcHeight);
        json.writeValue("originX", originX);
        json.writeValue("originY", originY);
        json.writeValue("minFilter", minFilter == TextureFilter.Linear ? "Linear" : "Nearest");
        json.writeValue("magFilter", magFilter == TextureFilter.Linear ? "Linear" : "Nearest");
        JsonUtil.writeColorToJson(json, color, "tint");
        json.writeValue("uWrap",
            uWrap == TextureWrap.ClampToEdge ? "ClampToEdge" : uWrap == TextureWrap.Repeat ? "Repeat" : "MirroredRepeat");
        json.writeValue("vWrap",
            vWrap == TextureWrap.ClampToEdge ? "ClampToEdge" : vWrap == TextureWrap.Repeat ? "Repeat" : "MirroredRepeat");
    }

    @Override
    public void read (Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        if (jsonData.has("width")) this.width = jsonData.getFloat("width");
        if (jsonData.has("height")) this.height = jsonData.getFloat("height");
        if (jsonData.has("texture")) this.texturePath = jsonData.getString("texture");
        if (jsonData.has("srcX")) {
            this.useCustomSrc = true;
            this.srcX = jsonData.getInt("srcX");
        }
        if (jsonData.has("srcY")) this.srcY = jsonData.getInt("srcY");
        if (jsonData.has("srcWidth")) this.srcWidth = jsonData.getInt("srcWidth");
        if (jsonData.has("srcHeight")) this.srcHeight = jsonData.getInt("srcHeight");
        if (jsonData.has("originX")) this.originX = jsonData.getFloat("originX");
        if (jsonData.has("originY")) this.originY = jsonData.getFloat("originY");
        if (jsonData.has("minFilter"))
            this.minFilter = jsonData.getString("minFilter").equals("Linear") ? TextureFilter.Linear : TextureFilter.Nearest;
        if (jsonData.has("magFilter"))
            this.magFilter = jsonData.getString("magFilter").equals("Linear") ? TextureFilter.Linear : TextureFilter.Nearest;
        if (jsonData.has("tint")) setColor(JsonUtil.readColorFromJson(jsonData, "tint"));
        if (jsonData.has("uWrap")) {
            String uWrapStrings = jsonData.getString("uWrap");
            uWrap = uWrapStrings.equals("ClampToEdge") ? TextureWrap.ClampToEdge
                : uWrapStrings.equals("Repeat") ? TextureWrap.Repeat : TextureWrap.MirroredRepeat;
        }
        if (jsonData.has("vWrap")) {
            String vWrapStrings = jsonData.getString("vWrap");
            vWrap = vWrapStrings.equals("ClampToEdge") ? TextureWrap.ClampToEdge
                : vWrapStrings.equals("Repeat") ? TextureWrap.Repeat : TextureWrap.MirroredRepeat;
        }
    }

    @Override
    public String[] getVariableNames () {
        return new String[] {"sortingLayerName", "sortingOrder", "width", "height", "texture", "regionName", "srcX", "srcY",
            "srcWidth", "srcHeight", "originX", "originY", "minFilter", "magFilter", "tint", "uTextureWrap", "vTextureWrap"};
    }

    @Override
    public void setVariable (int variableID, Object value) {
        switch (variableID) {
            case 0:
                this.sortingLayerName = String.valueOf(value);
                break;
            case 1:
                this.sortingOrder = Integer.valueOf(String.valueOf(value));
                break;
            case 2:
                width = Float.valueOf(String.valueOf(value.toString()));
                break;
            case 3:
                height = Float.valueOf(String.valueOf(value.toString()));
                break;
            case 4:
                setTexture(String.valueOf(value.toString()));
                break;
            case 5:
                setTexture(String.valueOf(value.toString()));
                break;
            case 6:
                srcX = Math.round(Float.valueOf(value.toString()));
                this.useCustomSrc = true;
                break;
            case 7:
                srcY = Math.round(Float.valueOf(value.toString()));
                this.useCustomSrc = true;
                break;
            case 8:
                srcWidth = Math.round(Float.valueOf(value.toString()));
                this.useCustomSrc = true;
                break;
            case 9:
                srcHeight = Math.round(Float.valueOf(value.toString()));
                this.useCustomSrc = true;
                break;
            case 10:
                originX = Float.valueOf(value.toString());
                break;
            case 11:
                originY = Float.valueOf(value.toString());
                break;
            case 12:
                if (value instanceof String) value = value.equals("Linear") ? TextureFilter.Linear
                    : value.equals("Nearest") ? TextureFilter.Nearest : TextureFilter.MipMap;
                minFilter = (TextureFilter)value;
                Gdx.app.postRunnable(new Runnable() {

                    @Override
                    public void run () {
                        texture.setFilter(minFilter, magFilter);
                    }
                });
                break;
            case 13:
                if (value instanceof String) value = value.equals("Linear") ? TextureFilter.Linear
                    : value.equals("Nearest") ? TextureFilter.Nearest : TextureFilter.MipMap;
                magFilter = (TextureFilter)value;
                Gdx.app.postRunnable(new Runnable() {

                    @Override
                    public void run () {
                        texture.setFilter(minFilter, magFilter);
                    }
                });
                break;
            case 14:
                color = (Color)value;
                break;
            case 15:
                if (value instanceof String) value = value.equals("ClampToEdge") ? TextureWrap.ClampToEdge
                    : value.equals("Repeat") ? TextureWrap.Repeat : TextureWrap.MirroredRepeat;
                uWrap = (TextureWrap)value;
                Gdx.app.postRunnable(new Runnable() {

                    @Override
                    public void run () {
                        texture.setWrap(uWrap, vWrap);
                    }
                });
                break;
            case 16:
                if (value instanceof String) value = value.equals("ClampToEdge") ? TextureWrap.ClampToEdge
                    : value.equals("Repeat") ? TextureWrap.Repeat : TextureWrap.MirroredRepeat;
                vWrap = (TextureWrap)value;
                Gdx.app.postRunnable(new Runnable() {

                    @Override
                    public void run () {
                        texture.setWrap(uWrap, vWrap);
                    }
                });
                break;
        }
    }

    @Override
    public int getVariableId (String variableName) {
        String[] variables = getVariableNames();
        for (int i = 0; i < variables.length; i++)
            if (variables[i].equals(variableName)) return i;
        return -1;
    }

    @Override
    public Object getVariable (int variableID) {
        switch (variableID) {
            case 0:
                return this.sortingLayerName;
            case 1:
                return this.sortingOrder;
            case 2:
                return width;
            case 3:
                return height;
            case 4:
                return this.texturePath;
            case 5:
                return this.texturePath;
            case 6:
                return srcX;
            case 7:
                return srcY;
            case 8:
                return srcWidth;
            case 9:
                return srcHeight;
            case 10:
                return originX;
            case 11:
                return originY;
            case 12:
                return minFilter;
            case 13:
                return magFilter;
            case 14:
                return color;
            case 15:
                return uWrap;
            case 16:
                return vWrap;
        }
        return null;
    }

    @Override
    public Object[] getValiables () {
        return null;
    }
}
