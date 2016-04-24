
package com.quexten.ravtech.dk.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.quexten.ravtech.components.GameObject;
import com.quexten.ravtech.dk.RavTechDK;
import com.quexten.ravtech.util.PrefabManager;

public class CopyAction implements Runnable {

	@Override
	public void run () {
		Array<GameObject> objects = RavTechDK.selectedObjects;
		Gdx.app.getClipboard()
			.setContents(PrefabManager.makePrefab(objects.get(0)));
	}
}
