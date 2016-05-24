
package com.quexten.ravtech.dk.ui.editor.assetview;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.quexten.ravtech.dk.RavTechDK;

public class FolderPreviewPanel extends AssetPreviewPanel {

	final static String folderIconPath = "ui/icons/folder.png";

	public FolderPreviewPanel (String folderName) {
		super(folderName);
		RavTechDK.editorAssetManager.load(folderIconPath, Texture.class);
		RavTechDK.editorAssetManager.finishLoading();
		Image image = new Image((Texture)RavTechDK.editorAssetManager.get(folderIconPath));
		image.getDrawable().setMinHeight(85);
		image.getDrawable().setMinWidth(100);

		folderName = folderName.substring(0, 8 < folderName.length() ? 8 : folderName.length());

		VisLabel label = new VisLabel(folderName);
		add(image).padTop(8).padBottom(7).grow();
		row();
		add(label);
	}

	@Override
	public void addToScene () {
	}

}
