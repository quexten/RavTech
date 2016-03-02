
package com.ravelsoftware.ravtech.dk.ui.editor;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.widget.VisTable;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.components.BoxCollider;
import com.ravelsoftware.ravtech.components.GameComponent;
import com.ravelsoftware.ravtech.components.Light;
import com.ravelsoftware.ravtech.components.Rigidbody;
import com.ravelsoftware.ravtech.components.SpriteRenderer;
import com.ravelsoftware.ravtech.components.Transform;
import com.ravelsoftware.ravtech.graphics.SortingLayer;

public class ComponentPanels {

	static ObjectMap<Class<? extends GameComponent>, ComponentPanel> panels = new ObjectMap<Class<? extends GameComponent>, ComponentPanel>();

	static <T extends GameComponent> void registerComponent (Class<T> component, ComponentPanel panel) {
		panels.put(component, panel);
	}

	public static <T extends GameComponent> VisTable createTable (T component) {
		return panels.get(component.getClass()) != null ? panels.get(component.getClass()).createTable(component) : new VisTable();
	}

	public static void registerPanels () {
		ComponentPanels.registerComponent(Transform.class, new ComponentPanel() {
			@Override
			public VisTable createTable (GameComponent component) {
				VisTable table = new VisTable();
				addSliderLabel(table, "x", component);
				addSliderLabel(table, "y", component);
				addSliderLabel(table, "rotation", component);
				return table;
			}
		});
		ComponentPanels.registerComponent(SpriteRenderer.class, new ComponentPanel() {
			@Override
			public VisTable createTable (GameComponent component) {
				VisTable table = new VisTable();
				addSliderLabel(table, "width", component);
				addSliderLabel(table, "height", component);
				addSliderLabel(table, "srcX", component);
				addSliderLabel(table, "srcY", component);
				addSliderLabel(table, "srcWidth", component);
				addSliderLabel(table, "srcHeight", component);
				addSliderLabel(table, "originX", component);
				addSliderLabel(table, "originY", component);
				addDropdown(table, "minFilter", new String[] {"Linear", "Nearest"}, component);
				addDropdown(table, "magFilter", new String[] {"Linear", "Nearest"}, component);
				Array<SortingLayer> layers = RavTech.currentScene.renderProperties.sortingLayers;
				String[] layernames = new String[layers.size];
				for (int i = 0; i < layers.size; i++)
					layernames[i] = layers.get(i).name;
				addDropdown(table, "sortingLayerName", layernames, component);
				addSliderLabel(table, "sortingOrder", component);
				addColorPicker(table, "tint", component);
				addDropdown(table, "uTextureWrap", new String[] {"ClampToEdge", "Repeat", "MirroredRepeat"}, component);
				addDropdown(table, "vTextureWrap", new String[] {"ClampToEdge", "Repeat", "MirroredRepeat"}, component);
				return table;
			}
		});
		ComponentPanels.registerComponent(Light.class, new ComponentPanel() {
			@Override
			public VisTable createTable (GameComponent component) {
				VisTable table = new VisTable();
				addColorPicker(table, "color", component);
				addSliderLabel(table, "angle", component);
				addSliderLabel(table, "distance", component);
				return table;
			}
		});
		ComponentPanels.registerComponent(Rigidbody.class, new ComponentPanel() {
			@Override
			public VisTable createTable (GameComponent component) {
				VisTable table = new VisTable();
				return table;
			}
		});
		ComponentPanels.registerComponent(BoxCollider.class, new ComponentPanel() {
			@Override
			public VisTable createTable (GameComponent component) {
				VisTable table = new VisTable();
				addSliderLabel(table, "x", component);
				addSliderLabel(table, "y", component);
				addSliderLabel(table, "width", component);
				addSliderLabel(table, "height", component);
				addSliderLabel(table, "angle", component);
				return table;
			}
		});
	}

}
