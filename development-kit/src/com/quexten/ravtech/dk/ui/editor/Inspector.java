
package com.quexten.ravtech.dk.ui.editor;

import java.lang.reflect.Method;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.quexten.ravtech.components.GameComponent;
import com.quexten.ravtech.dk.RavTechDK;

public class Inspector extends RavWindow {

	VisTable contentTable;
	VisScrollPane scrollPane;
	float scroll;
	public Array<Actor> dragActors = new Array<Actor>();
	Array<ComponentPanel> componentPanels = new Array<ComponentPanel>();

	boolean inspectorChanged;

	public Inspector () {
		super("Inspector", false);
		ComponentPanels.registerPanels();
		setSize(300, 500);
		setPosition(0, 200);
		setVisible(true);
	}

	public void act (float delta) {
		super.act(delta);
		if (hasChanged()) {
			synced();
			rebuild();
		}
	}

	void rebuild () {
		if (scrollPane != null)
			scroll = scrollPane.getScrollY();
		clear();

		VisTable nameTable = new VisTable();
		nameTable.add(new VisLabel("Name:")).align(Align.bottomLeft)
			.padRight(5);
		final VisTextField nameField = new VisTextField();
		nameField.setText(RavTechDK.selectedObjects.size > 0 ? RavTechDK.selectedObjects.first().getName() : "");
		nameField.addListener(new InputListener() {
			@Override
			public boolean keyTyped (InputEvent event, char c) {
				if (c == '\r' || c == '\n') {
					RavTechDK.selectedObjects.first()
						.setName(nameField.getText());
					return true;
				} else
					return false;
			}
		});
		nameTable.add(nameField).growX();
		add(nameTable).growX().padBottom(5);
		row();

		contentTable = new VisTable();
		contentTable.top();
		scrollPane = new VisScrollPane(contentTable);
		scrollPane.setScrollingDisabled(true, false);
		contentTable.clear();
		setVisible(true);
		if (RavTechDK.selectedObjects.size > 0) {
			for (int i = 0; i < RavTechDK.selectedObjects.first()
				.getComponents().size; i++)
				addCollapsiblePanel(RavTechDK.selectedObjects.first()
					.getComponents().get(i));
			final VisTextButton textButton = new VisTextButton(
				"Add Component");
			textButton.addListener(new ChangeListener() {
				@Override
				public void changed (ChangeEvent event, Actor actor) {
					PopupMenu menu = createMenu();
					Vector2 stageCoordinates = contentTable
						.localToStageCoordinates(new Vector2(
							textButton.getX(), textButton.getY()));
					menu.showMenu(getStage(), stageCoordinates.x,
						stageCoordinates.y);
				}
			});
			contentTable.row();
			contentTable.add(textButton);
		} else
			setVisible(false);
		scrollPane.setFlickScroll(false);
		scrollPane.setFadeScrollBars(false);
		scrollPane.setForceScroll(false, true);
		scrollPane.setSmoothScrolling(false);
		add(scrollPane).grow();
		try {
			Method scrollMethod = ScrollPane.class
				.getDeclaredMethod("scrollY", float.class);
			scrollMethod.setAccessible(true);
			scrollMethod.invoke(scrollPane, scroll);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		scrollPane.act(0.1f);
	}

	void addCollapsiblePanel (GameComponent component) {
		CollapsiblePanel title = new CollapsiblePanel(
			component.getName(), ComponentPanels.createTable(component));
		contentTable.add(title).growX().padRight(20).padLeft(20);
		contentTable.row();
	}

	PopupMenu createMenu () {
		PopupMenu menu = new PopupMenu();
		Entries<Class<? extends GameComponent>, ComponentPanel> entries = ComponentPanels.panels
			.iterator();
		while (entries.hasNext) {
			final Entry<Class<? extends GameComponent>, ComponentPanel> entry = entries
				.next();
			final String className = entry.key.getName();
			MenuItem item = new MenuItem(entry.key.getSimpleName());
			item.addListener(new ChangeListener() {
				@SuppressWarnings("unchecked")
				@Override
				public void changed (ChangeEvent event, Actor actor) {
					GameComponent component;
					try {
						component = (GameComponent)ClassReflection
							.newInstance(ClassReflection.forName(className));
						RavTechDK.selectedObjects.get(0)
							.addComponent(component);
						component.finishedLoading();
						Inspector.this.rebuild();
						RavTechDK.gizmoHandler.setupGizmos();
					} catch (ReflectionException e) {
						e.printStackTrace();
					}
				}
			});
			menu.addItem(item);
		}
		return menu;
	}

	public void updateValue (GameComponent component,
		String valueName) {
		for (int i = 0; i < componentPanels.size; i++)
			if (componentPanels.get(i).component.equals(component))
				componentPanels.get(i).updateValue(valueName);
	}

	public boolean hasChanged () {
		return inspectorChanged;
	}

	public void changed () {
		com.quexten.ravtech.util.Debug.log("Inspector", "changed");
		inspectorChanged = true;
	}

	public void synced () {
		inspectorChanged = false;
	}

}