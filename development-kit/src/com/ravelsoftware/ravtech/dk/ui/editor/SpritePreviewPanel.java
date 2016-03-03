
package com.ravelsoftware.ravtech.dk.ui.editor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.ravelsoftware.ravtech.RavTech;

public class SpritePreviewPanel extends AssetPreviewPanel {

	public SpritePreviewPanel (String assetPath) {
		RavTech.files.loadAsset(assetPath, Texture.class);
		RavTech.files.finishLoading();
		Image image = new Image((Texture)RavTech.files.getAsset(assetPath));
		image.getDrawable().setMinHeight(100);
		image.getDrawable().setMinWidth(100);
		VisLabel label = new VisLabel(assetPath.contains("/") ? assetPath.substring(assetPath.lastIndexOf('/')) : assetPath);
		add(image).grow();
		row();
		add(label);
	}

}
