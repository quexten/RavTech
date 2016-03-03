package com.ravelsoftware.ravtech.dk.ui.editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.kotcrab.vis.ui.widget.VisTable;
import com.ravelsoftware.ravtech.RavTech;

public class AssetPreviewPanel extends VisTable {
	
	boolean isSelected;
	
	public AssetPreviewPanel() {

	}

	public void select () {
		isSelected = true;
	}

	public void unselect () {
		isSelected = false;
	}
	
	@Override
	public void draw(Batch batch, float alpha) {
		if(isSelected) {
			batch.setColor(Color.CORAL);
			batch.draw(RavTech.sceneHandler.renderer.ambientTexture, getX(), getY(), getWidth(), getHeight());
		}
		super.draw(batch, alpha);
	}
	
}
