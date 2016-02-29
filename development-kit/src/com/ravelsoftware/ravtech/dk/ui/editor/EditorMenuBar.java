
package com.ravelsoftware.ravtech.dk.ui.editor;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.kotcrab.vis.ui.widget.Menu;
import com.kotcrab.vis.ui.widget.MenuBar;
import com.kotcrab.vis.ui.widget.MenuItem;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.Scene;
import com.ravelsoftware.ravtech.dk.RavTechDKUtil;
import com.ravelsoftware.ravtech.dk.zerobrane.ZeroBraneUtil;
import com.ravelsoftware.ravtech.history.ChangeManager;
import com.ravelsoftware.ravtech.history.CreateChangeable;
import com.ravelsoftware.ravtech.util.Debug;

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
						RavTech.files.getAsset(RavTech.files.getAssetManager().getAssetFileName(RavTech.currentScene));
						Debug.log("Saved Scene", "[" + RavTech.files.getAssetManager().getAssetFileName(RavTech.currentScene) + "]");
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
						RavTech.files.getAsset(RavTech.files.getAssetManager().getAssetFileName(RavTech.currentScene));
						Debug.log("Saved Scene", "[" + RavTech.files.getAssetManager().getAssetFileName(RavTech.currentScene) + "]");
					}
				});
				entry.setShortcut(Keys.CONTROL_LEFT, Keys.SHIFT_LEFT, Keys.S);
				menu.addItem(entry);
			}
			{ // Load Scene
				entry = new MenuItem("Load Scene");
				entry.addListener(new ChangeListener() {

					@Override
					public void changed (ChangeEvent event, Actor actor) {

						RavTech.files.getAsset(RavTech.files.getAssetManager().getAssetFileName(RavTech.currentScene));
						Debug.log("Saved Scene", "[" + RavTech.files.getAssetManager().getAssetFileName(RavTech.currentScene) + "]");
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
			this.addMenu(menu);
		}
		{ // Components Menu
			menu = new Menu("Components");
			{ // Components
				entry = new MenuItem("Add GameObject");
				entry.addListener(new ChangeListener() {

					@Override
					public void changed (ChangeEvent event, Actor actor) {
						CreateChangeable changeable = new CreateChangeable(null, "Added GameObject",
							"{\"componentType\":\"GameObject\",\"name\":\"DEFAULT\",\"components\":[{\"componentType\":\"Transform\",\"x\":"
								+ RavTech.sceneHandler.worldCamera.position.x + ",\"y\":" + RavTech.sceneHandler.worldCamera.position.y
								+ ",\"rotation\":0,\"scale\":1}]}");
						ChangeManager.addChangeable(changeable);
						RavTechDKUtil.setSelectedObject(RavTech.currentScene.gameObjects.peek());
					}
				});
				menu.addItem(entry);
			}
			this.addMenu(menu);
		}
		{ // Window Menu
			menu = new Menu("Project");
			{ // Components
				entry = new MenuItem("Component Settings");
				entry.addListener(new ChangeListener() {

					@Override
					public void changed (ChangeEvent event, Actor actor) {
					}
				});
				menu.addItem(entry);
			}
			this.addMenu(menu);
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
			}
			this.addMenu(menu);
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
				entry = new MenuItem("Check for ZeroBrane Updates");
				entry.addListener(new ChangeListener() {

					@Override
					public void changed (ChangeEvent event, Actor actor) {
						ZeroBraneUtil.checkForUpdates();
					}
				});
				menu.addItem(entry);
			}
			this.addMenu(menu);
		}
	}
}
