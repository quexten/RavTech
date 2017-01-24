
package com.quexten.ravtech;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.quexten.ravtech.components.GameComponent;
import com.quexten.ravtech.components.GameObject;
import com.quexten.ravtech.components.ScriptComponent;
import com.quexten.ravtech.components.UserData;
import com.quexten.ravtech.graphics.CameraManager;
import com.quexten.ravtech.graphics.SortedRenderer;
import com.quexten.ravtech.util.Debug;
import com.thesecretpie.shader.ShaderManager;

import box2dLight.DynamicRayHandler;

public class SceneHandler {

	public boolean paused = false;
	public float step = 1f / 60f;
	public float accumulator = 0;

	public World box2DWorld;
	public DynamicRayHandler lightHandler;
	public ShaderManager shaderManager;
	public SortedRenderer renderer;
	public CameraManager cameraManager;

	public SceneHandler () {
		box2DWorld = new World(new Vector2(0, -9.81f), false);
		box2DWorld.setContactListener(new ContactListener() {
			@Override
			public void beginContact (Contact contact) {
				Fixture fixtureA = contact.getFixtureA();
				Fixture fixtureB = contact.getFixtureB();
				UserData fixtureDataA = (UserData)fixtureA.getUserData();
				UserData fixtureDataB = (UserData)fixtureB.getUserData();
				if (fixtureDataA != null && fixtureDataB != null) {
					if (fixtureDataA.component != null)
						fixtureDataA.component.onCollisionEnter(fixtureB, contact);
					if (fixtureDataB.component != null)
						fixtureDataB.component.onCollisionEnter(fixtureA, contact);
				}
			}

			@Override
			public void endContact (Contact contact) {
				Fixture fixtureA = contact.getFixtureA();
				Fixture fixtureB = contact.getFixtureB();
				UserData fixtureDataA = (UserData)fixtureA.getUserData();
				UserData fixtureDataB = (UserData)fixtureB.getUserData();
				if (fixtureDataA != null && fixtureDataB != null) {
					if (fixtureDataA.component != null)
						fixtureDataA.component.onCollisionExit(fixtureB, contact);
					if (fixtureDataB.component != null)
						fixtureDataB.component.onCollisionExit(fixtureA, contact);
				}
				contact.setEnabled(false);
			}

			@Override
			public void preSolve (Contact contact, Manifold oldManifold) {
			}

			@Override
			public void postSolve (Contact contact, ContactImpulse impulse) {
			}
		});

		shaderManager = new ShaderManager("", RavTech.files.getAssetManager());
		shaderManager.add("default", Gdx.files.internal("shaders/default.vert"),
			Gdx.files.internal("shaders/default.frag"));

		renderer = new SortedRenderer(shaderManager);

		cameraManager = new CameraManager();

		lightHandler = new DynamicRayHandler(box2DWorld);
		lightHandler.resizeFBO(Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 4);
		lightHandler.setAmbientLight(0.1f, 0.1f, 0.1f, 0.5f);
		lightHandler.setBlurNum(2);
		lightHandler.setLightMapRendering(false);
		lightHandler.setCulling(true);
		
		//Fix bodies not colliding when spawned
		paused = true;
		update(0);
		paused = false;
	}
	
	public void update (float delta) {
		// Clean Up Debug Rendering
		Debug.debugFilledShapes.clear();
		Debug.debugLineShapes.clear();

		Debug.startTimer("box2dUpdateTime");
		if (paused)
			box2DWorld.step(0, 8, 3);
		else
			box2DWorld.step(delta, 8, 3);
		Debug.endTimer("box2dUpdateTime");

		Debug.startTimer("updateTime");
		if (!paused)
			for (int i = 0; i < RavTech.currentScene.gameObjects.size; i++)
				RavTech.currentScene.gameObjects.get(i).update();
		Debug.endTimer("updateTime");

		// Clean Up Dead Box2d Bodies
		removeBox2DBodies();
	}

	private void removeBox2DBodies () {
		if (!box2DWorld.isLocked()) {
			Array<Body> bodies = new Array<Body>();
			box2DWorld.getBodies(bodies);
			for (int i = 0; i < bodies.size; i++)
				if (bodies.get(i).getUserData() != null && ((UserData)bodies.get(i).getUserData()).isFlaggedForDelete)
					box2DWorld.destroyBody(bodies.get(i));
				else
					for (Fixture f : bodies.get(i).getFixtureList())
						if (f.getUserData() != null && ((UserData)f.getUserData()).isFlaggedForDelete)
							box2DWorld.destroyBody(bodies.get(i));
		}
	}

	public void render () {
		int targetFramerate = RavTech.settings != null ? RavTech.settings.getInt("targetFramerate") : 60;

		if (Math.abs(Gdx.graphics.getFramesPerSecond() - 1f / targetFramerate) > 2) {
			accumulator += Gdx.graphics.getDeltaTime();
			while (accumulator > step) {
				accumulator -= step;
				update(step);
			}
		} else
			update(step);

		Color clearColor = RavTech.currentScene.renderProperties.backgroundColor;
		Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		cameraManager.render();
	}

	public void resize (int width, int height) {
		HookApi.runHooks("onResize");
	}

	public void dispose () {
		if (lightHandler != null) {
			lightHandler.dispose();
			box2DWorld.dispose();
			cameraManager.dispose();
		}
	}

	/** Initializes all scripts in the scene */
	public void initScripts () {
		for (int i = 0; i < RavTech.currentScene.gameObjects.size; i++)
			initScripts(RavTech.currentScene.gameObjects.get(i).getComponents());
	}

	/** Initializes all scripts in the specified list of objects
	 * 
	 * @param array - the objects */
	public void initScripts (Array<GameComponent> objects) {
		for (int i = 0; i < objects.size; i++) {
			GameComponent component = objects.get(i);
			if (component instanceof ScriptComponent)
				((ScriptComponent)component).script.init();
			else if (component instanceof GameObject)
				initScripts(((GameObject)component).getComponents());
		}
	}

	/** Reloads all scripts in the scene */
	public void reloadScripts () {
		for (int i = 0; i < RavTech.currentScene.gameObjects.size; i++)
			reloadScripts(RavTech.currentScene.gameObjects.get(i).getComponents());
	}

	/** Reloads all scripts in the specified list of objects
	 * 
	 * @param array - the objects */
	public void reloadScripts (Array<GameComponent> objects) {
		for (int i = 0; i < objects.size; i++) {
			GameComponent component = objects.get(i);
			if (component instanceof ScriptComponent)
				((ScriptComponent)component).setScript(((ScriptComponent)component).path);
			else if (component instanceof GameObject)
				reloadScripts(((GameObject)component).getComponents());
		}
	}
}
