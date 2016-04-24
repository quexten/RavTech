
package com.ravelsoftware.ravtech.dk;

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
import com.kotcrab.vis.ui.widget.VisWindow;
import com.ravelsoftware.ravtech.EngineConfiguration;
import com.ravelsoftware.ravtech.HookApi;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.dk.adb.AdbManager;
import com.ravelsoftware.ravtech.dk.project.ProjectSettingsWizard;
import com.ravelsoftware.ravtech.dk.ui.editor.SceneViewWidget;
import com.ravelsoftware.ravtech.project.Project;
import com.ravelsoftware.ravtech.util.Debug;

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
		super.resize(width, height);
	}

	public void addWindow (String title) {
		final VisWindow window = new VisWindow(title);
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
