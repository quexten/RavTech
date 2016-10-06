
package com.quexten.ravtech.dk;

import java.io.File;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3FileHandle;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Preferences;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.kotcrab.vis.ui.VisUI;
import com.quexten.ravtech.Hook;
import com.quexten.ravtech.HookApi;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.Scene;
import com.quexten.ravtech.components.GameObject;
import com.quexten.ravtech.components.gizmos.GizmoHandler;
import com.quexten.ravtech.dk.adb.AdbManager;
import com.quexten.ravtech.dk.packaging.Packager;
import com.quexten.ravtech.dk.packaging.platforms.AndroidPlatform;
import com.quexten.ravtech.dk.packaging.platforms.DesktopPlatform;
import com.quexten.ravtech.dk.project.ProjectSettingsWizard;
import com.quexten.ravtech.dk.ui.editor.EditorMenuBar;
import com.quexten.ravtech.dk.ui.editor.Inspector;
import com.quexten.ravtech.dk.ui.editor.RavWindow;
import com.quexten.ravtech.dk.ui.editor.SceneViewWidget;
import com.quexten.ravtech.dk.ui.editor.UpdaterWidget;
import com.quexten.ravtech.dk.ui.editor.assetview.AssetViewer;
import com.quexten.ravtech.dk.ui.utils.UpdateManager;
import com.quexten.ravtech.dk.zerobrane.ZeroBraneUtil;
import com.quexten.ravtech.graphics.RavCamera;
import com.quexten.ravtech.net.Packet;
import com.quexten.ravtech.net.PacketProcessor;
import com.quexten.ravtech.net.Player;
import com.quexten.ravtech.project.Project;
import com.quexten.ravtech.remoteedit.FileHasher;
import com.quexten.ravtech.util.Debug;
import com.quexten.ravtech.util.FileUtil;
import com.quexten.ravtech.util.ResourceFileHandleResolver;
import com.quexten.ravtech.util.ZipUtil;

public class RavTechDK {

	public static String getVersionString () {
		return "V " + RavTech.majorVersion + "." + RavTech.minorVersion + "." + RavTech.microVersion;
	}

	public enum EditingMode {
		Move, Rotate, Scale, Other
	};

	private static EditingMode currentEditingMode = EditingMode.Move;

	public static RavCamera editorCamera;
	public static Project project;
	public static FileHandle projectHandle;
	public static GizmoHandler gizmoHandler;
	public static AssetManager editorAssetManager = new AssetManager(new ResourceFileHandleResolver());
	public static Inspector inspector;
	public static SceneViewWidget mainSceneView;
	public static AssetViewer assetViewer;
	public static UpdaterWidget updateWidget;

	public static Array<GameObject> selectedObjects = new Array<GameObject>();

	public static int windowWidth;
	public static int windowHeight;

	public static void initialize () {
		RavCamera.camId--;
		Packager.registerPlatform("Desktop", new DesktopPlatform());
		Packager.registerPlatform("Android", new AndroidPlatform());

		final Table root = new Table();
		root.setFillParent(true);
		RavTech.ui.getStage().addActor(root);
		EditorMenuBar menuBar = new EditorMenuBar();
		root.add(menuBar.getTable()).expandX().fillX().row();
		root.row();
		mainSceneView = new SceneViewWidget(true);
		root.add(mainSceneView).expand().fill();
		Gdx.input.setInputProcessor(RavTech.ui.getStage());
		
		HookApi.onResizeHooks.add(new Hook() {

			@Override
			public void run () {
				mainSceneView.resize();
			}

		});

		RavTech.ui.getStage().addActor(inspector = new Inspector());
		RavTechDK.gizmoHandler = new GizmoHandler(mainSceneView.camera);

		UpdateManager.loadCurrentVersions();
		ZeroBraneUtil.initialize();

		UpdateManager.checkForUpdates();
		RavTechDK.editorAssetManager.load("fonts/OpenSansBold.fnt", BitmapFont.class);
		RavTechDK.editorAssetManager.finishLoading();
		RavTech.ui.getStage().addActor(updateWidget = new UpdaterWidget());
		

		

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

		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run () {
				RavTech.settings.setValue("RavTechDK.project.path", projectRootPath);
				RavTech.settings.save();
			}
		});
		assetViewer = new AssetViewer();
		AssetFileWatcher.set(RavTechDK.projectHandle);
		RavWindow window = new RavWindow("AssetView", false);
		window.add(assetViewer).grow();
		window.setResizable(true);
		window.setSize(300, 300);
		window.setPosition(2000, 0);
		RavTech.ui.getStage().addActor(window);
		loadScene(project.startScene);
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

	/** Sets the currently selected objects
	 * @param objects - the objects that have been selected */
	public static void setSelectedObjects (Array<GameObject> objects) {
		Array<GameObject> componentCountList = new Array<GameObject>();
		for (int i = 0; i < selectedObjects.size; i++)
			if (!componentCountList.contains(selectedObjects.get(i), true))
				componentCountList.add(selectedObjects.get(i));
		int lastObjectCount = componentCountList.size;
		selectedObjects.clear();
		selectedObjects.addAll(objects);
		if (lastObjectCount != selectedObjects.size)
			RavTechDK.inspector.changed();
		RavTechDK.gizmoHandler.setupGizmos();
	}

	public static void setSelectedObjects (GameObject... objects) {
		selectedObjects.clear();
		selectedObjects.addAll(objects);
		inspector.changed();
		gizmoHandler.setupGizmos();
	}

	public static void setEditingMode (EditingMode editingMode) {
		currentEditingMode = editingMode;
	}

	public static EditingMode getEditingMode () {
		return currentEditingMode;
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
