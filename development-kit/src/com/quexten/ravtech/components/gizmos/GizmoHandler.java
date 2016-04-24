
package com.quexten.ravtech.components.gizmos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.DelaunayTriangulator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.ObjectMap.Values;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.components.BoxCollider;
import com.quexten.ravtech.components.CircleCollider;
import com.quexten.ravtech.components.GameComponent;
import com.quexten.ravtech.components.GameObject;
import com.quexten.ravtech.components.Light;
import com.quexten.ravtech.components.PolygonCollider;
import com.quexten.ravtech.components.SpriteRenderer;
import com.quexten.ravtech.components.Transform;
import com.quexten.ravtech.dk.RavTechDK;
import com.quexten.ravtech.dk.RavTechDK.EditingMode;
import com.quexten.ravtech.graphics.Camera;
import com.quexten.ravtech.util.EventType;

public class GizmoHandler {

	ObjectMap<GameComponent, Gizmo<? extends GameComponent>> selectedObjectGizmoMap = new ObjectMap<GameComponent, Gizmo<? extends GameComponent>>();
	Gizmo<? extends GameComponent> draggedGizmo;
	Gizmo<? extends GameComponent> closestGizmo;
	Gizmo<? extends GameComponent> exclusiveGizmo;

	Camera camera;
	PolygonShapeRenderer renderer;
	static final DelaunayTriangulator triangulator = new DelaunayTriangulator();
	public static final Texture whiteTexture = new Texture(
		RavTechDK.getLocalFile("resources/ui/icons/white.png"));

	public GizmoHandler (Camera camera) {
		this.camera = camera;
		this.renderer = new PolygonShapeRenderer(camera);
	}

	/** Renders the currently active gizmos
	 * @param renderer - the shaperenderer to render the gizmos with */
	public void render () {
		renderer.begin();
		if (exclusiveGizmo == null) {
			Array<GameObject> objects = RavTechDK.selectedObjects;
			for (int i = 0; i < RavTechDK.selectedObjects.size; i++) {
				// objects.addAll(RavTechDK.selectedObjects.get(i));
			}

			for (int i = 0; i < objects.size; i++) {
				GameObject object = objects.get(i);
				if (object != null)
					for (int n = 0; n < object.getComponents().size; n++) {
						Gizmo<? extends GameComponent> gizmo = getGizmoFor(
							object.getComponents().get(n));						
						if (gizmo != null && (RavTechDK.getEditingMode() == EditingMode.Other) ? true : gizmo instanceof TransformGizmo)
							gizmo.draw(renderer, gizmo == closestGizmo);
					}
			}
		} else
			exclusiveGizmo.draw(renderer,
				exclusiveGizmo == closestGizmo);
		renderer.end();
	}

	/** Inputs the given input to all gizmos and returns whether the input has been consumed
	 * @param x - the x position in world coordinate space
	 * @param y - the y position in world coordinate space
	 * @param button - the button that the mouse action is performed with
	 * @param eventType - the type of mouse event performed
	 * @return - Whether the event has been consumed */
	public boolean input (float x, float y, int button,
		int eventType) {
		switch (eventType) {
			case EventType.MouseMoved:
				if (exclusiveGizmo == null) {
					Values<Gizmo<? extends GameComponent>> values = selectedObjectGizmoMap.values();
					Gizmo<? extends GameComponent> closestGizmo = null;
					float closestDst = Float.MAX_VALUE;
					while (values.hasNext) {
						Gizmo<? extends GameComponent> giz = values.next();
						float gizDst = giz.input(x, y, 0,
							EventType.MouseMoved);
						if(!((RavTechDK.getEditingMode() == EditingMode.Other) ? true : giz instanceof TransformGizmo))
							continue;
						if (gizDst > 0 && gizDst < closestDst
							&& Math.abs(gizDst - closestDst) > 0.1f * 1
								/ 0.05f
								* RavTech.sceneHandler.worldCamera.zoom
							&& !giz.isExclusive) {
							closestDst = gizDst;
							closestGizmo = giz;
						}
					}
					this.closestGizmo = closestGizmo;
				} else
					closestGizmo = exclusiveGizmo.input(x, y, 0,
						EventType.MouseMoved) > 0f ? exclusiveGizmo : null;
				return closestGizmo != null;
			case EventType.MouseDown:
				draggedGizmo = closestGizmo;
				if (draggedGizmo != null)
					draggedGizmo.input(x, y, 0, EventType.MouseDown);
				else {
					Transform transform = getTransformAtPoint(
						RavTech.currentScene.gameObjects);
					if (transform != null) {
						Array<GameObject> objects = new Array<GameObject>();
						objects.add(transform.getParent());
						RavTechDK.setSelectedObjects(objects);
						if (button == Buttons.LEFT) {
							draggedGizmo = getGizmoFor(transform);
							((TransformGizmo) draggedGizmo).selectedAxis = TransformGizmo.AXIS_XY;
							RavTechDK.setEditingMode(EditingMode.Move);
							draggedGizmo.input(x, y, 0, EventType.MouseDown);
						} else {
						}
					} else {
						selectedObjectGizmoMap.clear();
						RavTechDK.selectedObjects.clear();
						setExclusiveGizmo(null);
					}
				}
				return true;
			case EventType.MouseUp:
				if (draggedGizmo != null) {
					closestGizmo = null;
					draggedGizmo.input(x, y, 0, EventType.MouseUp);
					draggedGizmo = null;
					return true;
				}
				return false;
			case EventType.MouseDrag:
				if (draggedGizmo != null && (RavTechDK.getEditingMode() == EditingMode.Other) ? true : draggedGizmo instanceof TransformGizmo)
					draggedGizmo.input(x, y, button, EventType.MouseDrag);
				return draggedGizmo != null;
		}
		return false;
	}

