
package com.ravelsoftware.ravtech.dk.ui.editor;

import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.widget.VisTable;
import com.ravelsoftware.ravtech.components.GameComponent;
import com.ravelsoftware.ravtech.components.Light;
import com.ravelsoftware.ravtech.components.SpriteRenderer;
import com.ravelsoftware.ravtech.components.Transform;

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
				addColorPicker(table, "tint", component);
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
	}

}
