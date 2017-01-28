
package com.quexten.ravtech.graphics;

import java.util.Comparator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.components.Light;
import com.quexten.ravtech.components.Renderer;
import com.thesecretpie.shader.ShaderManager;

public class SortedRenderer {

	RendererComparator comparator = new RendererComparator();
	ShaderManager shaderManager;
	ShapeRenderer shapeRenderer = new ShapeRenderer();
	public Texture ambientTexture;
	public Color lineColor = Color.ORANGE;
	FPSLogger logger = new FPSLogger();
	
	Array<Renderer> renderers = new Array<Renderer>();
	
	public SortedRenderer (ShaderManager shaderManager) {
		this.shaderManager = shaderManager;
		Pixmap pixmap = new Pixmap(1, 1, Format.RGB565);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		ambientTexture = new Texture(pixmap);
		pixmap.dispose();
	}

	public void render (final SpriteBatch spriteBatch, final RavCamera camera) {
		logger.log();
		final ObjectMap<String, Array<Renderer>> renderingLayers = new ObjectMap<String, Array<Renderer>>();
		final Array<String> sortingLayers = RavTech.currentScene.renderProperties.sortingLayers;
		for (String str : sortingLayers) {
			if (camera.layers.contains(str, false)) {
				renderingLayers.put(str, new Array<Renderer>());
			}
		}
		
		for(int n = 0; n < renderers.size; n++) {
			Renderer renderer = renderers.get(n);
			if(renderer.enabled) {
				renderingLayers.get(renderer.sortingLayerName).add(renderer);
				if(renderer instanceof Light)
					((Light) renderer).getLight().setActive(renderer.enabled);
			}
		}
		
		
		for (int i = 0; i < RavTech.currentScene.renderProperties.sortingLayers.size; i++) {
			if(RavTech.currentScene.renderProperties.sortingLayers.get(i).equals(RenderProperties.LAYER_LIGHTS)) {
				break;
			}				
		}
		
		String activeShader = null;
		
		spriteBatch.begin();		
		for (int i = 0; i < RavTech.currentScene.renderProperties.sortingLayers.size; i++) {
			//String sortingLayer = sortingLayers.get(i);
			
			for (final Renderer renderer : renderers) {
				if(activeShader == null || !activeShader.equals(renderer.shader.name)) {
					activeShader = renderer.shader.name;
					renderer.shader.apply();
				}
				if(spriteBatch.getBlendSrcFunc() != renderer.srcBlendFunction || spriteBatch.getBlendDstFunc() != renderer.dstBlendFunction)
					spriteBatch.setBlendFunction(renderer.srcBlendFunction, renderer.dstBlendFunction);
				renderer.draw(spriteBatch);
			}
		}
		spriteBatch.end();		
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

	public void register (Renderer renderer) {
		this.renderers.add(renderer);
	}

	public void unregister (Renderer renderer) {
		this.renderers.removeValue(renderer, true);
	}
}
