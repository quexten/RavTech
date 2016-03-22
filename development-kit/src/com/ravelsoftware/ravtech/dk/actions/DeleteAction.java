
package com.ravelsoftware.ravtech.dk.actions;

import com.ravelsoftware.ravtech.dk.RavTechDKUtil;

public class DeleteAction implements Runnable {

	@Override
	public void run () {
		for (int i = 0; i < RavTechDKUtil.selectedObjects.size; i++)
			RavTechDKUtil.selectedObjects.get(i).destroy();
	}

}
