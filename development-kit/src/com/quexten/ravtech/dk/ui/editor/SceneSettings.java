
package com.quexten.ravtech.dk.ui.editor;

import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.quexten.ravtech.Scene;

public class SceneSettings extends VisWindow {

	final Scene scene;

	public SceneSettings (Scene scene) {
		super("Scene Settings");
		this.scene = scene;

		add(new VisLabel("Background Color:")).growX();
		add(new ColorPanel(scene.renderProperties.backgroundColor))
			.growX().fillY();
		row();

		add(new VisLabel("Ambient Light:")).growX();
		add(new ColorPanel(scene.renderProperties.ambientLightColor))
			.growX().fillY();
		row();

		setResizable(true);
		setSize(200, 200);
		addCloseButton();
		centerWindow();
	}

}
