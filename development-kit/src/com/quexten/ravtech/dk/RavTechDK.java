
package com.quexten.ravtech.dk;

import java.io.File;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3FileHandle;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.quexten.ravtech.Hook;
import com.quexten.ravtech.HookApi;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.Scene;
import com.quexten.ravtech.dk.ui.editor.EditorMenuBar;
import com.quexten.ravtech.dk.ui.editor.Inspector;
import com.quexten.ravtech.dk.ui.editor.RavWindow;
import com.quexten.ravtech.dk.ui.editor.SceneViewWidget;
import com.quexten.ravtech.dk.ui.editor.assetview.AssetViewer;
import com.quexten.ravtech.dk.ui.utils.UpdateManager;
import com.quexten.ravtech.dk.zerobrane.ZeroBraneUtil;
import com.quexten.ravtech.project.Project;
import com.quexten.ravtech.util.Debug;
import com.quexten.ravtech.util.ResourceFileHandleResolver;

public class RavTechDK {

	public static String getVersionString () {
		return "V " + RavTech.majorVersion + "." + RavTech.minorVersion + "." + RavTech.microVersion;
	}

	public static FileHandle projectHandle;
	public static AssetManager editorAssetManager = new AssetManager(new ResourceFileHandleResolver());	
	public static AssetViewer assetViewer;
	public static SceneViewWidget mainSceneView;
	
	public static void initialize () {	
		Gdx.input.setInputProcessor(RavTech.ui.getStage());
		
		Table root = new Table();
		root.setFillParent(true);		
		RavTech.ui.getStage().addActor(root);
		
		mainSceneView = new SceneViewWidget(true);		
		root.add(new EditorMenuBar(mainSceneView).getTable()).growX().row();
		root.row();		
		root.add(mainSceneView).expand().fill();		
						
		RavTech.ui.getStage().addActor(mainSceneView.inspector = new Inspector());
		mainSceneView.inspector.view = mainSceneView;

		UpdateManager.loadCurrentVersions();
		UpdateManager.checkForUpdates();
		ZeroBraneUtil.initialize();
		
		mainSceneView.camera.drawGrid = true;	
		
		
		HookApi.addHook("onResize", new Hook() {
			@Override
			public void run () {
				mainSceneView.resize();
				RavTech.ui.getStage().getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
			}
		});	
	}

	public static void setProject (final String projectRootPath) {
		Debug.log("setProject", projectRootPath);
		RavTech.project = Project.load(projectHandle = new Lwjgl3FileHandle(new File(projectRootPath), FileType.Absolute));
		RavTech.files.setResolver(new AbsoluteFileHandleResolver() {
			@Override
			public FileHandle resolve (String fileName) {
				fileName = fileName.replace('\\', '/');
				String formattedWorkingDir = projectHandle.child("assets").path();
				String resolver = fileName.startsWith(formattedWorkingDir) ? fileName : formattedWorkingDir + "/" + fileName;
				return Gdx.files.absolute(resolver);
			}
		});

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run () {
				RavTech.settings.setValue("RavTech.project.path", projectRootPath);
				RavTech.settings.save();
			}
		});
		assetViewer = new AssetViewer(mainSceneView.inspector);
		AssetFileWatcher.set(RavTech.files.getAssetHandle("").parent());
		RavWindow window = new RavWindow("AssetView", false);
		window.add(assetViewer).grow();
		window.setResizable(true);
		window.setSize(300, 300);
		window.setPosition(2000, 0);
		RavTech.ui.getStage().addActor(window);
		loadScene(RavTech.project.startScene);
		RavTech.input.reload();
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
		textureHandle.child("error.png")
			.write(new Lwjgl3FileHandle(new File("resources/ui/icons/error.png"), FileType.Local).read(), false);
		project.save(projectHandle);
		assetHandle.child("keybindings.json").writeString("{}", false);
	}

	public static FileHandle getLocalFile (String path) {
		return Gdx.files.absolute(System.getProperty("user.dir") + "/" + path);
	}

	public static FileHandle getDownloadsFile (String name) {
		return getPluginsFile("downloads").child(name);
	}

	public static FileHandle getPluginsFile (String name) {
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
		if (RavTech.files.isLoaded(RavTechDK.getCurrentScene()))
			RavTech.files.getAssetManager().unload(RavTechDK.getCurrentScene());
		RavTech.files.loadAsset(path, Scene.class);
		RavTech.files.finishLoading();
		RavTech.currentScene.dispose();
		RavTech.currentScene = RavTech.files.getAsset(path);
	}
	
	@SuppressWarnings("unchecked")
	public static Lwjgl3Window getWindow () {
		com.badlogic.gdx.utils.reflect.Field field;
		try {
			field = ClassReflection.getDeclaredField(Lwjgl3Application.class, "windows");
			field.setAccessible(true);
			Array<Lwjgl3Window> windows = (Array<Lwjgl3Window>)field.get(Gdx.app);
			return windows.first();
		} catch (ReflectionException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
