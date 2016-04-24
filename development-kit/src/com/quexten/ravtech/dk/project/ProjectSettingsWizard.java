
package com.quexten.ravtech.dk.project;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.Mode;
import com.kotcrab.vis.ui.widget.file.FileChooser.SelectionMode;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.quexten.ravtech.dk.RavTechDK;
import com.quexten.ravtech.dk.ui.editor.LabelTextPair;
import com.quexten.ravtech.project.Project;

public class ProjectSettingsWizard extends VisWindow {

	VisTable table;
	Project project;
	ObjectMap<String, VisTextField> fieldMap = new ObjectMap<String, VisTextField>();
	boolean isCreation;
	public String creationPath = "";

	public ProjectSettingsWizard (final Project project,
		boolean isCreation) {
		super("Project Settings");
		this.project = project;
		this.isCreation = isCreation;
		setSize(330, 330);
		addCloseButton();
		System.out.println("project: " + project);

		top();
		if (isCreation) {
			add(new VisLabel("Path:"));
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
					fileChooser.setSize(330, 330);
					fileChooser
						.setSelectionMode(SelectionMode.DIRECTORIES);
					event.getListenerActor().getStage()
						.addActor(fileChooser);
				}
			});
			add(textField);
			row();
		}
		addTextLabelPair("Developer Name:", project.developerName);
		addTextLabelPair("App Name:", project.appName);
		addTextLabelPair("BuildVersion:",
			String.valueOf(project.buildVersion));
		addTextLabelPair("MajorVersion:",
			String.valueOf(project.majorVersion));
		addTextLabelPair("MinorVersion:",
			String.valueOf(project.minorVersion));
		addTextLabelPair("MicroVersion:",
			String.valueOf(project.microVersion));
		addTextLabelPair("Package:", String.valueOf(project.appId));
		addTextLabelPair("StartScene:", project.startScene);
		VisTextButton button = new VisTextButton("Save");
		button.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				project.developerName = fieldMap.get("Developer Name:")
					.getText();
				project.appName = fieldMap.get("App Name:").getText();
				project.buildVersion = Integer
					.valueOf(fieldMap.get("BuildVersion:").getText());
				project.majorVersion = Integer
					.valueOf(fieldMap.get("MajorVersion:").getText());
				project.minorVersion = Integer
					.valueOf(fieldMap.get("MinorVersion:").getText());
				project.microVersion = Integer
					.valueOf(fieldMap.get("MicroVersion:").getText());
				project.appId = fieldMap.get("Package:").getText();
				project.startScene = fieldMap.get("StartScene:")
					.getText();
				if (ProjectSettingsWizard.this.isCreation) {
					RavTechDK.createProject(creationPath, project);
					RavTechDK.setProject(creationPath);
					RavTechDK.loadScene(project.startScene);
				} else
					project.save(RavTechDK.projectHandle);

			}
		});
		add();
		add(button);
		row();
		setModal(true);
	}

	void addTextLabelPair (String name, String initialValue) {
		LabelTextPair label = new LabelTextPair(name, initialValue);
		add(label.label).expandX();
		add(label.pairedComponent).expandX();
		row();
		fieldMap.put(name, (VisTextField)label.pairedComponent);
	}

}
