
package com.quexten.ravtech.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.quexten.ravtech.RavTech;

public class RavUI {

	Stage screenStage;
	public DebugConsole debugConsole;

	public RavUI () {
		if (!RavTech.isHeadless()) {
			if (!VisUI.isLoaded() && !RavTech.isHeadless())
				VisUI.load(Gdx.files.internal("mdpi/uiskin.json"));
			screenStage = new Stage(new ScreenViewport());
			debugConsole = new DebugConsole();
			RavTech.input.addInputProcessor(screenStage);
			screenStage.addListener(new InputListener() {
				@Override
				public boolean keyDown (InputEvent event, int key) {
					if (key == Keys.F1)
						debugConsole.toggleVisible();
					return true;
				}
			});
			screenStage.addActor(debugConsole);
		}
	}

	public void render () {
		screenStage.act();
		screenStage.draw();
	}

	public Stage getStage () {
		return screenStage;
	}

}
