package com.ravelsoftware.ravtech.dk.ui.editor;

import com.kotcrab.vis.ui.widget.VisSelectBox;

public class LabelDropdownPair extends LabelActorPair<String> {
	
	@SuppressWarnings("unchecked")
	public LabelDropdownPair(String text, String[] options, String selectedOption) {
		super(text, new VisSelectBox<String>(), false);
		((VisSelectBox<String>) this.pairedComponent).setItems(options);
		((VisSelectBox<String>) this.pairedComponent).setSelected(selectedOption);
	}

	@SuppressWarnings("unchecked")
	@Override
	String getValue () {
		return (String)((VisSelectBox<String>) this.pairedComponent).getSelected();
	}
	
}
