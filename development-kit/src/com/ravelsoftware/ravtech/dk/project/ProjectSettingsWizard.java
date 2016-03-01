
package com.ravelsoftware.ravtech.dk.project;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.Mode;
import com.kotcrab.vis.ui.widget.file.FileChooser.SelectionMode;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.ravelsoftware.ravtech.dk.RavTechDK;
import com.ravelsoftware.ravtech.dk.ui.editor.LabelTextPair;
import com.ravelsoftware.ravtech.project.Project;

public class ProjectSettingsWizard implements ApplicationListener {

	Stage stage;
	VisTable table;
	Project project;
	ObjectMap<String, VisTextField> fieldMap = new ObjectMap<String, VisTextField>();
	boolean isCreation;
	String creationPath = "";

	public ProjectSettingsWizard (Project project, boolean isCreation) {
		this.project = project;
		this.isCreation = isCreation;
	}

	@Override
	public void create () {
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		table = new VisTable();
		table.top();
		table.setFillParent(true);
		table.setBackground(VisUI.getSkin().getDrawable("window"));
		if (isCreation) {
			table.add(new VisLabel("Path:"));
			final VisTextField textField = new VisTextField("");
			textField.setDisabled(true);
			textField.addListener(new ClickListener() {
				public void clicked (InputEvent event, float x, float y) {
					FileChooser fileChooser = new FileChooser(Mode.SAVE);
					fileChooser.setListener(new FileChooserAdapter() {
						@Override
						public void selected (Array<FileHandle> file) {
							textField.setText(file.get(0).path());
							creationPath = file.get(0).path();
						}
					});
					fileChooser.setDirectory(RavTechDK.projectHandle.child("assets"));
					fileChooser.setSize(330, 330);
					fileChooser.setSelectionMode(SelectionMode.DIRECTORIES);
					event.getListenerActor().getStage().addActor(fileChooser);
				}
			});
			table.add(textField);
			table.row();
		}
		addTextLabelPair("Developer Name:", project.developerName);
		addTextLabelPair("App Name:", project.appName);
		addTextLabelPair("BuildVersion:", String.valueOf(project.buildVersion));
		addTextLabelPair("MajorVersion:", String.valueOf(project.majorVersion));
		addTextLabelPair("MinorVersion:", String.valueOf(project.minorVersion));
		addTextLabelPair("MicroVersion:", String.valueOf(project.microVersion));
		addTextLabelPair("StartScene:", project.startScene);
		VisTextButton button = new VisTextButton("Save");
		button.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				project.developerName = fieldMap.get("Developer Name:").getText();
				project.appName = fieldMap.get("App Name:").getText();
				project.buildVersion = Integer.valueOf(fieldMap.get("BuildVersion:").getText());
				project.majorVersion = Integer.valueOf(fieldMap.get("MajorVersion:").getText());
				project.minorVersion = Integer.valueOf(fieldMap.get("MinorVersion:").getText());
				project.microVersion = Integer.valueOf(fieldMap.get("MicroVersion:").getText());
				project.startScene = fieldMap.get("StartScene:").getText();
				if (ProjectSettingsWizard.this.isCreation)
					RavTechDK.createProject(creationPath, project);
				else
					project.save(RavTechDK.projectHandle);
			}
		});
		table.add();
		table.add(button);
		table.row();
		stage.addActor(table);
	}

	@Override
	public void render () {
		stage.act();
		stage.draw();
	}

	@Override
	public void resize (int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void pause () {
	}

	@Override
	public void resume () {
	}

	@Override
	public void dispose () {
		stage.dispose();
	}

	void addTextLabelPair (String name, String initialValue) {
		LabelTextPair label = new LabelTextPair(name, initialValue);
		table.add(label.label).expandX();
		table.add(label.pairedComponent).expandX();
		table.row();
		fieldMap.put(name, (VisTextField)label.pairedComponent);
	}

}
