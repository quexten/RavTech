
package com.ravelsoftware.ravtech.dk.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.ravelsoftware.ravtech.components.GameObject;
import com.ravelsoftware.ravtech.dk.RavTechDK;
import com.ravelsoftware.ravtech.util.PrefabManager;

public class CopyAction implements Runnable {

	@Override
	public void run () {
		Array<GameObject> objects = RavTechDK.selectedObjects;
		Gdx.app.getClipboard()
			.setContents(PrefabManager.makePrefab(objects.get(0)));
	}
}
