
package com.ravelsoftware.ravtech.dk.actions;

import com.ravelsoftware.ravtech.dk.RavTechDK;

public class DeleteAction implements Runnable {

	@Override
	public void run () {
		for (int i = 0; i < RavTechDK.selectedObjects.size; i++)
			RavTechDK.selectedObjects.get(i).destroy();
	}

}
