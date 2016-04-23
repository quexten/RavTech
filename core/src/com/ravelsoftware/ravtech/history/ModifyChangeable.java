
package com.ravelsoftware.ravtech.history;

import com.ravelsoftware.ravtech.components.GameComponent;
import com.ravelsoftware.ravtech.util.GameObjectTraverseUtil;

public class ModifyChangeable extends Changeable {

	public int variableID;
	public Object newValue;
	Object oldValue;

	public ModifyChangeable () {
		super(null, null);
	}

	public ModifyChangeable (GameComponent component,
		String changeLabel, String variableName, Object oldValue,
		Object newValue) {
		super(component, changeLabel);
		variableID = component.getVariableId(variableName);
		this.newValue = newValue;
		this.oldValue = oldValue;
		redo();
	}

	@Override
	public void redo () {
		GameObjectTraverseUtil.gameComponentFromPath(pathToComponent)
			.setVariable(variableID, newValue);
	}

	@Override
	public void undo () {
		GameObjectTraverseUtil.gameComponentFromPath(pathToComponent)
			.setVariable(variableID, oldValue);
	}

	@Override
	public String getChangeLabel () {
		return changeLabel + newValue;
	}
}
