
package com.ravelsoftware.ravtech.history;

import com.ravelsoftware.ravtech.components.GameComponent;
import com.ravelsoftware.ravtech.util.GameObjectTraverseUtil;

public class Changeable {

	public boolean isDummy = false;
	public boolean isLocal = true;
	public boolean previousConnected = false;
	public String pathToComponent;
	String changeLabel;

	public Changeable (GameComponent component, String changeLabel) {
		if (component != null) this.pathToComponent = GameObjectTraverseUtil.pathFromGameComponent(component);
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
