
package com.ravelsoftware.ravtech.dk;

import java.io.File;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3FileHandle;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.ravelsoftware.ravtech.HookApi;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.Scene;
import com.ravelsoftware.ravtech.components.GameObject;
import com.ravelsoftware.ravtech.components.gizmos.GizmoHandler;
import com.ravelsoftware.ravtech.dk.ui.editor.EditorMenuBar;
import com.ravelsoftware.ravtech.dk.ui.editor.Inspector;
import com.ravelsoftware.ravtech.dk.ui.editor.SceneViewWidget;
import com.ravelsoftware.ravtech.dk.ui.editor.UpdaterWidget;
import com.ravelsoftware.ravtech.dk.ui.editor.assetview.AssetViewer;
import com.ravelsoftware.ravtech.dk.ui.utils.UpdateManager;
import com.ravelsoftware.ravtech.dk.zerobrane.ZeroBraneUtil;
import com.ravelsoftware.ravtech.project.Project;
import com.ravelsoftware.ravtech.util.Debug;
import com.ravelsoftware.ravtech.util.ResourceFileHandleResolver;

public class RavTechDK {

	public static final int majorVersion = 0;
	public static final int minorVersion = 2;
	public static final int microVersion = 0;
	
	public static String getVersionString() {
		return "V " + majorVersion + "." + minorVersion + "." + microVersion;
	}
	
	public static Project project;
	public static FileHandle projectHandle;
	public static GizmoHandler gizmoHandler;
	public static AssetManager editorAssetManager = new AssetManager(new ResourceFileHandleResolver());	
	public static Inspector inspector;
	public static SceneViewWidget mainSceneView;
	public static AssetViewer assetViewer;
	public static UpdaterWidget updateWidget;
	
	public static Array<GameObject> selectedObjects = new Array<GameObject>();
	
	public static void initialize() {

		final Table root = new Table();
		root.setFillParent(true);
		RavTech.ui.getStage().addActor(root);
		EditorMenuBar menuBar = new EditorMenuBar();
		root.add(menuBar.getTable()).expandX().fillX().row();
		root.row();
		mainSceneView = new SceneViewWidget(true);
		root.add(mainSceneView).expand().fill();
		Gdx.input.setInputProcessor(RavTech.ui.getStage());

		HookApi.onResizeHooks.add(new Runnable() {

			@Override
			public void run () {
				mainSceneView.resize();
			}

		});

		RavTech.ui.getStage().addActor(inspector = new Inspector());
		RavTechDK.gizmoHandler = new GizmoHandler();

		UpdateManager.loadCurrentVersions();
		ZeroBraneUtil.initialize();
		
		UpdateManager.checkForUpdates();
		RavTechDK.editorAssetManager.load("fonts/OpenSansBold.fnt", BitmapFont.class);
		RavTechDK.editorAssetManager.finishLoading();
		RavTech.ui.getStage().addActor(updateWidget = new UpdaterWidget());
	}
	
