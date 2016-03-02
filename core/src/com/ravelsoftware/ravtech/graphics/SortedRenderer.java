
package com.ravelsoftware.ravtech.graphics;

import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.ravelsoftware.ravtech.HookApi;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.components.ComponentType;
import com.ravelsoftware.ravtech.components.GameComponent;
import com.ravelsoftware.ravtech.components.GameObject;
import com.ravelsoftware.ravtech.components.Light;
import com.ravelsoftware.ravtech.components.Renderer;
import com.thesecretpie.shader.ShaderManager;

public class SortedRenderer {

	RendererComparator comparator = new RendererComparator();
	ShaderManager shaderManager;
	public Texture ambientTexture;
	public Color lineColor = Color.ORANGE;

	public SortedRenderer (ShaderManager shaderManager) {
		this.shaderManager = shaderManager;
		Pixmap pixmap = new Pixmap(1, 1, Format.RGB565);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		ambientTexture = new Texture(pixmap);
		pixmap.dispose();
	}

	public void render (final SpriteBatch spriteBatch, final Camera camera) {
		final ObjectMap<String, Array<Renderer>> renderingLayers = new ObjectMap<String, Array<Renderer>>();
		final Array<SortingLayer> sortingLayers = RavTech.currentScene.renderProperties.sortingLayers;
		for (SortingLayer str : sortingLayers)
			renderingLayers.put(str.name, new Array<Renderer>());
		for (int n = 0; n < RavTech.currentScene.gameObjects.size; n++) {
			GameObject object = RavTech.currentScene.gameObjects.get(n);
			Array<GameComponent> components = object.getComponentsInChildren(ComponentType.SpriteRenderer, ComponentType.Light);
			components.reverse();
			for (int i = 0; i < components.size; i++) {
				Renderer renderer = (Renderer)components.get(i);
				if (renderer instanceof Light) ((Light)renderer).light.setActive(renderer.enabled);
				if (renderingLayers.get(renderer.sortingLayerName) != null && renderer.enabled)
					renderingLayers.get(renderer.sortingLayerName).add(renderer);
			}
		}
		Array<Renderable> renderables = new Array<Renderable>();
		for (int i = 0; i < RavTech.currentScene.renderProperties.sortingLayers.size; i++) {
			renderingLayers.get(sortingLayers.get(i).name).sort(comparator);
			Array<Renderable> currentRenderables = new Array<Renderable>();
			if (RavTech.settings.getBoolean("renderDebug"))
				if (sortingLayers.get(i).name.equals("Default")) currentRenderables.add(new Renderable("default") {

					@Override
					public void render () {
						ShapeRenderer shapeRenderer = RavTech.shapeRenderer;
						Camera worldCamera = camera;
						float camWidth = worldCamera.viewportWidth / (1.0f / worldCamera.zoom);
						float camHeight = worldCamera.viewportHeight / (1.0f / worldCamera.zoom);
						float times = worldCamera.zoom < 0.01f ? 0.1f : worldCamera.zoom > 0.2 ? 10 : 1;
						shapeRenderer.setProjectionMatrix(worldCamera.combined);
						shapeRenderer.setColor(Color.LIGHT_GRAY);
						shapeRenderer.end();
						shapeRenderer.begin(ShapeType.Line);
						Color redColor = lineColor;
						Color greenColor = new Color(0.66f, 0.66f, 0.66f, 1f);
						Color blueColor = new Color(0.33f, 0.33f, 0.33f, 1f);
						if (times < 0.5f) {
							for (float w = (int)(worldCamera.position.x - camWidth / 2) - 1; w < worldCamera.position.x
								+ camWidth / 2; w += 0.1f) {
								shapeRenderer.setColor(greenColor);
								shapeRenderer.line(w, worldCamera.position.y + camHeight / 2, w, worldCamera.position.y - camHeight / 2);
							}
							for (float h = (int)(worldCamera.position.y - camHeight / 2) - 1; h < worldCamera.position.y
								+ camHeight / 2; h += 0.1f) {
								shapeRenderer.setColor(greenColor);
								shapeRenderer.line(worldCamera.position.x + camWidth / 2, h, worldCamera.position.x - camWidth / 2, h);
							}
						}
						for (float w = (int)(worldCamera.position.x - camWidth / 2) - 1; w < worldCamera.position.x
							+ camWidth / 2; w++) {
							shapeRenderer.setColor(Math.abs(w) % 10 < 0.01f ? redColor : blueColor);
							shapeRenderer.line(w, worldCamera.position.y + camHeight / 2, w, worldCamera.position.y - camHeight / 2);
						}
						for (float h = (int)(worldCamera.position.y - camHeight / 2) - 1; h < worldCamera.position.y
							+ camHeight / 2; h++) {
							shapeRenderer.setColor(Math.abs(h) % 10 < 0.01f ? redColor : blueColor);
							shapeRenderer.line(worldCamera.position.x + camWidth / 2, h, worldCamera.position.x - camWidth / 2, h);
						}
						shapeRenderer.end();
					}
				});
			for (final Renderer renderer : renderingLayers.get(sortingLayers.get(i).name))
				currentRenderables.add(new Renderable("default") {

					@Override
					public void render () {
						spriteBatch.begin();
						renderer.draw(spriteBatch);
						spriteBatch.end();
					}
				});
			if (sortingLayers.get(i).name.equals("Default"))
				if (RavTech.settings.getBoolean("useLights")) currentRenderables.add(new Renderable("default") {

					@Override
					public void render () {
						spriteBatch.begin();
						Matrix4 matrix = new Matrix4();
						matrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
						spriteBatch.setProjectionMatrix(matrix);
						spriteBatch.setColor(new Color(RavTech.currentScene.renderProperties.ambientLightColor));
						spriteBatch.draw(SortedRenderer.this.ambientTexture, 0, Gdx.graphics.getHeight(), Gdx.graphics.getWidth(),
							-Gdx.graphics.getHeight());
						spriteBatch.setColor(Color.WHITE);
						spriteBatch.end();
						int srcfn = spriteBatch.getBlendSrcFunc();
						int dstfn = spriteBatch.getBlendDstFunc();
						spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
						spriteBatch.begin();
						spriteBatch.draw(RavTech.sceneHandler.lightHandler.getLightMapTexture(), 0, Gdx.graphics.getHeight(),
							Gdx.graphics.getWidth(), -Gdx.graphics.getHeight());
						spriteBatch.end();
						spriteBatch.setBlendFunction(srcfn, dstfn);
						spriteBatch.setProjectionMatrix(camera.combined);
					}
				});
			renderables.addAll(currentRenderables);
		}
		if (camera.cameraBuffer != null) {
			camera.cameraBuffer.begin();
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			renderRunnables(renderables);
			for (int i = 0; i < HookApi.onRenderHooks.size; i++) {
				HookApi.onRenderHooks.get(i).run();
			}
			camera.cameraBuffer.end();
		} else {
			renderRunnables(renderables);

			for (int i = 0; i < HookApi.onRenderHooks.size; i++) {
				HookApi.onRenderHooks.get(i).run();
			}
		}
	}

	public void renderRunnables (Array<Renderable> renderables) {
		if (renderables.size > 0) {
			shaderManager.begin(renderables.get(0).shaderName);
			for (Renderable renderable : renderables) {
				if (shaderManager.currentShaderIdn != null && !shaderManager.currentShaderIdn.equals(renderable.shaderName)) {
					shaderManager.end();
					shaderManager.begin(renderable.shaderName);
				}
				renderable.render();
			}
			shaderManager.end();
		}
		renderables.clear();
	}

	public class RendererComparator implements Comparator<Renderer> {

		@Override
		public int compare (Renderer renderer1, Renderer renderer2) {
			return renderer1.sortingOrder - renderer2.sortingOrder > 0 ? 1 : -1;
		}
	}
}
