
package com.quexten.ravtech.dk.ui.editor.assetview;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.components.FontRenderer;
import com.quexten.ravtech.components.GameObject;
import com.quexten.ravtech.dk.RavTechDK;

public class FontPreviewPanel extends AssetPreviewPanel {

	public FontPreviewPanel (AssetViewer viewer, String assetPath) {
		super(viewer, assetPath);
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
		setSize(100, 100);
		add(previewLabel).grow().padTop(50).padBottom(50);
		row();
		add(label);
	}

	@Override
	public void addToScene () {
		Vector2 worldPosition = viewer.inspector.view.camera.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
		GameObject object = RavTech.currentScene.addGameObject(worldPosition.x, worldPosition.y);
		FontRenderer renderer = new FontRenderer();
		renderer.setFont(assetPath);
		object.addComponent(renderer);
	}

}
