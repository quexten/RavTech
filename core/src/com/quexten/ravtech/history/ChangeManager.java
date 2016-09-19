
package com.quexten.ravtech.history;

import com.badlogic.gdx.utils.Array;

public class ChangeManager {

	public static int currentChangeable = 0;
	public static Array<Changeable> changeables = new Array<Changeable>();
	static Array<ChangeListener> changeListeners = new Array<ChangeListener>();

	public ChangeManager () {
	}

	public static void addChangeable (Changeable changeable) {
		if (!changeable.isDummy) {
			if (currentChangeable == changeables.size) {
				changeables.add(changeable);
				currentChangeable = changeables.size;
			} else {
				changeables.removeRange(currentChangeable, changeables.size - 1);
				changeables.add(changeable);
				currentChangeable++;
			}
			for (ChangeListener listener : changeListeners)
				listener.changed(changeable);
		}
	}

	public static void redo () {
		if (canRedo())
			changeables.get(currentChangeable).redo();
		if (currentChangeable < changeables.size)
			currentChangeable++;
		if (currentChangeable < changeables.size - 1 && changeables.get(currentChangeable + 1).previousConnected)
			redo();
	}

	public static void undo () {
		if (currentChangeable > 0)
			currentChangeable--;
		changeables.get(currentChangeable).undo();
		if (changeables.get(currentChangeable).previousConnected)
			undo();
	}

	public static boolean canUndo () {
		return currentChangeable != 0;
	}

	public static boolean canRedo () {
		return currentChangeable != changeables.size;
	}

	public static void addChangeableListener (ChangeListener listener) {
		changeListeners.add(listener);
	}
}
