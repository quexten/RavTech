
package com.ravelsoftware.ravtech.dk;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ObjectMap;
import com.ravelsoftware.ravtech.RavTech;

public class AssetFileWatcher {

	private static WatchService watchService;
	private static Thread watchThread;
	private static FileHandle rootDirectory;
	private static ObjectMap<WatchKey, String> watchKeys = new ObjectMap<WatchKey, String>();

	public synchronized static String getRootPath () {
		return rootDirectory.path();
	}

	public static void set (FileHandle rootDirectory) {
		AssetFileWatcher.rootDirectory = rootDirectory;

		try {
			watchService = FileSystems.getDefault().newWatchService();
		} catch (IOException e) {
			e.printStackTrace();
		}
		watchThread = new Thread(new Runnable() {
			@Override
			public void run () {
				AssetFileWatcher.registerWatchServices();
				while (true) {
					try {
						WatchKey watchKey = watchService.take();
						List<WatchEvent<?>> pollEvents = watchKey.pollEvents();
						for (WatchEvent<?> event : pollEvents) {
							String rootPath = getRootPath();
							String fileName = event.context().toString();
							String directory = watchKeys.get(watchKey);
							String relativeDirectory = directory.replace('\\', '/').replaceAll(rootPath, "");
							relativeDirectory = relativeDirectory.length() < 8 ? "" : relativeDirectory.substring(8);
							final String assetPath = (relativeDirectory.isEmpty() ? fileName : (relativeDirectory + "/" + fileName));
							if (!fileName.isEmpty()) {
								Gdx.app.postRunnable(new Runnable() {
									@Override
									public void run () {
										if(!assetPath.endsWith(".scene"))
											if (RavTech.files.isLoaded(assetPath)) 
												RavTech.files.reloadAsset(assetPath);
										RavTechDK.assetViewer.assetView.refresh();
									}
								});
							}
						}
						watchKey.reset();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		watchThread.start();
	}

	private static synchronized void registerWatchServices () {
		try {
			Files.walkFileTree(rootDirectory.file().toPath(), new FileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory (Path dir, BasicFileAttributes attributes) throws IOException {
					watchKeys.put(dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
						StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY), dir.toString());
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile (Path file, BasicFileAttributes attributes) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed (Path file, IOException ex) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory (Path dir, IOException ex) throws IOException {
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (Exception ex) {
		}
	}

}
