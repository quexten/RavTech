
package com.quexten.ravtech.components;

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
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.animation.VariableAccessor;
import com.quexten.ravtech.util.JsonUtil;

public class SpriteRenderer extends Renderer
	implements Json.Serializable, VariableAccessor {

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
	private Color color = Color.WHITE.cpy();
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
	public SpriteRenderer () {
		this("textures/error.png", 2, 2);
		Array<AssetDescriptor> dependencies = new Array<AssetDescriptor>();
		load(dependencies);
		RavTech.files.loadAssets(dependencies);
		RavTech.files.finishLoading();
	}

	public SpriteRenderer (String texturePath, float width,
		float height) {
		super();
		this.width = width;
		this.height = height;
		this.texturePath = texturePath;
	}

	@Override
	public void load (
		@SuppressWarnings("rawtypes") Array<AssetDescriptor> dependencies) {
		dependencies.add(
			new AssetDescriptor<Texture>(texturePath, Texture.class));
		RavTech.files.addDependency(texturePath, this);
	}

	@Override
	public void finishedLoading () {
		texture = RavTech.files.getAsset(texturePath);
		srcX = 0;
		srcY = 0;
		srcWidth = texture.getWidth();
		srcHeight = texture.getHeight();
	}

	@Override
	public void update () {
	}

	@Override
	public void draw (SpriteBatch batch) {
		batch.setColor(getColor());
		if (texture != null) {
			float halfWidth = width / 2;
			float halfHeight = height / 2;
			float originWidth = originX * halfWidth + halfWidth;
			float originHeight = originY * halfHeight + halfHeight;
			batch.draw(texture,
				getParent().transform.getPosition().x - originWidth,
				getParent().transform.getPosition().y - originHeight,
				originWidth, originHeight, width, height,
				getParent().transform.getLocalScale().x,
				getParent().transform.getLocalScale().y,
				getParent().transform.getRotation(), srcX, srcY, srcWidth,
				srcHeight, false, false);
		}
		batch.setColor(Color.WHITE);
	}

	@Override
	public void dispose () {
		RavTech.files.removeDependency(texturePath, this);
	}

	public void setTexture (String texturePath) {
		RavTech.files.addDependency(texturePath, this);
		this.texturePath = texturePath;
		if (!RavTech.files.isLoaded(SpriteRenderer.this.texturePath))
			RavTech.files.loadAsset(SpriteRenderer.this.texturePath,
				Texture.class, false);
		RavTech.files.finishLoading();
		texture = RavTech.files
			.getAsset(SpriteRenderer.this.texturePath);
		srcWidth = texture.getWidth();
		srcHeight = texture.getHeight();
	}

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
		if (regionName != null)
			json.writeValue(regionName);
		json.writeValue("srcX", srcX);
		json.writeValue("srcY", srcY);
		json.writeValue("srcWidth", srcWidth);
		json.writeValue("srcHeight", srcHeight);
		json.writeValue("originX", originX);
		json.writeValue("originY", originY);
		json.writeValue("minFilter",
			minFilter == TextureFilter.Linear ? "Linear" : "Nearest");
		json.writeValue("magFilter",
			magFilter == TextureFilter.Linear ? "Linear" : "Nearest");
		JsonUtil.writeColorToJson(json, color, "tint");
		json.writeValue("uWrap", uWrap == TextureWrap.ClampToEdge
			? "ClampToEdge"
			: uWrap == TextureWrap.Repeat ? "Repeat" : "MirroredRepeat");
		json.writeValue("vWrap", vWrap == TextureWrap.ClampToEdge
			? "ClampToEdge"
			: vWrap == TextureWrap.Repeat ? "Repeat" : "MirroredRepeat");
	}

	@Override
	public void read (Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		if (jsonData.has("width"))
			width = jsonData.getFloat("width");
		if (jsonData.has("height"))
			height = jsonData.getFloat("height");
		if (jsonData.has("texture"))
			texturePath = jsonData.getString("texture");
		if (jsonData.has("srcX")) {
			useCustomSrc = true;
			srcX = jsonData.getInt("srcX");
		}
		if (jsonData.has("srcY"))
			srcY = jsonData.getInt("srcY");
		if (jsonData.has("srcWidth"))
			srcWidth = jsonData.getInt("srcWidth");
		if (jsonData.has("srcHeight"))
			srcHeight = jsonData.getInt("srcHeight");
		if (jsonData.has("originX"))
			originX = jsonData.getFloat("originX");
		if (jsonData.has("originY"))
			originY = jsonData.getFloat("originY");
		if (jsonData.has("minFilter"))
			minFilter = jsonData.getString("minFilter").equals("Linear")
				? TextureFilter.Linear : TextureFilter.Nearest;
		if (jsonData.has("magFilter"))
			magFilter = jsonData.getString("magFilter").equals("Linear")
				? TextureFilter.Linear : TextureFilter.Nearest;
		if (jsonData.has("tint"))
			setColor(JsonUtil.readColorFromJson(jsonData, "tint"));
		if (jsonData.has("uWrap")) {
			String uWrapStrings = jsonData.getString("uWrap");
			uWrap = uWrapStrings.equals("ClampToEdge")
				? TextureWrap.ClampToEdge
				: uWrapStrings.equals("Repeat") ? TextureWrap.Repeat
					: TextureWrap.MirroredRepeat;
		}
		if (jsonData.has("vWrap")) {
			String vWrapStrings = jsonData.getString("vWrap");
			vWrap = vWrapStrings.equals("ClampToEdge")
				? TextureWrap.ClampToEdge
				: vWrapStrings.equals("Repeat") ? TextureWrap.Repeat
					: TextureWrap.MirroredRepeat;
		}
	}

	@Override
	public String[] getVariableNames () {
		return new String[] {"sortingLayerName", "sortingOrder",
			"width", "height", "texture", "regionName", "srcX", "srcY",
			"srcWidth", "srcHeight", "originX", "originY", "minFilter",
			"magFilter", "tint", "uTextureWrap", "vTextureWrap"};
	}

	@Override
	public void setVariable (int variableId, Object value) {
		switch (variableId) {
			case 0:
				sortingLayerName = String.valueOf(value);
				break;
			case 1:
				String varString = String.valueOf(value);
				if (varString.indexOf('.') > 0)
					varString = varString.substring(0,
						varString.indexOf('.'));
				sortingOrder = Integer.valueOf(varString);
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
				useCustomSrc = true;
				break;
			case 7:
				srcY = Math.round(Float.valueOf(value.toString()));
				useCustomSrc = true;
				break;
			case 8:
				srcWidth = Math.round(Float.valueOf(value.toString()));
				useCustomSrc = true;
				break;
			case 9:
				srcHeight = Math.round(Float.valueOf(value.toString()));
				useCustomSrc = true;
				break;
			case 10:
				originX = Float.valueOf(value.toString());
				break;
			case 11:
				originY = Float.valueOf(value.toString());
				break;
			case 12:
				if (value instanceof String)
					value = value.equals("Linear") ? TextureFilter.Linear
						: value.equals("Nearest") ? TextureFilter.Nearest
							: TextureFilter.MipMap;
				minFilter = (TextureFilter)value;
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run () {
						texture.setFilter(minFilter, magFilter);
					}
				});
				break;
			case 13:
				if (value instanceof String)
					value = value.equals("Linear") ? TextureFilter.Linear
						: value.equals("Nearest") ? TextureFilter.Nearest
							: TextureFilter.MipMap;
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
				if (value instanceof String)
					value = value.equals("ClampToEdge")
						? TextureWrap.ClampToEdge
						: value.equals("Repeat") ? TextureWrap.Repeat
							: TextureWrap.MirroredRepeat;
				uWrap = (TextureWrap)value;
				Gdx.app.postRunnable(new Runnable() {

					@Override
					public void run () {
						texture.setWrap(uWrap, vWrap);
					}
				});
				break;
			case 16:
				if (value instanceof String)
					value = value.equals("ClampToEdge")
						? TextureWrap.ClampToEdge
						: value.equals("Repeat") ? TextureWrap.Repeat
							: TextureWrap.MirroredRepeat;
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
			if (variables[i].equals(variableName))
				return i;
		return -1;
	}

	@Override
	public Object getVariable (int variableID) {
		switch (variableID) {
			case 0:
				return sortingLayerName;
			case 1:
				return sortingOrder;
			case 2:
				return width;
			case 3:
				return height;
			case 4:
				return texturePath;
			case 5:
				return texturePath;
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
