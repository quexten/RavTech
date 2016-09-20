
package com.quexten.ravtech.dk.ui.editor;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.dk.RavTechDK;

public class RavWindow extends VisWindow {

	private Lwjgl3Window window;
	private Lwjgl3Window mainWindow;
	private VisTable rootTable;
	boolean visible;
	boolean isExternalized;
	public float accumulator;
	private boolean exitOnClose;
	final static int FRAMERATE = 120;
	final static float step = 1f / FRAMERATE;

	public RavWindow (String title) {
		this(title, true);
	}

	public RavWindow (String title, boolean external) {
		super(title);
		rootTable = new VisTable();
		super.add(rootTable).grow();
		setResizable(true);
		mainWindow = RavTechDK.getWindow();
		if (external)
			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run () {
					externalize();
				}
			});
	}

	public void externalize () {
		isExternalized = true;
		rootTable.setFillParent(true);

		if (window == null) {
			Lwjgl3Application app = (Lwjgl3Application)Gdx.app;

			Lwjgl3WindowConfiguration config = new Lwjgl3WindowConfiguration();
			config.setWindowedMode((int)getWidth(), (int)getHeight());
			config.setTitle(getTitleLabel().getText().toString());
			config.setInitialVisible(this.isVisible());
			RavWindowAppListener ravtechApp = new RavWindowAppListener();
			window = app.newWindow(ravtechApp, config);
			window.setWindowListener(new Lwjgl3WindowListener() {
				@Override
				public void iconified () {
				}

				@Override
				public void deiconified () {
				}

				@Override
				public void focusLost () {
				}

				@Override
				public void focusGained () {
				}

				@Override
				public boolean closeRequested () {
					if (!RavWindow.this.exitOnClose)
						RavWindow.this.setVisible(false);
					return RavWindow.this.exitOnClose;
				}

				@Override
				public void filesDropped (String[] files) {
				}
			});
			ravtechApp.registerWindow(window);
		}

		super.setVisible(false);
		final int width = (int)getWidth();
		final int height = (int)getHeight();
		window.postRunnable(new Runnable() {
			@Override
			public void run () {
				Gdx.graphics.setWindowedMode(width, height);
			}
		});
		window.setPosition((int)getX() + mainWindow.getPositionX(),
			(int)((mainWindow.getPositionY() + RavTechDK.windowHeight) - getY() - getHeight()));
		((RavWindowAppListener)window.getListener()).externalize();
	}

	public void internalize () {
		isExternalized = false;
		rootTable.setFillParent(false);
		setVisible(true);
		window.setVisible(false);

		setWidth(((RavWindowAppListener)window.getListener()).getWidth());
		float height = ((RavWindowAppListener)window.getListener()).getHeight();
		setHeight(height);

		setPosition(window.getPositionX() - mainWindow.getPositionX(),
			(mainWindow.getPositionY() + RavTechDK.windowHeight) - (window.getPositionY() + height));
		add(rootTable).grow();
	}

	@Override
	public <T extends Actor> Cell<T> add (T actor) {
		return rootTable.add(actor);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Cell<? extends Actor> row () {
		return rootTable.row();
	}

	@Override
	public Table align (int align) {
		return rootTable.align(align);
	}

	@Override
	public void clear () {
		rootTable.clear();
	}

	@Override
	public void setVisible (boolean visible) {
		this.visible = visible;
		if (!isExternalized)
			super.setVisible(visible);
		else if (window != null)
			window.setVisible(visible);
	}

	public void setExitOnClose (boolean exit) {
		this.exitOnClose = exit;
	}

	public Table top () {
		return rootTable.top();
	}

	public Table bottom () {
		return rootTable.bottom();
	}

	public Table left () {
		return rootTable.left();
	}

	public Table right () {
		return rootTable.right();
	}

	class RavWindowAppListener implements ApplicationListener {

		private int width = 100;
		private int height = 100;
		Stage stage = new Stage(new ScreenViewport() {
			@Override
			public void update (int screenWidth, int screenHeight, boolean centerRavCamera) {
				super.update(screenWidth, screenHeight, true);
			}
		});

		@Override
		public void create () {
			externalize();

		}

		@Override
		public void resize (int width, int height) {
			stage.getViewport().update(width, height);
			this.width = width;
			this.height = height;
		}

		@Override
		public void render () {
			RavWindow.this.act(Gdx.graphics.getDeltaTime());

			Color color = VisUI.getSkin().getColor("t-medium");
			Gdx.gl.glClearColor(color.r, color.g, color.b, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

			if (RavTech.isEditor && Math.abs(Gdx.graphics.getFramesPerSecond() - 1f / FRAMERATE) > 2) {
				accumulator += Gdx.graphics.getDeltaTime();
				while (accumulator > step) {
					accumulator -= step;
					stage.act(step);
				}
			}

			stage.draw();
		}

		@Override
		public void pause () {
		}

		@Override
		public void resume () {
		}

		@Override
		public void dispose () {
			stage.dispose();
		}

		void registerWindow (Lwjgl3Window window) {
			window.postRunnable(new Runnable() {
				@Override
				public void run () {
					Gdx.input.setInputProcessor(stage);
				}
			});
		}

		void externalize () {
			stage.addActor(RavWindow.this.rootTable);
		}

		public int getWidth () {
			return width;
		}

		public int getHeight () {
			return height;
		}

	};

}
