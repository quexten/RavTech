
package com.ravelsoftware.ravtech.dk.ui.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
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
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.dk.RavTechDK;
import com.ravelsoftware.ravtech.dk.RavTechDK.EditingMode;
import com.ravelsoftware.ravtech.dk.actions.CopyAction;
import com.ravelsoftware.ravtech.dk.actions.DeleteAction;
import com.ravelsoftware.ravtech.dk.actions.PasteAction;
import com.ravelsoftware.ravtech.graphics.Camera;
import com.ravelsoftware.ravtech.util.Debug;
import com.ravelsoftware.ravtech.util.EventType;

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

	boolean renderGrid;

	public SceneViewWidget (boolean main) {
		camera = main ? RavTech.sceneHandler.worldCamera : RavTech.sceneHandler.cameraManager.createCamera(1280, 720);
		camera.zoom = 0.05f;

		addListener(new InputListener() {
			public boolean mouseMoved (InputEvent event, float x, float y) {
				Vector2 unprojectedPosition = camera.unproject(new Vector2(x, getHeight() - y));
				RavTechDK.gizmoHandler.input(unprojectedPosition.x, unprojectedPosition.y, 0, EventType.MouseMoved);
				return false;
			}
		});

		addListener(new ClickListener() {

			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				Vector2 unprojectedPosition = camera.unproject(new Vector2(x, getHeight() - y));
				RavTechDK.gizmoHandler.input(unprojectedPosition.x, unprojectedPosition.y, button, EventType.MouseDown);
				if (button == Buttons.RIGHT)
					dragAnchorPosition = new Vector2(unprojectedPosition.x, unprojectedPosition.y);
				else if (button == Buttons.LEFT) {
					isDragging = true;
					selectionStart.set(camera.unproject(new Vector2(x, getHeight() - y)));
					selectionEnd.set(selectionStart);
				}
				return true;
			}

			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				Vector2 unprojectedPosition = camera.unproject(new Vector2(x, getHeight() - y));
				isDragging = false;
				if (!RavTechDK.gizmoHandler.input(unprojectedPosition.x, unprojectedPosition.y, button, EventType.MouseUp))
					RavTechDK.inspector.changed();
			}
		});

		DragListener leftListener = new DragListener() {

			@Override
			public void drag (InputEvent event, float x, float y, int pointer) {
				Vector2 unprojectedPosition = camera.unproject(new Vector2(x, getHeight() - y));
				if (RavTechDK.gizmoHandler.input(unprojectedPosition.x, unprojectedPosition.y, 0, EventType.MouseDrag))
					return;
				selectionEnd.set(camera.unproject(new Vector2(x, getHeight() - y)));
				RavTechDK.setSelectedObjects(
					RavTech.currentScene.getGameObjectsIn(selectionStart.x, selectionStart.y, selectionEnd.x, selectionEnd.y));
			}

		};
		addListener(leftListener);
		leftListener.setTapSquareSize(0);

		DragListener rightListener = new DragListener() {

			@Override
			public void drag (InputEvent event, float x, float y, int pointer) {
				int screenCenterWidth = (int)getWidth();
				int screenCenterHeight = (int)getHeight();
				float screenDiffX = screenCenterWidth - x - screenCenterWidth / 2f;
				float screenDiffY = screenCenterHeight - y - screenCenterHeight / 2f;
				camera.position.set(dragAnchorPosition.x + screenDiffX * camera.zoom,
					dragAnchorPosition.y + screenDiffY * camera.zoom, 0);
				camera.update();
				hasToLerpPosition = false;
			}

		};
		rightListener.setTapSquareSize(0);
		rightListener.setButton(Buttons.RIGHT);
		addListener(rightListener);

		InputListener scrollListener = new InputListener() {

			@Override
			public boolean scrolled (InputEvent event, float x, float y, int amount) {
				hasToLerpZoom = hasToLerpPosition = true;
				float lastzoom = camera.zoom;
				Vector2 lastposition = new Vector2(camera.position.x, camera.position.y);
				targetZoom += amount * camera.zoom * 0.5;
				targetZoom = Math.min(Math.max(0.0001f, targetZoom), 1);
				Vector3 worldPos = camera.unproject(new Vector3(x, getHeight() - y, 0), 0, 0, camera.getResolution().x,
					camera.getResolution().y);
				targetPosition = new Vector2(worldPos.x, worldPos.y);
				targetPosition = targetPosition.add(lastposition.sub(targetPosition).scl(targetZoom / lastzoom));
				return false;
			}

		};
		addListener(scrollListener);

		addListener(new InputListener() {

			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				SceneViewWidget.this.getStage().setScrollFocus(SceneViewWidget.this);
				SceneViewWidget.this.getStage().setKeyboardFocus(SceneViewWidget.this);
			}

		});
		addListener(new InputListener() {
			public boolean keyDown (InputEvent event, int keycode) {
				if (RavTech.input.isKeyPressed(Keys.CONTROL_LEFT) && keycode == Keys.C)
					new CopyAction().run();
				if (RavTech.input.isKeyPressed(Keys.CONTROL_LEFT) && keycode == Keys.V)
					new PasteAction().run();

				if (keycode == Keys.FORWARD_DEL)
					new DeleteAction().run();

				if (keycode == Keys.F5) {
					Array<String> assetNames = RavTech.files.getAssetManager().getAssetNames();
					for (int i = 0; i < assetNames.size; i++)
						RavTech.files.reloadAsset(assetNames.get(i));
				}
				if (keycode == Keys.W) {
					RavTechDK.setEditingMode(EditingMode.Move);
				}
				if (keycode == Keys.E) {
					RavTechDK.setEditingMode(EditingMode.Rotate);
				}

				return false;
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
		if (!(getWidth() > 0 && getHeight() > 0))
			return;
		Debug.logDebug("setToOrtho", getWidth() + "|" + getHeight());
		camera.setToOrtho(false, getWidth(), getHeight());
		camera.update();
		camera.setResolution((int)getWidth(), (int)getHeight());
	}

	@Override
	public void act (float delta) {
		if (hasToLerpZoom)
			camera.zoom += 0.16f * (targetZoom - camera.zoom);
		if (hasToLerpPosition)
			camera.position.lerp(new Vector3(targetPosition.x, targetPosition.y, 0), 0.16f);
		if (Math.abs(targetZoom - camera.zoom) < 0.00001f)
			hasToLerpZoom = hasToLerpPosition = false;
	}

	@Override
	public void draw (Batch batch, float alpha) {
		super.draw(batch, alpha);

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
