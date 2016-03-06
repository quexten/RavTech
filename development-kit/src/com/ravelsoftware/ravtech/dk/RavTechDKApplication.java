
package com.ravelsoftware.ravtech.dk;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.ravelsoftware.ravtech.HookApi;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.components.gizmos.GizmoHandler;
import com.ravelsoftware.ravtech.dk.project.ProjectSettingsWizard;
import com.ravelsoftware.ravtech.dk.ui.editor.EditorMenuBar;
import com.ravelsoftware.ravtech.dk.ui.editor.Inspector;
import com.ravelsoftware.ravtech.dk.ui.editor.SceneViewWidget;
import com.ravelsoftware.ravtech.dk.ui.editor.assetview.AssetViewer;
import com.ravelsoftware.ravtech.project.Project;

public class RavTechDKApplication extends RavTech {

	Stage stage;
	public SceneViewWidget mainSceneView;

	public RavTechDKApplication (AbsoluteFileHandleResolver absoluteFileHandleResolver, Project project) {
		super(absoluteFileHandleResolver, project);
	}

	@Override
	public void create () {
		super.create();
		RavTech.sceneHandler.paused = true;
		if (!VisUI.isLoaded()) VisUI.load(Gdx.files.local("resources/ui/mdpi/uiskin.json"));
		stage = new Stage(new ScreenViewport());

		final Table root = new Table();
		root.setFillParent(true);
		stage.addActor(root);
		EditorMenuBar menuBar = new EditorMenuBar();
		root.add(menuBar.getTable()).expandX().fillX().row();
		root.row();
		mainSceneView = new SceneViewWidget(true);
		root.add(mainSceneView).expand().fill();
		Gdx.input.setInputProcessor(stage);

		HookApi.onResizeHooks.add(new Runnable() {

			@Override
			public void run () {
				mainSceneView.resize();
			}

		});

		stage.addActor(new Inspector());

		RavTechDK.gizmoHandler = new GizmoHandler();
		HookApi.onRenderHooks.add(new Runnable() {
			@Override
			public void run () {
				RavTech.shapeRenderer.begin();
				RavTech.shapeRenderer.setProjectionMatrix(RavTech.sceneHandler.worldCamera.combined);
				RavTechDK.gizmoHandler.render(RavTech.shapeRenderer);
				RavTech.shapeRenderer.end();
			}
		});

		if (RavTech.settings.getString("RavTechDK.project.path").isEmpty()
			|| !new Lwjgl3FileHandle(RavTech.settings.getString("RavTechDK.project.path"), FileType.Absolute).child("project.json")
				.exists()) {
			final Project project = new Project();
			final ProjectSettingsWizard wizard = new ProjectSettingsWizard(project, true);
			wizard.setSize(330, 330);
			stage.addActor(wizard);
		}

		final AssetViewer assetViewer = new AssetViewer();
		VisWindow window = new VisWindow("AssetView");
		window.add(assetViewer).grow();
		window.setResizable(true);
		window.setSize(1000, 300);
		window.setPosition(2000, 0);
		stage.addActor(window);
	}

	public void render () {
		stage.act();
		RavTech.sceneHandler.render();
		stage.draw();
	}

	public void resize (int width, int height) {
		stage.getViewport().update(width, height, true);
		stage.draw();
		super.resize(width, height);
	}

	public void addWindow (String title) {
		final VisWindow window = new VisWindow(title);
		final SceneViewWidget sceneView = new SceneViewWidget(false);
		window.add(sceneView).expand().fill();
		window.setSize(128 * 3, 72 * 3);
		window.setResizable(true);
		window.addListener(new ClickListener() {

			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				super.touchUp(event, x, y, pointer, button);
				sceneView.setResolution((int)sceneView.getWidth(), (int)sceneView.getHeight());
				sceneView.camera.setToOrtho(false, sceneView.getWidth(), sceneView.getHeight());
			}

		});
		stage.addActor(window);
	}
}
