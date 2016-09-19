
package com.quexten.ravtech.dk;

import java.io.File;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3FileHandle;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.quexten.ravtech.EngineConfiguration;
import com.quexten.ravtech.Hook;
import com.quexten.ravtech.HookApi;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.dk.adb.AdbManager;
import com.quexten.ravtech.dk.project.ProjectSettingsWizard;
import com.quexten.ravtech.dk.ui.editor.RavWindow;
import com.quexten.ravtech.dk.ui.editor.SceneViewWidget;
import com.quexten.ravtech.net.Packet;
import com.quexten.ravtech.net.PacketProcessor;
import com.quexten.ravtech.net.Player;
import com.quexten.ravtech.project.Project;
import com.quexten.ravtech.remoteedit.FileHasher;
import com.quexten.ravtech.util.Debug;
import com.quexten.ravtech.util.FileUtil;
import com.quexten.ravtech.util.ZipUtil;

public class RavTechDKApplication extends RavTech {

	public float step = 1f / 60f;
	public float accumulator = 0;
	
	public RavTechDKApplication () {
		super(new InternalFileHandleResolver(), new Project(), new EngineConfiguration());
	}

	@Override
	public void create () {
		super.create();		
		
		AdbManager.initializeAdb();

		RavTech.sceneHandler.paused = true;
		if (!VisUI.isLoaded()) VisUI.load(Gdx.files.local("resources/ui/mdpi/uiskin.json"));

		RavTechDK.initialize();

		if (RavTech.settings.getString("RavTechDK.project.path").isEmpty()
			|| !new Lwjgl3FileHandle(RavTech.settings.getString("RavTechDK.project.path"), FileType.Absolute).child("project.json")
				.exists()) {
			final Project project = new Project();
			final ProjectSettingsWizard wizard = new ProjectSettingsWizard(project, true);
			wizard.setSize(330, 330);
			RavTech.ui.getStage().addActor(wizard);
		} else {
			final Preferences preferences = new Lwjgl3Preferences(
				new Lwjgl3FileHandle(new File(".prefs/", "RavTech"), FileType.External));
			RavTechDK.setProject(preferences.getString("RavTechDK.project.path"));
		}

		RavTechDK.mainSceneView.camera.drawGrid = true;

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run () {
				RavTech.net.addProcessor(new PacketProcessor() {
					@Override
					public boolean process (Player player, Packet packet) {
						if (packet instanceof Packet.AssetRequest) {							
							Packet.AssetRequest assetRequest = ((Packet.AssetRequest)packet);

							Array<FileHandle> files = FileUtil.getChildrenFiles(RavTechDK.projectHandle.child("assets"));
							Array<FileHandle> removeHandles = new Array<FileHandle>();
							
							for (int i = 0; i < files.size; i++) {
								FileHandle file = files.get(i);
								String path = file.path().replace(RavTechDK.projectHandle.child("assets") + "/", "").replace('\\', '/');
								if (assetRequest.fileHashes.containsKey(path)
									&& assetRequest.fileHashes.get(path).equals(FileHasher.getHash(file))) {
									Debug.logError("REMOVE", path);
									removeHandles.add(file);
								}
							}
							
							files.removeAll(removeHandles, true);
							
							for(int i = 0; i < files.size; i++) {
								Debug.log("File", files.get(i));
							}
							Debug.log("Files length", files.size);

							
							Array<String> stringHandles = new Array<String>();
							for(int i = 0; i < files.size; i++) {
								stringHandles.add(files.get(i).path().replace(RavTechDK.projectHandle.child("assets") + "/", "").replace('\\', '/'));
								Debug.log("StringHandle", stringHandles.get(i));
							}
							
							//stringHandles.add("project.json");
							RavTechDK.project.save(RavTechDK.projectHandle.child("assets"));
							new ZipUtil().zipFolder(RavTechDK.projectHandle.child("assets").path(),
								RavTechDK.getLocalFile("temp/build.ravpack").path(), stringHandles);
							Gdx.app.postRunnable(new Runnable() {
								@Override
								public void run() {
									RavTechDK.projectHandle.child("assets").child("project.json").delete();
								}
							});
						}
						return false;
					}
				});
			}
		});
	}

	@Override
	public void render () {
		input.update();
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

			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				super.touchUp(event, x, y, pointer, button);
				sceneView.setResolution((int)sceneView.getWidth(), (int)sceneView.getHeight());
				sceneView.camera.setToOrtho(false, sceneView.getWidth(), sceneView.getHeight());
			}

		});
		RavTech.ui.getStage().addActor(window);
	}

}
