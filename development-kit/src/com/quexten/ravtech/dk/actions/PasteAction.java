
package com.ravelsoftware.ravtech.dk.actions;

import com.badlogic.gdx.Gdx;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.components.GameObject;
import com.ravelsoftware.ravtech.util.PrefabManager;

public class PasteAction implements Runnable {

	@Override
	public void run () {
		try {
			GameObject object = PrefabManager
				.makeObject(Gdx.app.getClipboard().getContents());
			RavTech.currentScene.addGameObject(object);
			object.transform
				.setPosition(RavTech.input.getWorldPosition());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
