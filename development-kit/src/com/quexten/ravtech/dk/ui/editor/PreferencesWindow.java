
package com.quexten.ravtech.dk.ui.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.Mode;
import com.kotcrab.vis.ui.widget.file.FileChooser.SelectionMode;
import com.kotcrab.vis.ui.widget.file.FileChooserListener;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.dk.adb.AdbManager;

public class PreferencesWindow extends RavWindow {

	public PreferencesWindow () {
		super("Preferences");
		this.top();
		this.add(new VisLabel("Android Sdk")).padLeft(5);
		final VisLabel fileLabel = new VisLabel("");
		this.add(fileLabel).growX();
		VisTextButton selectButton = new VisTextButton("Select");
		selectButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				FileChooser chooser = new FileChooser(Mode.OPEN);
				chooser.setSelectionMode(SelectionMode.DIRECTORIES);
				chooser.setListener(new FileChooserListener() {
					@Override
					public void selected (Array<FileHandle> files) {
						FileHandle handle = files.first();
						RavTech.settings.setValue("RavTechDK.android.sdk.dir", handle.path());
						RavTech.settings.save();
						AdbManager.initializeAdb();
						fileLabel.setText(handle.path());
					}

					@Override
					public void canceled () {
					}
				});
				chooser.setDirectory(Gdx.files.absolute(System.getProperty("user.dir")));
				actor.getStage().addActor(chooser);
				float width = PreferencesWindow.this.getWidth();
				float height = PreferencesWindow.this.getHeight();
				chooser.setSize((int)width, (int)height);
			}
		});
		this.add(selectButton);
		this.row();
		this.centerWindow();
	}

}
