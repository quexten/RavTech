
package com.ravelsoftware.ravtech.dk.ui.editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.ravelsoftware.ravtech.RavTech;

public class ColorPanel extends Widget {

	Color backgroundColor;

	public ColorPanel (Color color) {
		super();
		this.backgroundColor = color;
	}

	@Override
	public void draw (Batch batch, float alpha) {
		batch.setColor(backgroundColor);
		batch.draw(RavTech.sceneHandler.renderer.ambientTexture, this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}
}
