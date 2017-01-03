
package com.quexten.ravtech.dk.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.quexten.ravtech.components.GameObject;
import com.quexten.ravtech.dk.RavTechDK;
import com.quexten.ravtech.dk.ui.editor.SceneViewWidget;
import com.quexten.ravtech.util.PrefabManager;

public class CopyAction implements Runnable {

	public CopyAction(SceneViewWidget sceneView) {
		Gdx.app.getClipboard().setContents(PrefabManager.makePrefab(sceneView.selectedObjects.first()));
	}
	
	@Override
	public void run () {
	}
}
