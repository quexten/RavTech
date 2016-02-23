/*******************************************************************************
 * Copyright 2014-2016 Bernd Schoolmann
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.ravelsoftware.ravtech;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.ravelsoftware.ravtech.components.GameComponent;
import com.ravelsoftware.ravtech.components.GameObject;
import com.ravelsoftware.ravtech.components.ScriptComponent;
import com.ravelsoftware.ravtech.components.UserData;
import com.ravelsoftware.ravtech.graphics.Camera;
import com.ravelsoftware.ravtech.graphics.SortedRenderer;
import com.ravelsoftware.ravtech.util.Debug;
import com.thesecretpie.shader.ShaderManager;

import box2dLight.RayHandler;

public class SceneHandler {

    public boolean paused = false;
    public float step = 1f / 60f;
    public float accumulator = 0;
    public Camera worldCamera;
    public FillViewport worldViewport;
    public World box2DWorld;
    public RayHandler lightHandler;
    public Box2DDebugRenderer box2DRenderer;
    public ShaderManager shaderManager;
    public SortedRenderer renderer;
    private String storedState;
    private InputProcessor storedProcessor;

    public SceneHandler() {
    }

    public void load () {
        shaderManager = new ShaderManager("shaders", RavTech.files.getAssetManager());
        shaderManager.add("default", RavTech.files.getAssetHandle("shaders/default.vert"),
            RavTech.files.getAssetHandle("shaders/default.frag"));
        renderer = new SortedRenderer(shaderManager);
        worldCamera = new Camera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        worldCamera.zoom = 0.05f;
        worldCamera.update();
        worldViewport = new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), worldCamera);
        box2DWorld = new World(new Vector2(0, -9.81f), false);
        box2DWorld.setContactListener(new ContactListener() {

            @Override
            public void beginContact (Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                UserData fixtureDataA = (UserData)fixtureA.getUserData();
                UserData fixtureDataB = (UserData)fixtureB.getUserData();
                if (fixtureDataA != null && fixtureDataB != null) {
                    if (fixtureDataA.component != null) fixtureDataA.component.onCollisionEnter(fixtureB);
                    if (fixtureDataB.component != null) fixtureDataB.component.onCollisionEnter(fixtureA);
                }
            }

            @Override
            public void endContact (Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                UserData fixtureDataA = (UserData)fixtureA.getUserData();
                UserData fixtureDataB = (UserData)fixtureB.getUserData();
                if (fixtureDataA != null && fixtureDataB != null) {
                    if (fixtureDataA.component != null) fixtureDataA.component.onCollisionExit(fixtureB);
                    if (fixtureDataB.component != null) fixtureDataB.component.onCollisionExit(fixtureA);
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
        box2DRenderer = new Box2DDebugRenderer();
        lightHandler = new RayHandler(box2DWorld);
        lightHandler.resizeFBO(Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 4);
        lightHandler.setAmbientLight(0.1f, 0.1f, 0.1f, 0.5f);
        lightHandler.setCombinedMatrix(worldCamera);
        lightHandler.setBlurNum(2);
        lightHandler.setLightMapRendering(false);
        lightHandler.setCulling(true);
    }

    public void update (float delta) {
        Debug.startTimer("box2dUpdateTime");
        Debug.debugFilledShapes.clear();
        Debug.debugLineShapes.clear();
        if (paused)
            box2DWorld.step(0, 8, 3);
        else
            box2DWorld.step(delta, 8, 3);
        Debug.endTimer("box2dUpdateTime");
        Debug.startTimer("updateTime");
        if (!paused) for (int i = 0; i < RavTech.currentScene.gameObjects.size; i++)
            RavTech.currentScene.gameObjects.get(i).update();
        Debug.endTimer("updateTime");
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
        // TODO Uncomment everything
        // for (MouseButtonState state : RavTech.input.mouseButtonStates)
        // state.update();
        // Prevent False game speed
        int targetFramerate = RavTech.settings.getInt("targetFramerate");
        if (Math.abs(Gdx.graphics.getFramesPerSecond() - 1f / targetFramerate) > 2) {
            accumulator += Gdx.graphics.getDeltaTime();
            while (accumulator > step) {
                accumulator -= step;
                update(step);
            }
        } else
            update(step);
        worldCamera.update();
        SpriteBatch spriteBatch = RavTech.spriteBatch;
        ShapeRenderer shapeRenderer = RavTech.shapeRenderer;
        spriteBatch.setProjectionMatrix(worldCamera.combined);
        if (RavTech.settings.getBoolean("useLights")) {
            lightHandler.setCombinedMatrix(worldCamera);
            lightHandler.updateAndRender();
        }
        worldCamera.render(spriteBatch);
        /*
         * Array<Renderer> renderers = new Array<Renderer>(); for (int n = 0; n < RavTech.currentScene.gameObjects.size; n++) {
         * GameObject object = RavTech.currentScene.gameObjects.get(n); object.transform.draw(spriteBatch); Array<GameComponent>
         * components = object.getComponentsInChildren(ComponentType.SpriteRenderer, ComponentType.Renderer); for (int i = 0; i <
         * components.size; i++) renderers.add((Renderer)components.get(i)); } for (int i = 0; i < renderers.size; i++)
         * renderers.get(i).draw(spriteBatch);
         */
        // lightHandler.setCombinedMatrix(worldCamera);
        // lightHandler.updateAndRender();
        shapeRenderer.setProjectionMatrix(worldCamera.combined);
        shapeRenderer.begin(ShapeType.Line);
        Debug.render(shapeRenderer);
        shapeRenderer.end();
        box2DRenderer.render(box2DWorld, worldCamera.combined);
    }

    public void resize (int width, int height) {
        if (worldCamera == null) return;
        worldCamera.setToOrtho(false, width, height);
        worldCamera.update();
        // if (shaderManager != null) shaderManager.resize(width, height, true);
        // worldCamera.setResolution(width, height);
        // render();
    }

    public void dispose () {
        if (lightHandler != null) {
            lightHandler.dispose();
            box2DRenderer.dispose();
            box2DWorld.dispose();
        }
    }

    /** Initializes all scripts in the scene */
    public void initScripts () {
        for (int i = 0; i < RavTech.currentScene.gameObjects.size; i++)
            initScripts(RavTech.currentScene.gameObjects.get(i).getComponents());
    }

    /** Initializes all scripts in the specified list of objects
     * @param array - the objects */
    public void initScripts (Array<GameComponent> objects) {
        for (int i = 0; i < objects.size; i++) {
            GameComponent component = objects.get(i);
            if (component instanceof ScriptComponent)
                ((ScriptComponent)component).script.init();
            else if (component instanceof GameObject) initScripts(((GameObject)component).getComponents());
        }
    }

    public void saveState () {
        /*
         * Array<String> assetNames = RavTech.assetManager.getAssetNames(); for (int i = 0; i < assetNames.size; i++) if
         * (assetNames.get(i).endsWith(".lua")) RavTech.assetManager.unload(assetNames.get(i)); RavTech.sceneHandler.paused =
         * false; Json json = new Json(); storedState = json.toJson(RavTech.currentScene); RavTech.sceneHandler.initScripts();
         * storedProcessor = Gdx.input.getInputProcessor(); Gdx.input.setInputProcessor(new GameInputProcessor());
         */
    }

    public void loadState (String state) {
        RavTech.currentScene.dispose();
        Json json = new Json();
        RavTech.currentScene = json.fromJson(Scene.class, state);
    }

    public void restoreState () {
        RavTech.sceneHandler.paused = true;
        loadState(storedState);
        Gdx.input.setInputProcessor(storedProcessor);
    }

    /** Reloads all scripts in the scene */
    public void reloadScripts () {
        for (int i = 0; i < RavTech.currentScene.gameObjects.size; i++)
            reloadScripts(RavTech.currentScene.gameObjects.get(i).getComponents());
    }

    /** Reloads all scripts in the specified list of objects
     * @param array - the objects */
    public void reloadScripts (Array<GameComponent> objects) {
        for (int i = 0; i < objects.size; i++) {
            GameComponent component = objects.get(i);
            if (component instanceof ScriptComponent)
                ((ScriptComponent)component).setScript(((ScriptComponent)component).path);
            else if (component instanceof GameObject) reloadScripts(((GameObject)component).getComponents());
        }
    }
}
