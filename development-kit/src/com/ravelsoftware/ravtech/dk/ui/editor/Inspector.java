
package com.ravelsoftware.ravtech.dk.ui.editor;

import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.ravelsoftware.ravtech.components.GameComponent;
import com.ravelsoftware.ravtech.dk.RavTechDKUtil;

public class Inspector extends VisTable {

	public Inspector () {
		this.setFillParent(true);
		this.top();
		this.getStage();
		add(new VisTextField("test")).top().padTop(32);
		row();
		ComponentPanels.registerPanels();
	}

	public void act (float delta) {
		super.act(delta);
		if (RavTechDKUtil.hasInspectorChanged()) {
			RavTechDKUtil.inspectorSynced();
			rebuild();
		}
	}

	void rebuild () {
		this.clear();
		add(new VisTable()).top().padTop(32);
		row();
		if (RavTechDKUtil.selectedObjects.size > 0)
			for (int i = 0; i < RavTechDKUtil.selectedObjects.first().getComponents().size; i++)
			addCollapsiblePanel(RavTechDKUtil.selectedObjects.first().getComponents().get(i));
	}

	void addCollapsiblePanel (GameComponent component) {
		CollapsiblePanel title = new CollapsiblePanel(component.getName(), ComponentPanels.createTable(component));
		add(title).growX();
		row();
	}

}
