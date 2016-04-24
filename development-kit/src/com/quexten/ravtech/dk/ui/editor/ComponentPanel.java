
package com.quexten.ravtech.dk.ui.editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisTextField.TextFieldListener;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerListener;
import com.quexten.ravtech.components.GameComponent;
import com.quexten.ravtech.dk.RavTechDK;
import com.quexten.ravtech.history.ChangeManager;
import com.quexten.ravtech.history.ModifyChangeable;

public abstract class ComponentPanel {

	public abstract VisTable createTable (GameComponent component);

	ObjectMap<String, Runnable> valueChangedListeners = new ObjectMap<String, Runnable>();
	GameComponent component;

	public void addSliderLabel (VisTable table, String variableName) {
		final String variable = variableName;
		final GameComponent gameComponent = component;
		final LabelNumberPair label = new LabelNumberPair(
			variable.substring(0, 1).toUpperCase()
				+ variable.substring(1) + ":",
			Float.valueOf(String.valueOf(component
				.getVariable(component.getVariableId(variable)))));
		table.add(label.label).padLeft(6).growX();
		table.add(label.pairedComponent).growX();
		table.setFillParent(true);
		table.row();

		label.releasedListener = new Runnable() {
			@Override
			public void run () {
				new ModifyChangeable(gameComponent, "", variable, 0,
					label.dragValue).redo();
			}
		};
		label.draggedListener = new Runnable() {
			@Override
			public void run () {
				ChangeManager.addChangeable(
					new ModifyChangeable(gameComponent, "Set " + variable,
						variable, label.oldValue, label.dragValue));
			}
		};
		valueChangedListeners.put(variableName, new Runnable() {
			@Override
			public void run () {
				label.label.setText(String.valueOf(component
					.getVariable(component.getVariableId(variable))));
			}
		});
	}

	public void addDropdown (VisTable table, String variableName,
		String[] options) {
		final String variable = variableName;
		final GameComponent gameComponent = component;
		final LabelDropdownPair label = new LabelDropdownPair(
			variable.substring(0, 1).toUpperCase()
				+ variable.substring(1) + ":",
			options, String.valueOf(gameComponent
				.getVariable(gameComponent.getVariableId(variableName))));
		table.add(label.label).padLeft(6).growX();
		table.add(label.pairedComponent).growX();
		table.setFillParent(true);
		table.row();

		label.pairedComponent.addListener(new ChangeListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				gameComponent.setVariable(
					gameComponent.getVariableId(variable),
					((VisSelectBox<String>)label.pairedComponent)
						.getSelected());
			}
		});
	}

	public void addColorPicker (VisTable table, String variableName) {
		final String variable = variableName;
		final GameComponent gameComponent = component;
		final LabelColorPair label = new LabelColorPair(
			variable.substring(0, 1).toUpperCase()
				+ variable.substring(1) + ":",
			(Color)component
				.getVariable(component.getVariableId(variable)));
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
						((ColorPanel)label.pairedComponent).backgroundColor
							.set(oldColor);
						gameComponent.setVariable(
							gameComponent.getVariableId(variable), oldColor);
					}

					@Override
					public void changed (Color newColor) {
						((ColorPanel)label.pairedComponent).backgroundColor
							.set(newColor);
						gameComponent.setVariable(
							gameComponent.getVariableId(variable), newColor);
					}

					@Override
					public void reset (Color previousColor,
						Color newColor) {
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

	public void addButton (VisTable table, String text, String title,
		ChangeListener listener) {
		table.add(new VisLabel(text));
		VisTextButton button = new VisTextButton(title);
		button.addListener(listener);
		table.add(button);
		table.row();
	}

	public void addFileSelector (VisTable table, String text,
		String initialPath, final ChangeListener listener,
		final String... fileTypes) {
		table.add(new VisLabel(text)).expandX().growX().padLeft(5);
		final VisLabel fileLabel = new VisLabel(initialPath);
		VisTable padTable = new VisTable();
		table.add(padTable).expandX().growX().height(20);
		padTable.add(fileLabel).growX().fillX();
		table.row();
		RavTechDK.assetViewer.dragAndDrop
			.addTarget(new Target(padTable) {
				@Override
				public boolean drag (Source source, Payload payload,
					float x, float y, int pointer) {
					String objectPath = String
						.valueOf(payload.getObject());
					for (int i = 0; i < fileTypes.length; i++)
						if (fileTypes[i].equals(objectPath
							.substring(objectPath.lastIndexOf(".") + 1)))
							return true;
					return false;
				}

				@Override
				public void drop (Source source, Payload payload, float x,
					float y, int pointer) {
					fileLabel.setText(
						String.valueOf(payload.getObject()).replace(
							RavTechDK.projectHandle.path() + "/assets/",
							""));
					listener.changed(new ChangeEvent(), fileLabel);
				}
			});
	}

	public void addTextField (VisTable table,
		final String variableName) {
		table
			.add(new VisLabel(variableName.substring(0, 1).toUpperCase()
				+ variableName.substring(1) + ":"))
			.expandX().growX().padLeft(5);
		final VisTextField textField = new VisTextField(
			String.valueOf(component
				.getVariable(component.getVariableId(variableName))));
		textField.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				component.setVariable(
					component.getVariableId(variableName),
					textField.getText());
			}
		});
		textField.setFocusTraversal(false);
		textField.setTextFieldListener(new TextFieldListener() {
			@Override
			public void keyTyped (VisTextField textField, char key) {
				if (key == '\n' || key == '\r')
					textField.focusLost();
			}
		});
		table.add(textField).growX();
		table.row();
	}

	public void addCheckBox (VisTable table,
		final String variableName) {
		table
			.add(new VisLabel(variableName.substring(0, 1).toUpperCase()
				+ variableName.substring(1) + ":"))
			.expandX().growX().padLeft(5);
		final VisCheckBox checkBox = new VisCheckBox("",
			Boolean.valueOf(String.valueOf(component
				.getVariable(component.getVariableId(variableName)))));
		checkBox.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				component.setVariable(
					component.getVariableId(variableName),
					checkBox.isChecked());
			}
		});
		table.add(checkBox).align(Align.right);
		table.row();
	}

	public void updateValue (String value) {
		if (valueChangedListeners.containsKey(value))
			valueChangedListeners.get(value).run();
	}
}
