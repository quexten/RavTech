
package com.quexten.ravtech.dk.actions;

import com.badlogic.gdx.utils.Array;
import com.quexten.ravtech.components.GameObject;

public class DeleteAction implements Runnable {
		
	public DeleteAction(Array<GameObject> selectedObjects) {
		for(GameObject object : selectedObjects) {
			object.destroy();
		}
	}
	
	@Override
	public void run () {
	}

}
