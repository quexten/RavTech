
package com.ravelsoftware.ravtech.dk.ui.editor;

import com.kotcrab.vis.ui.widget.VisTable;
import com.ravelsoftware.ravtech.components.GameComponent;

public abstract class ComponentPanel {

	public abstract VisTable createTable (GameComponent component);

	public void addSliderLabel (VisTable table, String variableName, GameComponent component) {
		final String variable = variableName;
		final LabelNumberPair label = new LabelNumberPair(variable.substring(0, 1).toUpperCase() + variable.substring(1) + ":",
			Float.valueOf(String.valueOf(component.getVariable(component.getVariableId(variable)))));
		table.add(label.label).padLeft(6).growX();
		table.add(label.pairedComponent).growX();
		table.setFillParent(true);
		table.row();
	}

}
