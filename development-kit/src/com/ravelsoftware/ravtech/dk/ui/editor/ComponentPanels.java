
package com.ravelsoftware.ravtech.dk.ui.editor;

import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.widget.VisTable;
import com.ravelsoftware.ravtech.components.GameComponent;

public class ComponentPanels {

	static ObjectMap<Class<? extends GameComponent>, ComponentPanel> panels = new ObjectMap<Class<? extends GameComponent>, ComponentPanel>();

	static <T extends GameComponent> void registerComponent (Class<T> component, ComponentPanel panel) {
		panels.put(component, panel);
	}

	public static <T extends GameComponent> VisTable createTable (T component) {
		return panels.get(component.getClass()) != null ? panels.get(component.getClass()).createTable(component) : new VisTable();
	}

	public void registerPanels () {

	}

}
