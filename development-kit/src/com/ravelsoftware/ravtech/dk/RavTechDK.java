package com.ravelsoftware.ravtech.dk;

import java.io.File;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.backends.lwjgl.LwjglFileHandle;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Json;
import com.ravelsoftware.ravtech.HookApi;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.Scene;
import com.ravelsoftware.ravtech.components.GameObject;
import com.ravelsoftware.ravtech.components.gizmos.Gizmo;
import com.ravelsoftware.ravtech.dk.input.InputManager;
import com.ravelsoftware.ravtech.project.Project;

public class RavTechDK {

    public static Project project;
    public static FileHandle projectHandle;
    public static RavTechDKUI ui;

    public static void initialize (RavTech ravtech) {
        ui = new RavTechDKUI(ravtech);
        setProject(projectHandle.path());
        Gdx.app.postRunnable(new Runnable() {

            @Override
            public void run () {
                ui.ravtechDKFrame.setTitle(ui.ravtechDKFrame.getFullTitle());
                RavTech.sceneHandler.paused = true;
                final InputManager inputManager = new InputManager();
                Gdx.input.setInputProcessor(inputManager);
                HookApi.onRenderHooks.add(new Runnable() {

                    @Override
                    public void run () {
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
                        renderer.begin(ShapeType.Line);
                        for (int i = 0; i < RavTechDKUtil.selectedObjects.size; i++) {
                            GameObject object = RavTechDKUtil.selectedObjects.get(i);
                            if (object != null) for (int n = 0; n < object.getComponents().size; n++) {
                                Gizmo gizmo = RavTechDKUtil.getGizmoFor(object.getComponents().get(n));
                                if (gizmo != null) gizmo.draw(renderer, RavTech.spriteBatch, gizmo == RavTechDKUtil.closestGizmo);
                            }
                        }
                        renderer.end();
                    }
                });
            }
        });
    }

    public static void setProject (final String projectRootPath) {
        project = Project.load(projectHandle = new LwjglFileHandle(new File(projectRootPath), FileType.Absolute));
        ui.ravtechDKFrame.setTitle(ui.ravtechDKFrame.getFullTitle());
        ui.ravtechDKFrame.view.setPath(projectRootPath);
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
        FileHandle projectHandle = new LwjglFileHandle(new File(projectRootPath), FileType.Absolute);
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
            .writeString(new LwjglFileHandle(new File("resources/shaders/default.frag"), FileType.Local).readString(), false);
        assetHandle.child("shaders").child("default.vert")
            .writeString(new LwjglFileHandle(new File("resources/shaders/default.vert"), FileType.Local).readString(), false);
        projectHandle.child("builds").mkdirs();
        projectHandle.child("icons").mkdirs();
        projectHandle.child("plugins").mkdirs();
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