	public static void setProject (final String projectRootPath) {
		project = Project.load(projectHandle = new Lwjgl3FileHandle(new File(projectRootPath), FileType.Absolute));
		RavTech.files.setResolver(new AbsoluteFileHandleResolver() {
			@Override
			public FileHandle resolve (String fileName) {
				fileName = fileName.replace('\\', '/');
				String formattedWorkingDir = RavTechDK.projectHandle.child("assets").path();
				String resolver = fileName.startsWith(formattedWorkingDir) ? fileName : formattedWorkingDir + "/" + fileName;
				return Gdx.files.absolute(resolver);
			}
		});
		// ui.ravtechDKFrame.setTitle(ui.ravtechDKFrame.getFullTitle());
		// ui.ravtechDKFrame.view.setPath(projectRootPath);
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run () {
				RavTech.settings.setValue("RavTechDK.project.path", projectRootPath);
				RavTech.settings.save();
			}
		});
		assetViewer = new AssetViewer();
		AssetFileWatcher.set(RavTechDK.projectHandle);
		VisWindow window = new VisWindow("AssetView");
		window.add(assetViewer).grow();
		window.setResizable(true);
		window.setSize(1000, 300);
		window.setPosition(2000, 0);
		RavTech.ui.getStage().addActor(window);
	}

	/** Creates Project base directories and base files
	 * @param projectRootPath - the root path of the project
	 * @param project - the project */
	public static void createProject (String projectRootPath, Project project) {
		FileHandle projectHandle = new Lwjgl3FileHandle(new File(projectRootPath), FileType.Absolute);
		projectHandle.mkdirs();
		FileHandle assetHandle = projectHandle.child("assets");
		assetHandle.mkdirs();
		assetHandle.child("scenes");
		assetHandle.child("scripts").mkdirs();
		assetHandle.child("shaders").mkdirs();
		assetHandle.child("sounds").mkdirs();
		assetHandle.child("music").mkdirs();
		assetHandle.child(project.startScene).writeString(new Json().toJson(new Scene()), false);
		assetHandle.child("shaders").child("default.frag")
			.writeString(new Lwjgl3FileHandle(new File("resources/shaders/default.frag"), FileType.Local).readString(), false);
		assetHandle.child("shaders").child("default.vert")
			.writeString(new Lwjgl3FileHandle(new File("resources/shaders/default.vert"), FileType.Local).readString(), false);
		projectHandle.child("builds").mkdirs();
		projectHandle.child("icons").mkdirs();
		projectHandle.child("plugins").mkdirs();
		FileHandle textureHandle = assetHandle.child("textures");
		textureHandle.mkdirs();
		textureHandle.child("error.png").write(new Lwjgl3FileHandle(new File("resources/ui/icons/error.png"), FileType.Local).read(),
			false);
		project.save(projectHandle);
	}

	public static FileHandle getLocalFile (String path) {
		return Gdx.files.absolute((System.getProperty("user.dir") + "/" + path));
	}
	
	public static FileHandle getDownloadsFile(String name) {
		return getPluginsFile("downloads").child(name);
	}
	
	public static FileHandle getPluginsFile(String name) {
		return Gdx.files.absolute(System.getProperty("user.dir") + "/plugins/" + name);
	}
	
	public static String getSystemExecutableEnding () {
		return System.getProperty("os.name").toLowerCase().contains("windows") ? "exe" : "sh";
	}

	public static void saveScene (FileHandle file) {
		file.writeString(new Json().toJson(RavTech.currentScene), false);
	}

	public static String getCurrentScene () {
		return RavTech.files.getAssetManager().getAssetFileName(RavTech.currentScene);
	}

	public static void loadScene (String path) {
		Debug.log("Load", "[" + path + "]");
		if(RavTech.files.isLoaded(RavTechDK.getCurrentScene()))
		RavTech.files.getAssetManager().unload(RavTechDK.getCurrentScene());
		RavTech.files.loadAsset(path, Scene.class);
		RavTech.files.finishLoading();
		RavTech.currentScene.dispose();
		RavTech.currentScene = RavTech.files.getAsset(path);
	}
	
	/** Sets the currently selected objects
	 * @param objects - the objects that have been selected */
	public static void setSelectedObjects (Array<GameObject> objects) {
		Array<GameObject> componentCountList = new Array<GameObject>();
		for (int i = 0; i < selectedObjects.size; i++)
			if (!componentCountList.contains(selectedObjects.get(i), true)) componentCountList.add(selectedObjects.get(i));
		int lastObjectCount = componentCountList.size;
		selectedObjects.clear();
		selectedObjects.addAll(objects);
		if (lastObjectCount != selectedObjects.size) RavTechDK.inspector.changed();
		RavTechDK.gizmoHandler.setupGizmos();
	}

	public static void setSelectedObjects (GameObject... objects) {
		selectedObjects.clear();
		selectedObjects.addAll(objects);
		inspector.changed();
		gizmoHandler.setupGizmos();
	}
	
}
