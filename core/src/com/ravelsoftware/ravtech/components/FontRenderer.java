
package com.ravelsoftware.ravtech.components;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.util.JsonUtil;

public class FontRenderer extends Renderer {

	@Override
	public ComponentType getType () {
		return ComponentType.FontRenderer;
	}

	public boolean flip;
	public TextureFilter minFilter = TextureFilter.Linear;
	public TextureFilter magFilter = TextureFilter.Linear;
	String path = "fonts/font.fnt";
	String text = "HelloWorld!";
	Color tint = Color.WHITE;
	
	float xOffset = 0;
	float yOffset = 0;
	BitmapFont font;

	public FontRenderer () {
	}

	@Override
	public void load (Array<AssetDescriptor> dependencies) {
		dependencies.add(new AssetDescriptor<BitmapFont>(this.path, BitmapFont.class));
		RavTech.files.addDependency(this.path, this);
	}

	@Override
	public void finishedLoading () {
		this.font = RavTech.files.getAsset(this.path);
		font.getCache().addText(text, 0, 0);
	}

	@Override
	public void update () {
	}

	@Override
	public void dispose () {
	}

	@Override
	public void draw (SpriteBatch batch) {
		font.setColor(this.tint);
		font.getData().setScale(0.05f);
		font.setUseIntegerPositions(false);
		GlyphLayout layout = font.draw(batch, text, getParent().transform.getPosition().x - xOffset / 2,
			getParent().transform.getPosition().y + yOffset / 2);
		xOffset = layout.width;
		yOffset = layout.height;
	}

	public void setText (String text) {
		this.text = text;
	}

	public String getText () {
		return text;
	}

	public void setColor(Color color) {
		this.tint = color;
	}
	
	@Override
	public void write (Json json) {
		super.write(json);
		json.writeValue("path", path);
		json.writeValue("text", text);
		JsonUtil.writeColorToJson(json, tint, "tint");
		json.writeValue("minFilter", minFilter == TextureFilter.Linear ? "Linear" : "Nearest");
		json.writeValue("magFilter", magFilter == TextureFilter.Linear ? "Linear" : "Nearest");
	}

	@Override
	public void read (Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		if (jsonData.has("text")) text = jsonData.getString("text");
		if (jsonData.has("tint")) tint = JsonUtil.readColorFromJson(jsonData, "tint");
		if (jsonData.has("minFilter"))
			this.minFilter = jsonData.getString("minFilter").equals("Linear") ? TextureFilter.Linear : TextureFilter.Nearest;
		if (jsonData.has("magFilter"))
			this.magFilter = jsonData.getString("magFilter").equals("Linear") ? TextureFilter.Linear : TextureFilter.Nearest;
	}

	@Override
	public void setVariable (int variableID, Object value) {
		switch (variableID) {
		case 0:
			this.text = String.valueOf(value);
			break;
		}
	}

	@Override
	public int getVariableId (String variableName) {
		switch (variableName) {
		case "text":
			return 0;
		}
		return -1;
	}

	@Override
	public Object getVariable (int variableID) {
		return null;
	}

	@Override
	public String[] getVariableNames () {
		return null;
	}

	@Override
	public Object[] getValiables () {
		return null;
	}

}
