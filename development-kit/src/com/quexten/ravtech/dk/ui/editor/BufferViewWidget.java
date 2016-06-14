package com.quexten.ravtech.dk.ui.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.dk.RavTechDK;

public class BufferViewWidget extends Widget {

	FrameBuffer buffer;
	ShaderProgram shader;
	boolean renderAlphaMap;
	
	public BufferViewWidget (String buffer) {
		setBuffer(buffer);
		shader = new ShaderProgram(RavTechDK.getLocalFile("resources/shaders/default.vert"), RavTechDK.getLocalFile("resources/shaders/alpha.frag"));
	}
	
	public void setBuffer(String buffer) {
		this.buffer = RavTech.sceneHandler.shaderManager.getFB(buffer);
	}
	
	@Override
	public void draw(Batch batch, float alpha) {
		super.draw(batch, alpha);
		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);
		batch.setColor(Color.WHITE);
		batch.disableBlending();
		batch.setShader(renderAlphaMap ? shader : null);
		((SpriteBatch)batch).draw(buffer.getColorBufferTexture(), 0, getHeight(), getWidth(), -getHeight());
		batch.setShader(null);
		batch.enableBlending();
	}	

}
