
package com.quexten.ravtech.dk.ui.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectIntMap.Entries;
import com.badlogic.gdx.utils.ObjectIntMap.Entry;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisTextField.TextFieldFilter;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.input.ActionMap;
import com.quexten.ravtech.input.KeyboardMouseDevice;
import com.quexten.ravtech.input.RavInput;

public class ActionMapEditor extends RavWindow {

	final VisSelectBox<String> dropDown;
	int currentInputAmount = RavTech.input.inputDevices.size;
	VisTable actionTable = new VisTable();
	ObjectMap<String, ActionMap> actionMaps = new ObjectMap<String, ActionMap>();
	ActionMap currentActionMap;

	InputMultiplexer multiplexer = new InputMultiplexer();

	public ActionMapEditor (ObjectMap<String, ActionMap> actionMaps) {
		super("Action Map Editor", true);

		this.actionMaps = actionMaps;
		this.currentActionMap = actionMaps.entries().next().value;
		buildActionTable();

		VisLabel selectionLabel = new VisLabel("Input:");
		this.top();
		this.add(selectionLabel).growX();

		String[] devices = new String[] {"KeyboardMouse", "GamePad"};

		for (int i = 0; i < devices.length; i++) {
			if (!actionMaps.containsKey(devices[i])) {
				ActionMap actionMap = new ActionMap();
				Entries<String> actionMapEntries = ActionMapEditor.this.actionMaps.entries().next().value.entries();
				while (actionMapEntries.hasNext) {
					actionMap.put(devices[i], actionMapEntries.next().value);
				}
			}
		}

		dropDown = new VisSelectBox<String>();
		dropDown.setItems(devices);
		dropDown.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				ObjectMap<String, ActionMap> actionMaps = ActionMapEditor.this.actionMaps;
				ActionMapEditor.this.currentActionMap = actionMaps.get(dropDown.getSelected());
				buildActionTable();
			}
		});

		this.add(dropDown).growX().right();
		this.row();

		this.add(actionTable).grow().colspan(2);
		this.row();

		this.add();
		VisTextButton addButton = new VisTextButton("Add");
		addButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				com.badlogic.gdx.utils.ObjectMap.Entries<String, ActionMap> actionMapEntries = ActionMapEditor.this.actionMaps
					.entries();
				while (actionMapEntries.hasNext) {
					actionMapEntries.next().value.put("", 0);
				}
				buildActionTable();
			}
		});
		this.add(addButton).right();
		VisTextButton saveButton = new VisTextButton("Save");
		saveButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				Json json = new Json();
				json.setOutputType(OutputType.json);
				json.setTypeName(null);
				RavTech.files.getAssetHandle("keybindings.json").writeString(json.prettyPrint(ActionMapEditor.this.actionMaps),
					false);
			}
		});
		this.add(saveButton).right();

		this.row();
		this.setSize(300, 400);
		this.center();
	}

	@Override
	public void act (float delta) {

	}

	void buildActionTable () {
		actionTable.clear();
		Entries<String> entries = currentActionMap.entries();

		while (entries.hasNext) {
			final Entry<String> entry = entries.next();
			TextNumberPair textNumberPair = new TextNumberPair(entry.key, entry.value);
			actionTable.add(textNumberPair.nameField).growX();
			actionTable.add(textNumberPair.numberField).growX();
			actionTable.row();
		}
	}

	class TextNumberPair extends VisTable {

		final VisTextField nameField;
		final VisTextField numberField;
		String key;
		int value;
		boolean isFocused;

		public TextNumberPair (String key, int value) {
			this.key = key;
			this.value = value;

			numberField = new VisTextField(String.valueOf(value)) {
				int lastIndex;

				@Override
				public void act (float delta) {
					super.act(delta);
					if (TextNumberPair.this.isFocused)
						if (RavTech.input.getDevice(dropDown.getSelected()).getLastPressed() != lastIndex) {
							this.setText(String.valueOf(RavTech.input.getDevice(dropDown.getSelected()).getLastPressed()));
						}
					lastIndex = RavTech.input.getDevice(dropDown.getSelected()).getLastPressed();
				}

				@Override
				public void focusGained () {
					super.focusGained();
					System.out.println("focus gained");
					if (Gdx.input.getInputProcessor() != ActionMapEditor.this.multiplexer) {
						InputProcessor currentProcessor = Gdx.input.getInputProcessor();
						multiplexer.addProcessor(((KeyboardMouseDevice)RavTech.input.getDevice("KeyboardMouse")).processor);
						multiplexer.addProcessor(currentProcessor);
						Gdx.input.setInputProcessor(multiplexer);
					}
					TextNumberPair.this.isFocused = true;
				}

				@Override
				public void focusLost () {
					super.focusLost();
					TextNumberPair.this.isFocused = false;
					currentActionMap.put(TextNumberPair.this.key, Integer.valueOf(getText().isEmpty() ? "0" : getText()));
					TextNumberPair.this.value = Integer.valueOf(getText());
				}
			};
			numberField.setTextFieldFilter(new TextFieldFilter() {
				@Override
				public boolean acceptChar (VisTextField textField, char c) {
					return Character.isDigit(c);
				}
			});

			nameField = new VisTextField(key) {
				@Override
				public void focusLost () {
					super.focusLost();
					com.badlogic.gdx.utils.ObjectMap.Entries<String, ActionMap> actionMapEntries = ActionMapEditor.this.actionMaps
						.entries();
					while (actionMapEntries.hasNext) {
						ActionMap iteratedActionMap = actionMapEntries.next().value;
						iteratedActionMap.remove(TextNumberPair.this.key, cursor);
						iteratedActionMap.put(getText(), TextNumberPair.this.value);
					}
					TextNumberPair.this.key = getText();
				}
			};
		}

	}

}
