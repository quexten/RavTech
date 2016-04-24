
package com.quexten.ravtech.dk.actions;

import com.quexten.ravtech.dk.RavTechDK;

public class DeleteAction implements Runnable {

	@Override
	public void run () {
		for (int i = 0; i < RavTechDK.selectedObjects.size; i++)
			RavTechDK.selectedObjects.get(i).destroy();
	}

}
