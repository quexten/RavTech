
package com.ravelsoftware.ravtech.dk.ui.editor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.PopupMenu;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.ravelsoftware.ravtech.components.GameComponent;
import com.ravelsoftware.ravtech.dk.RavTechDKUtil;

public class Inspector extends VisWindow {

	public Inspector () {
		super("Inspector");
		top();
		ComponentPanels.registerPanels();
		setSize(300, 500);
		setVisible(false);
	}

	public void act (float delta) {
		super.act(delta);
		if (RavTechDKUtil.hasInspectorChanged()) {
			RavTechDKUtil.inspectorSynced();
			rebuild();
		}
	}

	void rebuild () {
		clear();
		add(new VisTable()).top().padTop(32);
		row();
		setVisible(true);
		if (RavTechDKUtil.selectedObjects.size > 0) {
			for (int i = 0; i < RavTechDKUtil.selectedObjects.first().getComponents().size; i++)
				addCollapsiblePanel(RavTechDKUtil.selectedObjects.first().getComponents().get(i));
			final VisTextButton textButton = new VisTextButton("Add Component");
			textButton.addListener(new ChangeListener() {
				@Override
				public void changed (ChangeEvent event, Actor actor) {
					PopupMenu menu = createMenu();
					menu.showMenu(getStage(), textButton.getX(), textButton.getY());
				}
			});
			row();
			add(textButton);
		} else
			setVisible(false);
	}

	void addCollapsiblePanel (GameComponent component) {
		CollapsiblePanel title = new CollapsiblePanel(component.getName(), ComponentPanels.createTable(component));
		add(title).growX();
		row();
	}

	PopupMenu createMenu () {
		PopupMenu menu = new PopupMenu();
		Entries<Class<? extends GameComponent>, ComponentPanel> entries = ComponentPanels.panels.iterator();
		while (entries.hasNext) {
			final Entry<Class<? extends GameComponent>, ComponentPanel> entry = entries.next();
			final String className = entry.key.getName();
			MenuItem item = new MenuItem(entry.key.getSimpleName());
			item.addListener(new ChangeListener() {
				@SuppressWarnings("unchecked")
				@Override
				public void changed (ChangeEvent event, Actor actor) {
					GameComponent component;
					try {
						component = ClassReflection.newInstance(ClassReflection.forName(className));
						RavTechDKUtil.selectedObjects.get(0).addComponent(component);
						component.finishedLoading();
					} catch (ReflectionException e) {
						e.printStackTrace();
					}
				}
			});
			menu.addItem(item);
		}
		return menu;
	}

}