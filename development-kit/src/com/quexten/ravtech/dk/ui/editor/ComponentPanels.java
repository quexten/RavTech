
package com.quexten.ravtech.dk.ui.editor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.components.BoxCollider;
import com.quexten.ravtech.components.CircleCollider;
import com.quexten.ravtech.components.ComponentType;
import com.quexten.ravtech.components.FontRenderer;
import com.quexten.ravtech.components.GameComponent;
import com.quexten.ravtech.components.Light;
import com.quexten.ravtech.components.Light.LightType;
import com.quexten.ravtech.components.Rigidbody;
import com.quexten.ravtech.components.ScriptComponent;
import com.quexten.ravtech.components.SpriteRenderer;
import com.quexten.ravtech.components.Transform;
import com.quexten.ravtech.dk.RavTechDK;
import com.quexten.ravtech.graphics.SortingLayer;

public class ComponentPanels {

	static ObjectMap<Class<? extends GameComponent>, ComponentPanel> panels = new ObjectMap<Class<? extends GameComponent>, ComponentPanel>();

	static <T extends GameComponent> void registerComponent (
		Class<T> component, ComponentPanel panel) {
		panels.put(component, panel);
	}

	public static <T extends GameComponent> VisTable createTable (
		T component) {
		return panels.get(component.getClass()) != null
			? panels.get(component.getClass()).createTable(component)
			: new VisTable();
	}

