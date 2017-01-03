
package com.quexten.ravtech.dk.ui.editor;

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
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.components.Camera;
import com.quexten.ravtech.components.GameObject;
import com.quexten.ravtech.components.SpriterAnimator;
import com.quexten.ravtech.components.Transform;
import com.quexten.ravtech.components.gizmos.Gizmo;
import com.quexten.ravtech.components.gizmos.GizmoHandler;
import com.quexten.ravtech.components.gizmos.TransformGizmo;
import com.quexten.ravtech.dk.RavTechDK;
import com.quexten.ravtech.dk.actions.CopyAction;
import com.quexten.ravtech.dk.actions.DeleteAction;
import com.quexten.ravtech.dk.actions.PasteAction;
import com.quexten.ravtech.dk.ui.editor.SceneViewWidget.EditingMode;
import com.quexten.ravtech.graphics.RavCamera;
import com.quexten.ravtech.remoteedit.FileHasher;
import com.quexten.ravtech.util.Debug;
import com.quexten.ravtech.util.EventType;
import com.quexten.ravtech.util.PrefabManager;
import com.quexten.ravtech.util.ZipUtil;

public class SceneViewWidget extends Widget {

	//Editor Cameras have their own id's that are negative and keep decreasing
	//as more cameras get added
	int camId = -1;
	
	public RavCamera camera;
	Vector2 dragAnchorPosition;
	boolean hasToLerpZoom;
	boolean hasToLerpPosition;
	float targetZoom;
	Vector2 targetPosition;

	Vector2 selectionStart = new Vector2();
	Vector2 selectionEnd = new Vector2();
	boolean isDragging;

	boolean renderGrid;

	int oldWidth, oldHeight;

	public enum EditingMode {
		Move, Rotate, Scale, Other
	};

	private EditingMode currentEditingMode = EditingMode.Move;
	GizmoHandler gizmoHandler;
	public Inspector inspector = new Inspector();
	
	public Array<GameObject> selectedObjects = new Array<GameObject>();
	
	public SceneViewWidget (boolean main) {
		if (!main) {
			camera = RavTech.sceneHandler.cameraManager.createCamera(camId--, 1280, 720);
			camera.zoom = 0.05f;
		} else {
			camera = new RavCamera(camId--, 1280, 720) {
				@Override
				public void render (SpriteBatch spriteBatch) {
					super.render(spriteBatch);
					this.getCameraBuffer().begin();
					gizmoHandler.render();
					this.getCameraBuffer().end();
				}
			};
			camera.zoom = 0.05f;
			RavTech.sceneHandler.cameraManager.cameras.add(camera);
		}

		addListener(new InputListener() {
			public boolean mouseMoved (InputEvent event, float x, float y) {
				Vector2 unprojectedPosition = camera.unproject(new Vector2(x, getHeight() - y));
				gizmoHandler.input(unprojectedPosition.x, unprojectedPosition.y, 0, EventType.MouseMoved);
				return false;
			}
		});

		addListener(new ClickListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				Vector2 unprojectedPosition = camera.unproject(new Vector2(x, getHeight() - y));
				gizmoHandler.input(unprojectedPosition.x, unprojectedPosition.y, button, EventType.MouseDown);
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
				if (!gizmoHandler.input(unprojectedPosition.x, unprojectedPosition.y, button, EventType.MouseUp))
					inspector.changed();
			}
		});

		DragListener leftListener = new DragListener() {

			@Override
			public void drag (InputEvent event, float x, float y, int pointer) {
				Vector2 unprojectedPosition = camera.unproject(new Vector2(x, getHeight() - y));
				if (gizmoHandler.input(unprojectedPosition.x, unprojectedPosition.y, 0, EventType.MouseDrag))
					return;
				selectionEnd.set(camera.unproject(new Vector2(x, getHeight() - y)));
				setSelectedObjects(
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
					new CopyAction(SceneViewWidget.this).run();
				if (RavTech.input.isKeyPressed(Keys.CONTROL_LEFT) && keycode == Keys.V)
					new PasteAction().run();

				if (keycode == Keys.FORWARD_DEL)
					new DeleteAction(selectedObjects).run();

				if (keycode == Keys.F5) {
					Array<String> assetNames = RavTech.files.getAssetManager().getAssetNames();
					for (int i = 0; i < assetNames.size; i++)
						RavTech.files.reloadAsset(assetNames.get(i));
				}
				if (keycode == Keys.Q) {
					setEditingMode(EditingMode.Other);
				}
				if (keycode == Keys.W) {
					setEditingMode(EditingMode.Move);
				}
				if (keycode == Keys.E) {
					setEditingMode(EditingMode.Rotate);
				}
				if (keycode == Keys.R) {
					setEditingMode(EditingMode.Scale);
				}
				
				if (keycode == Keys.F2) {
					System.out.println(PrefabManager.makePrefab(selectedObjects.first()));
				}
				return true;
			}
		});

		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run () {
				SceneViewWidget.this.resize();
				camera.setClearColor(RavTech.currentScene.renderProperties.backgroundColor);
			}

		});
		
		gizmoHandler = new GizmoHandler(this);	
	}

	public void setEditingMode (EditingMode editingMode) {
		this.currentEditingMode = editingMode;
		Array<Gizmo<Transform>> gizmos = this.gizmoHandler.getGizmosOfType(Transform.class);
		for(Gizmo<Transform> gizmo : gizmos) {
			TransformGizmo transformGizmo = ((TransformGizmo) gizmo);
			transformGizmo.setEditingMode(editingMode);
		}
	}

	public void resize () {
		if (!(getWidth() > 0 && getHeight() > 0))
			return;
		camera.setToOrtho(false, getWidth(), getHeight());
		camera.update();
		camera.setResolution((int)getWidth(), (int)getHeight());
	}

	@Override
	public void act (float delta) {
		if (hasToLerpZoom)
			camera.zoom += 5 * delta * (targetZoom - camera.zoom);
		if (hasToLerpPosition)
			camera.position.lerp(new Vector3(targetPosition.x, targetPosition.y, 0), 5 * delta);
		if (Math.abs(targetZoom - camera.zoom) < 0.00001f)
			hasToLerpZoom = hasToLerpPosition = false;
	}

	@Override
	public void draw (Batch batch, float alpha) {
		super.draw(batch, alpha);

		if (oldWidth != (int)getWidth() || oldHeight != (int)getHeight()) {
			resize();
		}
		oldWidth = (int)getWidth();
		oldHeight = (int)getHeight();

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
			batch.draw(VisUI.getSkin().getRegion("white"), selectionStartProjection.x, selectionStartProjection.y,
				selectionEndProjection.x, selectionEndProjection.y);
		}
	}

	public void setResolution (int width, int height) {
		camera.setResolution(width, height);
	}

	public EditingMode getEditingMode () {
		return this.currentEditingMode;
	}
	
	public void setSelectedObjects(Array<GameObject> objects) {
		selectedObjects.clear();
		selectedObjects.addAll(objects);
	}

	public void setSelectedObjects (GameObject object) {
		selectedObjects.clear();
		selectedObjects.add(object);
	}
	
}
