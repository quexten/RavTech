
package com.quexten.ravtech.dk.ui.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuBar;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.Mode;
import com.kotcrab.vis.ui.widget.file.FileChooser.SelectionMode;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.Scene;
import com.quexten.ravtech.dk.RavTechDK;
import com.quexten.ravtech.dk.project.ProjectSettingsWizard;
import com.quexten.ravtech.dk.ui.packaging.BuildDialog;
import com.quexten.ravtech.history.ChangeManager;
import com.quexten.ravtech.history.CreateChangeable;
import com.quexten.ravtech.util.Debug;

public class EditorMenuBar extends MenuBar {

	public EditorMenuBar () {
		Menu menu;
		MenuItem entry;
		{ // File Menu
			menu = new Menu("File");
			{ // New Scene
				entry = new MenuItem("New Scene");
				entry.addListener(new ChangeListener() {

					@Override
					public void changed (ChangeEvent event, Actor actor) {
						RavTech.currentScene.dispose();
						RavTech.currentScene = new Scene();
					}
				});
				entry.setShortcut(Keys.CONTROL_LEFT, Keys.N);
				menu.addItem(entry);
			}
			{ // Save Scene
				entry = new MenuItem("Save");
				entry.addListener(new ChangeListener() {

					@Override
					public void changed (ChangeEvent event, Actor actor) {
						RavTech.files
							.getAssetHandle(RavTech.files.getAssetManager()
								.getAssetFileName(RavTech.currentScene))
							.writeString(
								new Json().toJson(RavTech.currentScene),
								false);
						Debug.log("Saved Scene",
							"[" + RavTech.files.getAssetManager()
								.getAssetFileName(RavTech.currentScene)
							+ "]");
					}
				});
				entry.setShortcut(Keys.CONTROL_LEFT, Keys.S);
				menu.addItem(entry);
			}
			{ // Save Scene
				entry = new MenuItem("Save As..");
				entry.addListener(new ChangeListener() {

					@Override
					public void changed (ChangeEvent event, Actor actor) {
						FileChooser fileChooser = new FileChooser(
							Mode.SAVE);
						fileChooser.setSelectionMode(SelectionMode.FILES);
						fileChooser.setListener(new FileChooserAdapter() {
							@Override
							public void selected (Array<FileHandle> file) {
								file.first().writeString(
									new Json().toJson(RavTech.currentScene),
									true);
								Debug.log("Saved Scene",
									"[" + RavTech.files.getAssetManager()
										.getAssetFileName(RavTech.currentScene)
									+ "]");
							}
						});
						fileChooser.setDirectory(
							RavTechDK.projectHandle.child("assets"));
						actor.getStage().addActor(fileChooser);
					}
				});
				entry.setShortcut(Keys.CONTROL_LEFT, Keys.SHIFT_LEFT,
					Keys.S);
				menu.addItem(entry);
			}
			{ // Load Scene
				entry = new MenuItem("Load Scene");
				entry.addListener(new ChangeListener() {

					@Override
					public void changed (ChangeEvent event, Actor actor) {

						FileChooser fileChooser = new FileChooser(
							Mode.OPEN);
						fileChooser.setSelectionMode(SelectionMode.FILES);
						fileChooser.setListener(new FileChooserAdapter() {
							@Override
							public void selected (Array<FileHandle> file) {
								String localScenePath = file.first().path()
									.replaceAll(RavTechDK.projectHandle
										.child("assets").path(), "")
									.substring(1);
								RavTechDK.loadScene(localScenePath);
							}
						});
						fileChooser.setDirectory(
							RavTechDK.projectHandle.child("assets"));
						actor.getStage().addActor(fileChooser);
					}
				});
				entry.setShortcut(Keys.CONTROL_LEFT, Keys.L);
				menu.addItem(entry);
			}
			menu.addSeparator();
			{ // Build
				entry = new MenuItem("Build");
				entry.addListener(new ChangeListener() {

					@Override
					public void changed (ChangeEvent event, Actor actor) {
						actor.getStage().addActor(new BuildDialog());
					}
				});
				menu.addItem(entry);
			}
			{ // New Project Entry
			}
			{ // Import Project Entry
			}
			{ // Export Project Entry
			}
			addMenu(menu);
		}
		{ // Components Menu
			menu = new Menu("Components");
			{ // Components
				entry = new MenuItem("Add GameObject");
				entry.addListener(new ChangeListener() {

					@Override
					public void changed (ChangeEvent event, Actor actor) {
						CreateChangeable changeable = new CreateChangeable(
							null, "Added GameObject",
							"{\"componentType\":\"GameObject\",\"name\":\"DEFAULT\",\"components\":[{\"componentType\":\"Transform\",\"x\":"
								+ RavTech.sceneHandler.worldCamera.position.x
								+ ",\"y\":"
								+ RavTech.sceneHandler.worldCamera.position.y
								+ ",\"rotation\":0,\"scale\":1}]}");
						ChangeManager.addChangeable(changeable);
						RavTechDK.setSelectedObjects(
							RavTech.currentScene.gameObjects.peek());
					}
				});
				menu.addItem(entry);
			}
			addMenu(menu);
		}
		{ // Window Menu
			menu = new Menu("Project");
			{ // Components
				entry = new MenuItem("Project Settings");
				entry.addListener(new ChangeListener() {

					@Override
					public void changed (ChangeEvent event, Actor actor) {
						actor.getStage().addActor(new ProjectSettingsWizard(
							RavTechDK.project, false));
					}
				});
				menu.addItem(entry);
			}
			addMenu(menu);
		}
		{ // Window Menu
			menu = new Menu("Window");
			{ // About
				entry = new MenuItem("Preferences");
				entry.addListener(new ChangeListener() {
					@Override
					public void changed (ChangeEvent event, Actor actor) {

					}
				});
				menu.addItem(entry);
				entry = new MenuItem("Toggle Debug Console");
				entry.addListener(new ChangeListener() {
					@Override
					public void changed (ChangeEvent event, Actor actor) {
						Gdx.app.postRunnable(new Runnable() {
							@Override
							public void run () {
								RavTech.ui.debugConsole.toggleVisible();
							}
						});
					}
				});
				menu.addItem(entry);
				entry = new MenuItem("Add Scene View");
				entry.addListener(new ChangeListener() {
					@Override
					public void changed (ChangeEvent event, Actor actor) {
						Gdx.app.postRunnable(new Runnable() {
							@Override
							public void run () {
								VisWindow window = new VisWindow("Scene");
								window.addCloseButton();
								window.setSize(320, 180);
								window.add(new SceneViewWidget(false)).grow();
								window.toFront();
								window.setVisible(true);
								RavTech.ui.getStage().addActor(window);
							}
						});
					}
				});
				menu.addItem(entry);
			}
			addMenu(menu);
		}
		{ // Help
			menu = new Menu("Help");
			{ // About
				entry = new MenuItem("About");
				entry.addListener(new ChangeListener() {

					@Override
					public void changed (ChangeEvent event, Actor actor) {

					}
				});
				menu.addItem(entry);
			}
			{ // ZeroBrane
				entry = new MenuItem("Updates");
				entry.addListener(new ChangeListener() {

					@Override
					public void changed (ChangeEvent event, Actor actor) {
						VisWindow window = RavTechDK.updateWidget;
						window.setVisible(!window.isVisible());
					}
				});
				menu.addItem(entry);
			}
			addMenu(menu);
		}

		final VisTextButton playButton = new VisTextButton("Run");
		playButton.addListener(new ChangeListener() {
			String state;

			@Override
			public void changed (ChangeEvent event, Actor actor) {
				RavTech.sceneHandler.paused = !RavTech.sceneHandler.paused;
				if (RavTech.sceneHandler.paused) {
					RavTech.files.loadState(state);
					playButton.setText("Run");
				} else {
					state = RavTech.files.storeState();
					RavTech.sceneHandler.reloadScripts();
					RavTech.sceneHandler.initScripts();
					playButton.setText("Stop");
				}
			}
		});
		playButton.setFocusBorderEnabled(false);
		getTable().add(playButton);
	}
}
