
package com.quexten.ravtech.dk.ui.editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerListener;
import com.quexten.ravtech.RavTech;

public class ColorPanel extends Widget {

	final Color backgroundColor;

	public ColorPanel (Color color) {
		super();
		backgroundColor = color;
		addListener(new ClickListener() {
			public void clicked (InputEvent event, float x, float y) {
				final ColorPicker picker = new ColorPicker();
				picker.setListener(new ColorPickerListener() {
					@Override
					public void canceled (Color oldColor) {
						backgroundColor.set(oldColor);
					}

					@Override
					public void changed (Color newColor) {
						backgroundColor.set(newColor);
					}

					@Override
					public void reset (Color previousColor, Color newColor) {
					}

					@Override
					public void finished (Color newColor) {
						picker.dispose();
					}
				});
				ColorPanel.this.getStage().addActor(picker);
			}
		});
	}

	@Override
	public float getMinWidth () {
		return 50;
	}

	@Override
	public void draw (Batch batch, float alpha) {
		batch.setColor(backgroundColor);
		batch.draw(RavTech.sceneHandler.renderer.ambientTexture, this.getX(), this.getY(), getWidth(), getHeight());
	}
}
