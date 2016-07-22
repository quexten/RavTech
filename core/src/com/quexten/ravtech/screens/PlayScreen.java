
package com.quexten.ravtech.screens;

import com.badlogic.gdx.Screen;
import com.quexten.ravtech.RavTech;

public class PlayScreen implements Screen {

	@Override
	public void show () {
	}

	@Override
	public void render (float delta) {
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
