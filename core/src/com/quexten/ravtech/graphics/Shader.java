
package com.quexten.ravtech.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.util.Debug;
import com.thesecretpie.shader.ShaderManager;

public class Shader implements Serializable {

	String name;
	ShaderManager manager;
	boolean individualShaders = false;
	String vertexShader, fragmentShader;
	ObjectMap<String, String> textures = new ObjectMap<String, String>();

	public Shader (String shaderProgram) {
		this.manager = RavTech.sceneHandler.shaderManager;
		this.name = shaderProgram;
	}

	public Shader () {
		individualShaders = true;
		this.manager = RavTech.sceneHandler.shaderManager;
	}

	public void setFloat (String valueName, float value) {
		manager.setUniformf(valueName, value);
	}

	public void setTexture (String valueName, String texture) {
		manager.setUniformTexture(valueName,
			texture.endsWith(".framebuffer")
				? RavTech.sceneHandler.shaderManager.getFBTexture(texture.substring(0, texture.length() - ".framebuffer".length()))
				: (Texture)RavTech.files.getAsset(texture));
		textures.put(name, texture);
	}

	public void setMatrix (String valueName, Matrix4 matrix) {
		manager.setUniformMatrix(valueName, matrix);
	}

	@Override
	public void write (Json json) {
		json.writeValue("name", this.name);
		if (this.individualShaders) {
			json.writeValue("vertexShader", this.vertexShader);
			json.writeValue("fragmentShader", this.fragmentShader);
		}
		json.writeValue("textures", textures);
	}

	@Override
	public void read (Json json, JsonValue jsonData) {
		this.name = jsonData.getString("name");
		if (jsonData.has("vertexShader")) {
			this.individualShaders = true;
			this.vertexShader = jsonData.getString("vertexShader");
			this.fragmentShader = jsonData.getString("fragmentShader");
			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run () {
					Debug.log("load", Shader.this.fragmentShader);
					Shader.this.manager.add(Shader.this.name, RavTech.files.getAssetHandle(vertexShader),
						RavTech.files.getAssetHandle(fragmentShader));
				}
			});
		}
		String textureString = jsonData.get("textures").toString();
		textureString = textureString.substring(textureString.indexOf('{'));
		this.textures = json.fromJson(ObjectMap.class, String.class, textureString);
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run () {
				Entries<String, String> entries = Shader.this.textures.iterator();
				while (entries.hasNext) {
					Entry<String, String> entry = entries.next();
					if (!entry.value.endsWith(".framebuffer") && !RavTech.files.isLoaded(entry.value)) {
						RavTech.files.loadAsset(entry.value, Texture.class, true);
						((Texture)RavTech.files.getAsset(entry.value)).setFilter(TextureFilter.Linear, TextureFilter.Linear);
					}
					Shader.this.textures.put(entry.key, entry.value);
				}
			}
		});

	}

	public void apply () {
		int textureId = 1;

		Entries<String, String> entries = Shader.this.textures.iterator();
		while (entries.hasNext) {
			Entry<String, String> entry = entries.next();

			if (!entry.value.endsWith(".framebuffer")) {
				if (!RavTech.files.isLoaded(entry.value))
					continue;

				((Texture)RavTech.files.getAsset(entry.value)).bind(textureId);
			} else {
				String trimmedName = entry.value.substring(0, entry.value.length() - ".framebuffer".length());

				if (!RavTech.sceneHandler.shaderManager.containsFB(trimmedName))
					continue;

				RavTech.sceneHandler.shaderManager.getFBTexture(trimmedName).bind(textureId);
			}
			if (RavTech.sceneHandler.shaderManager.contains(this.name))
				RavTech.sceneHandler.shaderManager.get(this.name).setUniformi(entry.key, textureId);

			textureId++;
		}
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
	}

}
