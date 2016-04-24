
package com.quexten.ravtech.dk.ui.editor;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.CollapsibleWidget;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisImageButton.VisImageButtonStyle;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

public class CollapsiblePanel extends VisTable {

	private static final Drawable treePlus = VisUI.getSkin()
		.getDrawable("tree-plus");
	private static final Drawable treeMinus = VisUI.getSkin()
		.getDrawable("tree-minus");

	public CollapsiblePanel (String title, VisTable contentTable) {
		VisTable titleTable = new VisTable(true);
		titleTable
			.setBackground(VisUI.getSkin().getDrawable("tree-over"));

		final Image icon = new Image(treeMinus, Scaling.none);

		VisLabel nameLabel = new VisLabel(title);

		titleTable.add(icon).size(32).spaceRight(0);
		titleTable.add(nameLabel).spaceRight(0).width(220);
		titleTable.add().space(0).expandX().fillX();
		VisImageButton button = new VisImageButton("close");
		VisImageButtonStyle style = button.getStyle();
		Drawable up = style.up;
		style.up = style.over;
		style.over = up;
		titleTable.add(button);

		final CollapsibleWidget collapsible = new CollapsibleWidget(
			contentTable, false);
		add(titleTable).expandX().fillX().row();
		add(collapsible).expandX().fillX().padTop(3);

		nameLabel.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				collapsible.setCollapsed(!collapsible.isCollapsed(),
					false);
				icon.setDrawable(
					collapsible.isCollapsed() ? treePlus : treeMinus);
			}
		});
	}

}
