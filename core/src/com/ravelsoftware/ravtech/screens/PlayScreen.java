
package com.ravelsoftware.ravtech.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.ravelsoftware.ravtech.RavTech;

public class PlayScreen implements Screen {

	@Override
	public void show () {
	}

	@Override
	public void render (float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		RavTech.sceneHandler.update(1f / 60f);
		RavTech.sceneHandler.render();
	}

	@Override
	public void resize (int width, int height) {
		RavTech.sceneHandler.resize(width, height);
	}

	@Override
	public void pause () {
	}

	@Override
	public void resume () {
	}

	@Override
	public void hide () {
	}

	@Override
	public void dispose () {
		RavTech.sceneHandler.dispose();
	}
}
