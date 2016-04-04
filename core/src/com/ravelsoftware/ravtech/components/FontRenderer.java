
package com.ravelsoftware.ravtech.components;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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
	
	@Override
	public String getName () {
		return "FontRenderer";
	}

	public String path = "fonts/font.fnt";
	BitmapFont font;

	String text = "HelloWorld!";

	public boolean centered = true;
	public boolean flipped = false;
	float xOffset = 0;
	float yOffset = 0;
	public float xScale = 1;
	public float yScale = 1;

	Color tint = Color.WHITE;
	public TextureFilter minFilter = TextureFilter.Linear;
	public TextureFilter magFilter = TextureFilter.Linear;

	private final Matrix4 fontMatrix = new Matrix4();
	private final static Matrix4 resetMatrix = new Matrix4();

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
		font.getData().setScale(0.05f);
		font.setUseIntegerPositions(false);
	}

	@Override
	public void update () {
	}

	@Override
	public void draw (SpriteBatch batch) {
		Matrix4 oldTransformMatrix = batch.getTransformMatrix().cpy();

		fontMatrix.set(resetMatrix);
		fontMatrix.rotate(Vector3.Z, getParent().transform.getRotation());
		Vector2 position = getParent().transform.getPosition();
		fontMatrix.trn(position.x, position.y, 0);
		batch.setTransformMatrix(fontMatrix);

		font.setColor(this.tint);
		font.getData().setScale(0.05f * xScale, 0.05f * yScale);
		GlyphLayout layout = font.draw(batch, text, centered ? -xOffset / 2 : 0, centered ? yOffset / 2 : 0);
		xOffset = layout.width;
		yOffset = layout.height;

		batch.setTransformMatrix(oldTransformMatrix);
	}

	@Override
	public void dispose () {
	}

	/** Sets the text
	 * @param text - the text */
	public void setText (String text) {
		this.text = text;
	}

	/** Gets the Text */
	public String getText () {
		return text;
	}

	/** Sets the Tint
	 * @param color - the tint */
	public void setColor (Color color) {
		this.tint = color;
	}

	/** Gets the Tint */
	public Color getColor () {
		return this.tint;
	}

	@Override
	public void write (Json json) {
		super.write(json);
		json.writeValue("path", path);
		json.writeValue("text", text);
		json.writeValue("centerText", centered);
		json.writeValue("flipped", flipped);
		json.writeValue("xScale", xScale);
		json.writeValue("yScale", yScale);
		JsonUtil.writeColorToJson(json, tint, "tint");
		json.writeValue("minFilter", minFilter == TextureFilter.Linear ? "Linear" : "Nearest");
		json.writeValue("magFilter", magFilter == TextureFilter.Linear ? "Linear" : "Nearest");
	}

	@Override
	public void read (Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		if (jsonData.has("path")) this.path = jsonData.getString("path");
		if (jsonData.has("text")) text = jsonData.getString("text");
		if (jsonData.has("centerText")) centered = jsonData.getBoolean("centerText");
		if (jsonData.has("flipped")) flipped = jsonData.getBoolean("flip");
		if (jsonData.has("xScale")) xScale = jsonData.getFloat("xScale");
		if (jsonData.has("yScale")) yScale = jsonData.getFloat("yScale");
		if (jsonData.has("tint")) tint = JsonUtil.readColorFromJson(jsonData, "tint");
	}

	@Override
	public void setVariable (int variableID, Object value) {
		switch (variableID) {
		case 0:
			this.path = String.valueOf(value);
			break;
		case 1:
			this.text = String.valueOf(value);
			break;
		case 2:
			this.centered = Boolean.valueOf(String.valueOf(value));
			break;
		case 3:
			this.flipped = Boolean.valueOf(String.valueOf(value));
			break;
		case 4:
			this.xScale = Float.valueOf(String.valueOf(value));
			break;
		case 5:
			this.yScale = Float.valueOf(String.valueOf(value));
			break;
		case 6:
			this.tint = ((Color)value);
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
			return this.path;
		case 1:
			return this.text;
		case 2:
			return this.centered;
		case 3:
			return this.flipped;
		case 4:
			return this.xScale;
		case 5:
			return this.yScale;
		case 6:
			return this.tint;
		}
		return null;
	}

	@Override
	public String[] getVariableNames () {
		return new String[] {"path", "text", "centered", "flipped", "xScale", "yScale", "tint"};
	}

	@Override
	public Object[] getValiables () {
		return new Object[] {this.path, this.text, this.centered, this.flipped, this.xScale, this.yScale, this.tint};
	}

	public void setFont (String path) {
		if(!RavTech.files.isLoaded(path)) {
			RavTech.files.loadAsset(path, BitmapFont.class);
			RavTech.files.finishLoading();
		}
		this.path = path;
		this.finishedLoading();
	}

}
