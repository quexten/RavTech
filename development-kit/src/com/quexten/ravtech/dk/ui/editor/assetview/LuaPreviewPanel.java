
package com.quexten.ravtech.dk.ui.editor.assetview;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.quexten.ravtech.dk.RavTechDK;

public class LuaPreviewPanel extends AssetPreviewPanel {

	final static String iconPath = "ui/icons/lua-file.png";

	public LuaPreviewPanel (String fileName) {
		super(fileName);
		RavTechDK.editorAssetManager.load(iconPath, Texture.class);
		RavTechDK.editorAssetManager.finishLoading();
		Image image = new Image(
			(Texture)RavTechDK.editorAssetManager.get(iconPath));
		image.getDrawable().setMinHeight(100);
		image.getDrawable().setMinWidth(100);

		VisLabel label = new VisLabel(assetPath.contains("/")
			? assetPath.substring(assetPath.lastIndexOf('/') + 1)
			: assetPath);
		add(image).padTop(8).padBottom(7).grow();
		row();
		add(label);
	}

	@Override
	public void addToScene () {
	}

}