	public static void registerPanels () {
		ComponentPanels.registerComponent(Transform.class,
			new ComponentPanel() {
				@Override
				public VisTable createTable (GameComponent component) {
					this.component = component;
					VisTable table = new VisTable();
					addSliderLabel(table, "x");
					addSliderLabel(table, "y");
					addSliderLabel(table, "rotation");
					addSliderLabel(table, "scaleX");
					addSliderLabel(table, "scaleY");
					return table;
				}
			});
		ComponentPanels.registerComponent(SpriteRenderer.class,
			new ComponentPanel() {
				@Override
				public VisTable createTable (GameComponent component) {
					this.component = component;
					VisTable table = new VisTable();
					addSliderLabel(table, "width");
					addSliderLabel(table, "height");
					addSliderLabel(table, "srcX");
					addSliderLabel(table, "srcY");
					addSliderLabel(table, "srcWidth");
					addSliderLabel(table, "srcHeight");
					addSliderLabel(table, "originX");
					addSliderLabel(table, "originY");
					addDropdown(table, "minFilter",
						new String[] {"Linear", "Nearest"});
					addDropdown(table, "magFilter",
						new String[] {"Linear", "Nearest"});
					Array<SortingLayer> layers = RavTech.currentScene.renderProperties.sortingLayers;
					String[] layernames = new String[layers.size];
					for (int i = 0; i < layers.size; i++)
						layernames[i] = layers.get(i).name;
					addDropdown(table, "sortingLayer", layernames);
					addSliderLabel(table, "sortingOrder");
					addColorPicker(table, "tint");
					addDropdown(table, "uTextureWrap", new String[] {
						"ClampToEdge", "Repeat", "MirroredRepeat"});
					addDropdown(table, "vTextureWrap", new String[] {
						"ClampToEdge", "Repeat", "MirroredRepeat"});
					return table;
				}
			});
		ComponentPanels.registerComponent(FontRenderer.class,
			new ComponentPanel() {
				@Override
				public VisTable createTable (
					final GameComponent component) {
					this.component = component;
					VisTable table = new VisTable();
					addFileSelector(table, "Path:",
						((FontRenderer)component).path,
						new ChangeListener() {
						@Override
						public void changed (ChangeEvent event,
							Actor actor) {
							((FontRenderer)component).setFont(
								((VisLabel)actor).getText().toString());
						}
					}, "font");
					addTextField(table, "text");
					addCheckBox(table, "centered");
					addCheckBox(table, "flipped");
					addSliderLabel(table, "xScale");
					addSliderLabel(table, "yScale");
					addColorPicker(table, "tint");
					return table;
				}
			});
		ComponentPanels.registerComponent(Light.class,
			new ComponentPanel() {
				@Override
				public VisTable createTable (GameComponent component) {
					this.component = component;
					VisTable table = new VisTable();
					addSliderLabel(table, "angle");
					addColorPicker(table, "color");
					addSliderLabel(table, "distance");
					addSliderLabel(table, "rayCount");
					addDropdown(table, "type",
						new String[] {LightType.ChainLight.toString(),
							LightType.ConeLight.toString(),
							LightType.DirectionalLight.toString(),
							LightType.PointLight.toString()});
					addCheckBox(table, "isSoft");
					addSliderLabel(table, "softnessLength");
					return table;
				}
			});
		ComponentPanels.registerComponent(Rigidbody.class,
			new ComponentPanel() {
				@Override
				public VisTable createTable (GameComponent component) {
					this.component = component;
					VisTable table = new VisTable();
					addSliderLabel(table, "angularDamping");
					addSliderLabel(table, "gravityScale");
					addSliderLabel(table, "linearDamping");
					addDropdown(table, "bodyType",
						new String[] {"Static", "Dynamic", "Kinematic"});
					return table;
				}
			});
		ComponentPanels.registerComponent(BoxCollider.class,
			new ComponentPanel() {
				@Override
				public VisTable createTable (
					final GameComponent component) {
					this.component = component;
					VisTable table = new VisTable();
					addSliderLabel(table, "x");
					addSliderLabel(table, "y");
					addSliderLabel(table, "width");
					addSliderLabel(table, "height");
					addSliderLabel(table, "angle");
					addButton(table, "Edit Collider", "Edit",
						new ChangeListener() {
						@Override
						public void changed (ChangeEvent event,
							Actor actor) {
							RavTechDK.gizmoHandler
								.setExclusiveGizmo(component);
						}
					});
					addButton(table, "AutoFit", "Fit",
						new ChangeListener() {
						@Override
						public void changed (ChangeEvent event,
							Actor actor) {
							SpriteRenderer spriteRenderer = (SpriteRenderer)component
								.getParent().getComponentByType(
									ComponentType.SpriteRenderer);
							if (spriteRenderer != null) {
								((BoxCollider)component).setBounds(
									spriteRenderer.width,
									spriteRenderer.height);
								((BoxCollider)component).setPosition(
									-spriteRenderer.originX
										* spriteRenderer.width / 2,
									-spriteRenderer.originY
										* spriteRenderer.height / 2);
							}
						}
					});
					return table;
				}
			});
		ComponentPanels.registerComponent(CircleCollider.class,
			new ComponentPanel() {
				@Override
				public VisTable createTable (
					final GameComponent component) {
					this.component = component;
					VisTable table = new VisTable();
					addSliderLabel(table, "x");
					addSliderLabel(table, "y");
					addSliderLabel(table, "radius");
					addButton(table, "Edit Collider", "Edit",
						new ChangeListener() {
						@Override
						public void changed (ChangeEvent event,
							Actor actor) {
							RavTechDK.gizmoHandler
								.setExclusiveGizmo(component);
						}
					});
					addButton(table, "AutoFit", "Fit",
						new ChangeListener() {
						@Override
						public void changed (ChangeEvent event,
							Actor actor) {
							SpriteRenderer spriteRenderer = (SpriteRenderer)component
								.getParent().getComponentByType(
									ComponentType.SpriteRenderer);
							if (spriteRenderer != null) {
								((CircleCollider)component)
									.setRadius(spriteRenderer.height / 2);
								((CircleCollider)component).setPosition(
									-spriteRenderer.originX
										* spriteRenderer.width / 2,
									-spriteRenderer.originY
										* spriteRenderer.height / 2);
							}
						}
					});
					return table;
				}
			});
		ComponentPanels.registerComponent(ScriptComponent.class,
			new ComponentPanel() {
				@Override
				public VisTable createTable (
					final GameComponent component) {
					this.component = component;
					VisTable table = new VisTable();
					addFileSelector(table, "Path:",
						((ScriptComponent)component).path,
						new ChangeListener() {

						@Override
						public void changed (ChangeEvent event,
							Actor actor) {
							((ScriptComponent)component).setScript(
								((VisTextField)actor).getText().toString());
						}

					}, "lua");
					return table;
				}
			});
	}

}
