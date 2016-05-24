
package com.quexten.ravtech.dk.ui.editor.assetview;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.kotcrab.vis.ui.widget.VisTable;
import com.quexten.ravtech.RavTech;

public abstract class AssetPreviewPanel extends VisTable {

	String assetPath;
	boolean isSelected;
	Color selectionColor = Color.CORAL;

	public AssetPreviewPanel (String assetPath) {
		this.assetPath = assetPath;
	}

	public void select () {
		isSelected = true;
	}

	public void unselect () {
		isSelected = false;
	}

	public void setSelectionColor (Color color) {
		selectionColor = color;
	}

	@Override
	public void draw (Batch batch, float alpha) {
		if (isSelected) {
			batch.setColor(selectionColor);
			batch.draw(RavTech.sceneHandler.renderer.ambientTexture, getX(), getY(), getWidth(), getHeight());
		}
		super.draw(batch, alpha);
	}

	public abstract void addToScene ();

}
