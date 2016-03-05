
package com.ravelsoftware.ravtech.dk.ui.editor;

import java.util.Comparator;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.layout.HorizontalFlowGroup;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.ravelsoftware.ravtech.dk.RavTechDK;

public class AssetViewer extends VisTable {

	AssetView assetView;
	VisTextButton upButton;

	public AssetViewer () {
		VisTable headerTable = new VisTable();
		upButton = new VisTextButton("Up");
		headerTable.left();
		headerTable.add(upButton).left();

		upButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				assetView.moveUp();
			}
		});
		upButton.setFocusBorderEnabled(false);

		add(headerTable).growX();
		row();

		add(assetView = new AssetView()).grow();
	}

	class AssetView extends VisTable {

		ObjectMap<String, AssetPreviewPanel> previewPanels = new ObjectMap<String, AssetPreviewPanel>();
		AssetPreviewPanel lastSelected;
		FileHandle folderHandle;

		public AssetView () {
			setDirectory("");
		}

		public void setDirectory (String path) {

			com.ravelsoftware.ravtech.util.Debug.log("path", path);
			clear();
			HorizontalFlowGroup group = new HorizontalFlowGroup();
			add(group).grow();

			folderHandle = RavTechDK.projectHandle.child("assets").child(path);
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
					panel = new SpritePreviewPanel(
						fileHandle.path().substring((RavTechDK.projectHandle.path() + "/assets/").length()));
				}
				panel.addListener(new ClickListener() {

					public void clicked (InputEvent event, float x, float y) {
						AssetView.this.setSelected(fileHandle.name());
						if (this.getTapCount() > 1) {
							if (fileHandle.isDirectory())
								AssetView.this
									.setDirectory(fileHandle.path().substring((RavTechDK.projectHandle.path() + "/assets/").length()));
							else {

							}
						}
					}

				});
				group.addActor(panel.pad(5));
				previewPanels.put(fileHandle.name(), panel);
			}

			upButton.setDisabled(folderHandle.path().equals(RavTechDK.projectHandle.child("assets").path()));
		}

		public void setSelected (String name) {
			previewPanels.get(name).select();
			if (lastSelected != null) lastSelected.unselect();
			lastSelected = previewPanels.get(name);
		}

		public void moveUp () {
			this
				.setDirectory((folderHandle.parent().path() + "/").substring((RavTechDK.projectHandle.path() + "/assets/").length()));
		}
	}

}