	/** Gets the gizmo for a specified component in the list of currently selected GameObjects */
	public Gizmo<? extends GameComponent> getGizmoFor (GameComponent component) {
		Entries<GameComponent, Gizmo<? extends GameComponent>> iterator = selectedObjectGizmoMap
			.iterator();
		while (iterator.hasNext()) {
			Entry<GameComponent, Gizmo<? extends GameComponent>> entry = iterator.next();
			if (entry.key == component)
				return entry.value;
		}
		return null;
	}

	/** Sets up gizmos for the currently selected list of objects */
	public void setupGizmos () {
		selectedObjectGizmoMap.clear();
		Array<GameObject> objects = RavTechDK.selectedObjects;
		for (int i = 0; i < RavTechDK.selectedObjects.size; i++)
			objects.addAll(RavTechDK.selectedObjects.get(i)
				.getGameObjectsInChildren());

		for (int i = 0; i < objects.size; i++) {
			GameObject selectedObject = objects.get(i);
			if (selectedObject != null)
				for (int n = 0; n < selectedObject
					.getComponents().size; n++) {
					GameComponent iteratedComponent = selectedObject
						.getComponents().get(n);
					Gizmo<? extends GameComponent> gizmo = null;
					gizmo = createGizmoFor(iteratedComponent);
					if (gizmo != null)
						selectedObjectGizmoMap.put(iteratedComponent,
							gizmo);
				}
		}
	}

	/** Creates gizmo for a specified component
	 * @param component - the component to create a gizmo for
	 * @return Returns the created gizmo */
	public Gizmo<? extends GameComponent> createGizmoFor (GameComponent component) {
		Class<? extends GameComponent> iteratedComponentClass = component
			.getClass();
		Gizmo<? extends GameComponent> gizmo = null;
		if (iteratedComponentClass.equals(Transform.class))
			gizmo = new TransformGizmo((Transform)component);
		else if (iteratedComponentClass.equals(BoxCollider.class))
			gizmo = new BoxColliderGizmo((BoxCollider)component);
		else if (iteratedComponentClass.equals(CircleCollider.class))
			gizmo = new CircleColliderGizmo((CircleCollider)component);
		else if (iteratedComponentClass.equals(Light.class))
			gizmo = new ConeLightGizmo((Light)component);
		else if (iteratedComponentClass.equals(PolygonCollider.class))
			gizmo = new PolygonColliderGizmo((PolygonCollider)component);
		else if (iteratedComponentClass.equals(SpriteRenderer.class))
			gizmo = new SpriteRendererGizmo((SpriteRenderer)component);
		return gizmo;
	}

	/** Sets exclusivity of input and rendering for a specified component, unset if the specified component is null
	 * @param component - the component to grant exclusivity to */
	public void setExclusiveGizmo (GameComponent component) {
		if (component != null)
			exclusiveGizmo = getGizmoFor(component);
		else
			exclusiveGizmo = null;
	}

	public static Transform getTransformAtPoint (
		Array<? extends GameComponent> objects) {
		Transform transform = null;
		for (int i = 0; i < objects.size; i++)
			if (objects.get(i) instanceof GameObject) {
				Transform localTransform = getTransformAtPoint(
					((GameObject)objects.get(i)).getComponents());
				if (localTransform != null) {
					transform = localTransform;
					break;
				}
			} else {
				Gizmo<? extends GameComponent> gizmo = RavTechDK.gizmoHandler
					.createGizmoFor(objects.get(i));
				if (gizmo != null) {
					boolean isIn = gizmo
						.isInBoundingBox(RavTechDK.mainSceneView.camera
							.unproject(new Vector2(Gdx.input.getX(),
								Gdx.input.getY() - 24)));
					if (isIn) {
						transform = objects.get(i).getParent().transform;
						break;
					}
				}
			}
		return transform;
	}

	static PolygonRegion createCircleRegion (float degrees) {
		final int steps = Math.abs((int)degrees);
		if (steps < 3) {
			return new PolygonRegion(new TextureRegion(whiteTexture),
				new float[0], new short[0]);
		}

		float[] vertecies = new float[steps * 2];
		vertecies[0] = 0;
		vertecies[1] = 0;

		for (int i = 2; i < (vertecies.length); i += 2) {
			float subDegrees = degrees * ((float)i - 2)
				/ (vertecies.length - 4);
			vertecies[i] = (float)Math.cos(Math.toRadians(subDegrees));
			vertecies[i + 1] = (float)Math
				.sin(Math.toRadians(subDegrees));
		}

		short[] triangles = new short[(steps - 2) * 3];

		for (int i = 0; i < (steps - 2) * 3; i += 3) {
			triangles[i] = 0;
			triangles[i + 1] = (short)((i / 3) + 1);
			triangles[i + 2] = (short)((i / 3) + 2);
		}

		return new PolygonRegion(new TextureRegion(whiteTexture),
			vertecies, triangles);
	}

}
