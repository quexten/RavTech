
package com.ravelsoftware.ravtech.dk.ui.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.components.GameObject;
import com.ravelsoftware.ravtech.components.SpriteRenderer;
import com.ravelsoftware.ravtech.dk.RavTechDKApplication;

public class SpritePreviewPanel extends AssetPreviewPanel {
		
	public SpritePreviewPanel (String assetPath) {
		super(assetPath);
		com.ravelsoftware.ravtech.util.Debug.log("assetPath", assetPath);
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

	@Override
	public void addToScene () {
		Vector2 worldPosition = ((RavTechDKApplication)Gdx.app.getApplicationListener()).mainSceneView.camera
			.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
		GameObject object = RavTech.currentScene.addGameObject(worldPosition.x, worldPosition.y);
		SpriteRenderer renderer = new SpriteRenderer();
		renderer.setTexture(assetPath);
		object.addComponent(renderer);
	}

}
