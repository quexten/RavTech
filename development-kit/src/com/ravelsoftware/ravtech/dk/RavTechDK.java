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
package com.ravelsoftware.ravtech.dk;

import java.io.File;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3FileHandle;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.Scene;
import com.ravelsoftware.ravtech.components.gizmos.GizmoHandler;
import com.ravelsoftware.ravtech.project.Project;

public class RavTechDK {

    public static Project project;
    public static FileHandle projectHandle;
    public static GizmoHandler gizmoHandler;

    public static void initialize (RavTech ravtech) {
        gizmoHandler = new GizmoHandler();
        setProject(projectHandle.path());
        Gdx.app.postRunnable(new Runnable() {

            @Override
            public void run () {
                RavTech.sceneHandler.paused = true;
                //final InputManager inputManager = new InputManager();
                //Gdx.input.setInputProcessor(inputManager);
                /*HookApi.onRenderHooks.add(new Runnable() {

                    @Override
                    public void run () {
                        // Selection
                        if (!Gdx.input.isButtonPressed(Buttons.LEFT)) inputManager.selectionAlpha -= Gdx.graphics.getDeltaTime();
                        ShapeRenderer renderer = RavTech.shapeRenderer;
                        Gdx.gl.glEnable(GL20.GL_BLEND);
                        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
                        renderer.setColor(1, 1, 1, inputManager.selectionAlpha);
                        renderer.begin(ShapeType.Filled);
                        renderer.rect(inputManager.dragStartPosition.x, inputManager.dragStartPosition.y,
                            inputManager.dragCurrentPosition.x - inputManager.dragStartPosition.x,
                            inputManager.dragCurrentPosition.y - inputManager.dragStartPosition.y);
                        renderer.end();
                        Gdx.gl.glDisable(GL20.GL_BLEND);
                        // Gizmos
                        renderer.begin(ShapeType.Line);
                        gizmoHandler.render(renderer);
                        renderer.end();
                    }
                });*/
            }
        });
    }

    public static void setProject (final String projectRootPath) {
        project = Project.load(projectHandle = new Lwjgl3FileHandle(new File(projectRootPath), FileType.Absolute));
        //ui.ravtechDKFrame.setTitle(ui.ravtechDKFrame.getFullTitle());
        //ui.ravtechDKFrame.view.setPath(projectRootPath);
        Gdx.app.postRunnable(new Runnable() {

            @Override
            public void run () {
                RavTech.settings.setValue("RavTechDK.project.path", projectRootPath);
                RavTech.settings.save();
            }
        });
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
        textureHandle.child("error.png").write(new Lwjgl3FileHandle(new File("resources/icons/error.png"), FileType.Local).read(), false);
        project.save(projectHandle);
    }

    public static File getLocalFile (String path) {
        return new File(System.getProperty("user.dir") + "/" + path);
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
}
