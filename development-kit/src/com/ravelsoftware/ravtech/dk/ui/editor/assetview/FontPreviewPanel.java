package com.ravelsoftware.ravtech.dk.ui.editor.assetview;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.components.FontRenderer;
import com.ravelsoftware.ravtech.components.GameObject;
import com.ravelsoftware.ravtech.dk.RavTechDK;
import com.ravelsoftware.ravtech.dk.RavTechDKApplication;

public class FontPreviewPanel extends AssetPreviewPanel {

	public FontPreviewPanel (String assetPath) {
		super(assetPath);
		RavTech.files.loadAsset(assetPath, BitmapFont.class);
		RavTech.files.finishLoading();
		
		BitmapFont font = RavTech.files.getAsset(assetPath);
		
		VisLabel previewLabel = new VisLabel("Sample Text");
		LabelStyle labelStyle = new LabelStyle();
		labelStyle.background = previewLabel.getStyle().background;
		labelStyle.fontColor = previewLabel.getStyle().fontColor;
		labelStyle.font = font;
		previewLabel.setStyle(labelStyle);
		previewLabel.setFontScale(0.2f, 0.2f);
		VisLabel label = new VisLabel(assetPath.contains("/") ? assetPath.substring(assetPath.lastIndexOf('/') + 1) : assetPath);
		this.setSize(100, 100);
		add(previewLabel).grow().padTop(50).padBottom(50);
		row();
		add(label);
	}

	@Override
	public void addToScene () {
		Vector2 worldPosition = RavTechDK.mainSceneView.camera
			.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
		GameObject object = RavTech.currentScene.addGameObject(worldPosition.x, worldPosition.y);
		FontRenderer renderer = new FontRenderer();
		renderer.setFont(assetPath);
		object.addComponent(renderer);
	}

}
