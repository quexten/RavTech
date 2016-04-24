
package com.quexten.ravtech.history;

import com.quexten.ravtech.components.GameComponent;
import com.quexten.ravtech.util.GameObjectTraverseUtil;

public class Changeable {

	public boolean isDummy = false;
	public boolean isLocal = true;
	public boolean previousConnected = false;
	public String pathToComponent;
	String changeLabel;

	public Changeable (GameComponent component, String changeLabel) {
		if (component != null)
			pathToComponent = GameObjectTraverseUtil
				.pathFromGameComponent(component);
		this.changeLabel = changeLabel;
	}

	public void redo () {
	}

	public void undo () {
	}

	public String getChangeLabel () {
		return changeLabel;
	}
}
