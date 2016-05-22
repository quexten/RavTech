
package com.quexten.ravtech.dk;

import java.io.File;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3FileHandle;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Preferences;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;
import com.quexten.ravtech.EngineConfiguration;
import com.quexten.ravtech.HookApi;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.dk.adb.AdbManager;
import com.quexten.ravtech.dk.project.ProjectSettingsWizard;
import com.quexten.ravtech.dk.ui.editor.RavWindow;
import com.quexten.ravtech.dk.ui.editor.SceneViewWidget;
import com.quexten.ravtech.project.Project;

public class RavTechDKApplication extends RavTech {

	public float step = 1f / 60f;
	public float accumulator = 0;

	public RavTechDKApplication () {
		super(new InternalFileHandleResolver(), new Project(),
			new EngineConfiguration());
	}

	@Override
	public void create () {
		super.create();

		AdbManager.initializeAdb();

		RavTech.sceneHandler.paused = true;
		if (!VisUI.isLoaded())
			VisUI.load(Gdx.files.local("resources/ui/mdpi/uiskin.json"));

		RavTechDK.initialize();
		HookApi.onRenderHooks.add(new Runnable() {
			@Override
			public void run () {
				RavTechDK.gizmoHandler.render();
			}
		});

		if (RavTech.settings.getString("RavTechDK.project.path")
			.isEmpty()
			|| !new Lwjgl3FileHandle(
				RavTech.settings.getString("RavTechDK.project.path"),
				FileType.Absolute).child("project.json").exists()) {
			final Project project = new Project();
			final ProjectSettingsWizard wizard = new ProjectSettingsWizard(
				project, true);
			wizard.setSize(330, 330);
			RavTech.ui.getStage().addActor(wizard);
		} else {
			final Preferences preferences = new Lwjgl3Preferences(
				new Lwjgl3FileHandle(new File(".prefs/", "RavTech"),
					FileType.External));
			RavTechDK.setProject(
				preferences.getString("RavTechDK.project.path"));
		}

		RavTechDK.mainSceneView.camera.drawGrid = true;
	}

	@Override
	public void render () {
		accumulator += Gdx.graphics.getDeltaTime();
		while (accumulator > step) {
			accumulator -= step;
			RavTech.ui.getStage().act(step);
		}

		RavTech.sceneHandler.render();
		RavTech.ui.getStage().draw();
	}

	public void resize (int width, int height) {
		RavTech.ui.getStage().getViewport().update(width, height, true);
		RavTech.ui.getStage().draw();
		RavTechDK.windowWidth = width;
		RavTechDK.windowHeight = height;
		super.resize(width, height);
	}

	public void addWindow (String title) {
		final RavWindow window = new RavWindow(title);
		final SceneViewWidget sceneView = new SceneViewWidget(false);
		window.add(sceneView).expand().fill();
		window.setSize(128 * 3, 72 * 3);
		window.setResizable(true);
		window.addListener(new ClickListener() {

			public void touchUp (InputEvent event, float x, float y,
				int pointer, int button) {
				super.touchUp(event, x, y, pointer, button);
				sceneView.setResolution((int)sceneView.getWidth(),
					(int)sceneView.getHeight());
				sceneView.camera.setToOrtho(false, sceneView.getWidth(),
					sceneView.getHeight());
			}

		});
		RavTech.ui.getStage().addActor(window);
	}

}
