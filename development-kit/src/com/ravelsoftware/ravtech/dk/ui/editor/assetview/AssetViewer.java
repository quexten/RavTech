
package com.ravelsoftware.ravtech.dk.ui.editor.assetview;

import java.io.File;
import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.layout.HorizontalFlowGroup;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.ravelsoftware.ravtech.dk.RavTechDK;
import com.ravelsoftware.ravtech.dk.zerobrane.ZeroBraneUtil;

public class AssetViewer extends VisTable {

	public AssetView assetView;
	VisTextButton upButton;
	public DragAndDrop dragAndDrop;

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

	public class AssetView extends VisTable {

		ObjectMap<String, AssetPreviewPanel> previewPanels = new ObjectMap<String, AssetPreviewPanel>();
		AssetPreviewPanel lastSelected;
		FileHandle folderHandle;
		String currentPath;

		public AssetView () {
			setDirectory("");
		}

		public void setDirectory (String path) {
			RavTechDK.inspector.changed();
			currentPath = path;
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

			dragAndDrop = new DragAndDrop();
			dragAndDrop.setDragActorPosition(0, 0);
			dragAndDrop.addTarget(new Target(RavTechDK.mainSceneView) {
				@Override
				public boolean drag (Source source, Payload payload, float x, float y, int pointer) {
					return true;
				}

				@Override
				public void drop (Source source, Payload payload, float x, float y, int pointer) {
					((AssetPreviewPanel)payload.getDragActor()).addToScene();
				}
			});

			Array<Actor> inspectorActors = RavTechDK.inspector.dragActors;
			for (int i = 0; i < inspectorActors.size; i++)
				dragAndDrop.addTarget(new Target(inspectorActors.get(i)) {

					@Override
					public boolean drag (Source source, Payload payload, float x, float y, int pointer) {
						return true;
					}

					@Override
					public void drop (Source source, Payload payload, float x, float y, int pointer) {
					}

				});

			for (int i = 0; i < files.size; i++) {
				final FileHandle fileHandle = files.get(i);
				final String filePath = fileHandle.path();
				AssetPreviewPanel panel = getPreviewPanelFor(fileHandle);
				if (panel != null) {
					panel.addListener(new ClickListener() {

						public void clicked (InputEvent event, float x, float y) {
							AssetView.this.setSelected(fileHandle.name());
							if (getTapCount() > 1)
								if (fileHandle.isDirectory())
									AssetView.this
										.setDirectory(fileHandle.path().substring((RavTechDK.projectHandle.path() + "/assets/").length()));
								else {

								}
						}

					});

					dragAndDrop.addSource(new Source(panel) {
						public Payload dragStart (InputEvent event, float x, float y, int pointer) {
							Payload payload = new Payload();
							payload.setDragActor(getDragPanel(filePath, event, Color.CORAL.cpy().mul(1, 1, 1, 0.5f)));
							payload.setValidDragActor(getDragPanel(filePath, event, Color.GREEN.cpy().mul(1, 1, 1, 0.5f)));
							payload.setInvalidDragActor(getDragPanel(filePath, event, Color.RED.cpy().mul(1, 1, 1, 0.5f)));
							payload.setObject(filePath);
							return payload;
						}
					});

					if (fileHandle.isDirectory())
						dragAndDrop.addTarget(new Target(panel) {
							@Override
							public boolean drag (Source source, Payload payload, float x, float y, int pointer) {
								return true;
							}

							@Override
							public void drop (Source source, Payload payload, float x, float y, int pointer) {
								FileHandle srcHandle = Gdx.files.absolute(String.valueOf(payload.getObject()));
								FileHandle dstHandle = Gdx.files.absolute(filePath).child(srcHandle.name());
								srcHandle.moveTo(dstHandle);
								AssetView.this.refresh();
							}
						});

					group.addActor(panel.pad(5));
					previewPanels.put(fileHandle.name(), panel);
				}
			}

			upButton.setDisabled(folderHandle.path().equals(RavTechDK.projectHandle.child("assets").path()));
		}

		public void setSelected (String name) {
			previewPanels.get(name).select();
			if (lastSelected != null)
				lastSelected.unselect();
			lastSelected = previewPanels.get(name);
		}

		public void moveUp () {
			setDirectory((folderHandle.parent().path() + "/").substring((RavTechDK.projectHandle.path() + "/assets/").length()));
		}

		public void refresh () {
			setDirectory(currentPath);
		}

		AssetPreviewPanel getDragPanel (String filePath, InputEvent event, Color color) {
			AssetPreviewPanel previewPanel = getPreviewPanelFor(Gdx.files.absolute(filePath));
			previewPanel.setSelectionColor(color);
			previewPanel.select();
			previewPanel.setSize(event.getListenerActor().getWidth() - 10, event.getListenerActor().getHeight() - 10);
			return previewPanel;
		}

		AssetPreviewPanel getPreviewPanelFor (FileHandle fileHandle) {
			AssetPreviewPanel panel = null;
			final String filePath = fileHandle.path();
			String extension = fileHandle.extension();
			if (fileHandle.isDirectory())
				panel = new FolderPreviewPanel(fileHandle.name());
			else if (extension.equals("png") || extension.equals("jpg"))
				panel = new SpritePreviewPanel(fileHandle.path().substring((RavTechDK.projectHandle.path() + "/assets/").length()));
			else if (extension.equals("lua")) {
				panel = new LuaPreviewPanel(fileHandle.path().substring((RavTechDK.projectHandle.path() + "/assets/").length()));
				panel.addListener(new ClickListener() {
					@Override
					public void clicked (InputEvent event, float x, float y) {
						if (getTapCount() > 1)
							ZeroBraneUtil.openFile(new File(filePath));
					}
				});
			} else if (extension.equals("fnt"))
				panel = new FontPreviewPanel(fileHandle.path().substring((RavTechDK.projectHandle.path() + "/assets/").length()));
			return panel;
		}

	}

}
