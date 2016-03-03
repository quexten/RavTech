
package com.ravelsoftware.ravtech.dk.ui.editor;

import java.util.Comparator;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.layout.HorizontalFlowGroup;
import com.kotcrab.vis.ui.widget.VisTable;
import com.ravelsoftware.ravtech.dk.RavTechDK;

public class AssetViewer extends VisTable {

	ObjectMap<String, AssetPreviewPanel> previewPanels = new ObjectMap<String, AssetPreviewPanel>();
	AssetPreviewPanel lastSelected;

	public AssetViewer () {
		setDirectory("");
	}

	public void setDirectory (String path) {
		clear();
		HorizontalFlowGroup group = new HorizontalFlowGroup();
		add(group).grow();

		FileHandle folderHandle = RavTechDK.projectHandle.child("assets").child(path);
		Array<FileHandle> files = new Array<FileHandle>();
		files.addAll(folderHandle.list());
		files.sort(new Comparator<FileHandle>() {
			@Override
			public int compare (FileHandle fileOne, FileHandle fileTwo) {
				if (fileOne.isDirectory() != fileTwo.isDirectory())
					return fileOne.isDirectory() ? -1 : 1;
				else
					return fileOne.name().compareTo(fileTwo.name());
			}
		});

		for (int i = 0; i < files.size; i++) {
			final FileHandle fileHandle = files.get(i);
			AssetPreviewPanel panel = null;
			if (fileHandle.isDirectory()) {
				panel = new FolderPreviewPanel(fileHandle.name());
			} else if (fileHandle.extension().equals("png")) {
				panel = new SpritePreviewPanel(fileHandle.name());
			}
			panel.addListener(new ClickListener() {
				
				public void clicked (InputEvent event, float x, float y) {
					AssetViewer.this.setSelected(fileHandle.name());
				}
				
			});
			group.addActor(panel.pad(5));
			previewPanels.put(fileHandle.name(), panel);
		}
	}

	public void setSelected (String name) {
		previewPanels.get(name).select();
		if (lastSelected != null) lastSelected.unselect();
		lastSelected = previewPanels.get(name);
	}

}
