
package com.quexten.ravtech.dk.packaging.platforms;

import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

public class AndroidBuildOptionsTable extends VisTable {

	VisCheckBox signBox;

	public AndroidBuildOptionsTable (BuildOptions options) {
		signBox = new VisCheckBox("");
		this.add().growX();
		this.add().growX();
		this.row();
		this.add(new VisLabel("Sign")).growX().left();
		this.add(signBox).left().padRight(23);
		this.row();
	}
}
