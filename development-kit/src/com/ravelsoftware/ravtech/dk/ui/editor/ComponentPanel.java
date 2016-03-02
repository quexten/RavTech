
package com.ravelsoftware.ravtech.dk.ui.editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerListener;
import com.ravelsoftware.ravtech.components.GameComponent;
import com.ravelsoftware.ravtech.history.ChangeManager;
import com.ravelsoftware.ravtech.history.ModifyChangeable;

public abstract class ComponentPanel {

	public abstract VisTable createTable (GameComponent component);

	public void addSliderLabel (VisTable table, String variableName, GameComponent component) {
		final String variable = variableName;
		final GameComponent gameComponent = component;
		final LabelNumberPair label = new LabelNumberPair(variable.substring(0, 1).toUpperCase() + variable.substring(1) + ":",
			Float.valueOf(String.valueOf(component.getVariable(component.getVariableId(variable)))));
		table.add(label.label).padLeft(6).growX();
		table.add(label.pairedComponent).growX();
		table.setFillParent(true);
		table.row();

		label.releasedListener = new Runnable() {
			@Override
			public void run () {
				new ModifyChangeable(gameComponent, "", variable, 0, label.dragValue).redo();
			}
		};
		label.draggedListener = new Runnable() {
			@Override
			public void run () {
				ChangeManager
					.addChangeable(new ModifyChangeable(gameComponent, "Set " + variable, variable, label.oldValue, label.dragValue));
			}
		};
	}

	public void addColorPicker (VisTable table, String variableName, GameComponent component) {
		final String variable = variableName;
		final GameComponent gameComponent = component;
		final LabelColorPair label = new LabelColorPair(variable.substring(0, 1).toUpperCase() + variable.substring(1) + ":",
			(Color)component.getVariable(component.getVariableId(variable)));
		table.add(label.label).padLeft(6).growX();
		table.add(label.pairedComponent).expand().height(20).growX();
		table.setFillParent(true);
		table.row();

		label.pairedComponent.addListener(new ClickListener() {
			public void clicked (InputEvent event, float x, float y) {
				final ColorPicker picker = new ColorPicker();
				picker.setListener(new ColorPickerListener() {
					@Override
					public void canceled (Color oldColor) {
						gameComponent.setVariable(gameComponent.getVariableId(variable), oldColor);
					}

					@Override
					public void changed (Color newColor) {
						gameComponent.setVariable(gameComponent.getVariableId(variable), newColor);
					}

					@Override
					public void reset (Color previousColor, Color newColor) {
					}

					@Override
					public void finished (Color newColor) {
						picker.dispose();
					}
				});
				label.label.getStage().addActor(picker);
			}
		});
	}

}
