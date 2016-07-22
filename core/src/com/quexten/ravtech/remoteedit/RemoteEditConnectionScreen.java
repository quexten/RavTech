
package com.quexten.ravtech.remoteedit;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.kotcrab.vis.ui.widget.VisList;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.quexten.ravtech.Hook;
import com.quexten.ravtech.HookApi;
import com.quexten.ravtech.RavTech;

public class RemoteEditConnectionScreen implements Screen {

	Stage screenStage;
	VisList<String> connectionList = new VisList<String>();

	public RemoteEditConnectionScreen() {
	}

	@Override
	public void show() {
		BitmapFont font = new BitmapFont(Gdx.files.internal("font.fnt"));
		if (Gdx.app.getType() == ApplicationType.Android)
			font.getData().setScale(2);

		screenStage = new Stage();
		RavTech.input.addInputProcessor(screenStage);
		connectionList.setItems("192.168.0.1 - Quexten", "localhost - Quexten");
		connectionList.getStyle().font = font;
		connectionList.getStyle().selection.setTopHeight(50);
		connectionList.getStyle().selection.setBottomHeight(50);

		VisTable backgroundTable = new VisTable();
		backgroundTable.setFillParent(true);
		backgroundTable.add(connectionList).grow().top();
		backgroundTable.row();

		VisTextButton connectButton = new VisTextButton("Connect");
		connectButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				RemoteEdit.connect(connectionList.getSelected());
				RavTech.sceneHandler.cameraManager.createCamera(Gdx.graphics.getWidth(),
						Gdx.graphics.getHeight()).zoom = 0.05f;
				final TouchDebugController controller = new TouchDebugController();
				HookApi.onUpdateHooks.add(new Hook() {
					@Override
					public void run() {
						controller.update();
					}
				});
				Gdx.input.setInputProcessor(new GestureDetector(controller));
			}
		});
		VisTextButton refreshButton = new VisTextButton("Refresh");
		refreshButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				RavTech.net.discoverHosts();
			}
		});

		try {
			Field cacheField = ClassReflection.getDeclaredField(Label.class, "cache");
			cacheField.setAccessible(true);
			cacheField.set(connectButton.getLabel(), new BitmapFontCache(font));
			cacheField.set(refreshButton.getLabel(), new BitmapFontCache(font));
		} catch (ReflectionException e) {
			e.printStackTrace();
		}

		VisTable bottomTable = new VisTable();
		backgroundTable.add(bottomTable).growX().height(200);
		bottomTable.add(connectButton).grow();
		bottomTable.add(refreshButton).grow();

		screenStage.addActor(backgroundTable);
	}

	@Override
	public void render(float delta) {
		if (RavTech.net.transportLayers.size > 0)
			connectionList.setItems(RavTech.net.transportLayers.first().hosts);
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
		screenStage.act(delta);
		screenStage.draw();
	}

	@Override
	public void resize(int width, int height) {
		screenStage.getViewport().apply();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		screenStage.dispose();
	}

}
