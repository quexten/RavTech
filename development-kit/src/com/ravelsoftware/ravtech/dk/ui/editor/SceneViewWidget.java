
package com.ravelsoftware.ravtech.dk.ui.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.kotcrab.vis.ui.VisUI;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.graphics.Camera;
import com.ravelsoftware.ravtech.util.Debug;

public class SceneViewWidget extends Widget {

	public Camera camera;
	Vector2 dragAnchorPosition;
	boolean hasToLerpZoom;
	boolean hasToLerpPosition;
	float targetZoom;
	Vector2 targetPosition;

	Vector2 selectionStart = new Vector2();
	Vector2 selectionEnd = new Vector2();
	boolean isDragging;

	public SceneViewWidget (boolean main) {
		camera = main ? RavTech.sceneHandler.worldCamera : RavTech.sceneHandler.cameraManager.createCamera(1280, 720);
		camera.zoom = 0.05f;

		this.addListener(new ClickListener() {

			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if (button == Buttons.RIGHT) {
					Vector3 unprojectedPosition = camera.unproject(new Vector3(x, getHeight() - y, 0));
					dragAnchorPosition = new Vector2(unprojectedPosition.x, unprojectedPosition.y);
				} else if (button == Buttons.LEFT) {
					isDragging = true;
					selectionStart.set(camera.unproject(new Vector2(x, getHeight() - y)));
					selectionEnd.set(selectionStart);
				}
				return true;
			}

			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				isDragging = false;
			}

		});

		DragListener leftListener = new DragListener() {

			@Override
			public void drag (InputEvent event, float x, float y, int pointer) {
				selectionEnd.set(camera.unproject(new Vector2(x, getHeight() - y)));
			}

		};
		this.addListener(leftListener);

		DragListener rightListener = new DragListener() {

			@Override
			public void drag (InputEvent event, float x, float y, int pointer) {
				int screenCenterWidth = (int)getWidth();
				int screenCenterHeight = (int)getHeight();
				float screenDiffX = (screenCenterWidth - x) - (float)screenCenterWidth / 2f;
				float screenDiffY = (screenCenterHeight - y - (float)screenCenterHeight / 2f);
				camera.position.set(dragAnchorPosition.x + screenDiffX * camera.zoom,
					dragAnchorPosition.y + screenDiffY * camera.zoom, 0);
				camera.update();
				hasToLerpPosition = false;
			}

		};
		rightListener.setTapSquareSize(0);
		rightListener.setButton(Buttons.RIGHT);
		this.addListener(rightListener);

		InputListener scrollListener = new InputListener() {

			@Override
			public boolean scrolled (InputEvent event, float x, float y, int amount) {
				hasToLerpZoom = hasToLerpPosition = true;
				float lastzoom = camera.zoom;
				Vector2 lastposition = new Vector2(camera.position.x, camera.position.y);
				targetZoom += amount * camera.zoom * 0.5;
				if (targetZoom < 0) targetZoom = 0.0001f;
				Vector3 worldPos = camera.unproject(new Vector3(x, getHeight() - y, 0), 0, 0, camera.getResolution().x,
					camera.getResolution().y);
				targetPosition = new Vector2(worldPos.x, worldPos.y);
				targetPosition = targetPosition.add(lastposition.sub(targetPosition).scl(targetZoom / lastzoom));
				return false;
			}

		};
		this.addListener(scrollListener);

		this.addListener(new InputListener() {

			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				SceneViewWidget.this.getStage().setScrollFocus(SceneViewWidget.this);
			}

		});

		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run () {
				SceneViewWidget.this.resize();
			}

		});
	}

	public void resize () {
		if (!(getWidth() > 0 && getHeight() > 0)) return;
		Debug.log("setToOrtho", getWidth() + "|" + getHeight());
		this.camera.setToOrtho(false, getWidth(), getHeight());
		this.camera.update();
		this.camera.setResolution((int)getWidth(), (int)getHeight());
	}

	@Override
	public void draw (Batch batch, float alpha) {
		super.draw(batch, alpha);
		if (hasToLerpZoom) {
			camera.zoom += 0.16f * (targetZoom - camera.zoom);
		}
		if (hasToLerpPosition) camera.position.lerp(new Vector3(targetPosition.x, targetPosition.y, 0), 0.16f);
		if (Math.abs(targetZoom - camera.zoom) < 0.00001f) hasToLerpZoom = hasToLerpPosition = false;
		batch.setColor(Color.WHITE);
		batch.disableBlending();
		((SpriteBatch)batch).draw(camera.getCameraBufferTexture(), 0, getHeight(), getWidth(), -getHeight());
		batch.enableBlending();

		if (isDragging) {
			Vector3 selectionStartProjection = camera.project(new Vector3(selectionStart.x, selectionStart.y, 0), 0, 0, getWidth(),
				getHeight());
			Vector3 selectionEndProjection = camera.project(new Vector3(selectionEnd.x, selectionEnd.y, 0), 0, 0, getWidth(),
				getHeight());
			selectionEndProjection.sub(selectionStartProjection);
			batch.setColor(new Color(1, 1, 1, 0.5f));
			batch.draw(VisUI.getSkin().getRegion("window-bg"), selectionStartProjection.x, selectionStartProjection.y,
				selectionEndProjection.x, selectionEndProjection.y);
		}
	}

	public void setResolution (int width, int height) {
		camera.setResolution(width, height);
	}
}