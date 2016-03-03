
package com.ravelsoftware.ravtech.dk.ui.editor;

import com.badlogic.gdx.files.FileHandle;
import com.kotcrab.vis.ui.layout.HorizontalFlowGroup;
import com.kotcrab.vis.ui.widget.VisTable;
import com.ravelsoftware.ravtech.dk.RavTechDK;

public class AssetViewer extends VisTable {

	public AssetViewer () {
		setDirectory("");
	}

	public void setDirectory (String path) {
		clear();
		HorizontalFlowGroup group = new HorizontalFlowGroup();
		add(group).grow();

		FileHandle folderHandle = RavTechDK.projectHandle.child("assets").child(path);
		FileHandle[] files = folderHandle.list();
		for (int i = 0; i < files.length; i++) {
			FileHandle fileHandle = files[i];
			if (fileHandle.isDirectory()) {
				group.addActor(new FolderPreviewPanel(fileHandle.name()).pad(5));
			}
		}
	}

}
