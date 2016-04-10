
package com.ravelsoftware.ravtech.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.kotcrab.vis.ui.VisUI;

public class RavUI {

	Stage screenStage = new Stage();
	public DebugConsole debugConsole;
	
	public RavUI () {
		if (!VisUI.isLoaded()) 
			VisUI.load(Gdx.files.internal("mdpi/uiskin.json"));
		debugConsole  = new DebugConsole();		
		Gdx.input.setInputProcessor(screenStage);
		screenStage.addListener(new InputListener() {
			@Override
			public boolean keyDown (InputEvent event, int key) {
				if(key == Keys.F1)
					debugConsole.toggleVisible();
				return false;
			}
		});
		screenStage.addActor(debugConsole);
	}

	public void render () {
		screenStage.draw();
	}

	public Stage getStage () {
		return screenStage;
	}

}
