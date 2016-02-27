package com.ravelsoftware.ravtech.dk.ui.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.graphics.Camera;
import com.ravelsoftware.ravtech.util.Debug;

public class SceneViewWidget extends Widget {

    public Camera camera;
    Vector2 dragAnchorPosition;
    
    public SceneViewWidget(boolean main) {
        camera = main ? RavTech.sceneHandler.worldCamera : RavTech.sceneHandler.cameraManager.createCamera(1280, 720);
        camera.zoom = 0.05f;
        this.addListener(new ClickListener() {

            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                Vector3 unprojectedPosition = camera.unproject(new Vector3(x, getHeight() - y, 0));
                dragAnchorPosition = new Vector2(unprojectedPosition.x, unprojectedPosition.y);
                Debug.log("dragAnchor", dragAnchorPosition);
                Debug.log("screenPosition", new Vector2(x, getHeight() - y));
                return true;
            }
        });
        DragListener leftListener = new DragListener() {

            @Override
            public void drag (InputEvent event, float x, float y, int pointer) {
                Debug.log("Drag", x + "|" + y);
            }
        };
        this.addListener(leftListener);
        DragListener rightListener = new DragListener() {

            @Override
            public void drag (InputEvent event, float x, float y, int pointer) {
                int screenCenterWidth = (int)getWidth();
                int screenCenterHeight = (int)getHeight();
                float screenDiffX = (screenCenterWidth - x) - (float)screenCenterWidth / 2f;
                float screenDiffY = (screenCenterHeight - y - (float)screenCenterHeight / 2f);
                camera.position.set(dragAnchorPosition.x + screenDiffX * camera.zoom,
                    dragAnchorPosition.y + screenDiffY * camera.zoom, 0);
                camera.update();
            }
        };
        rightListener.setTapSquareSize(0);
        rightListener.setButton(Buttons.RIGHT);
        this.addListener(rightListener);
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                SceneViewWidget.this.resize();
            }
        });
    }

    public void resize () {
        if(!(getWidth() > 0 && getHeight() > 0))
            return;
        this.camera.setToOrtho(false, getWidth(), getHeight());
        this.camera.update();
        this.camera.setResolution((int)getWidth(), (int)getHeight());
    }

    @Override
    public void draw (Batch batch, float alpha) {
        super.draw(batch, alpha);
        batch.setColor(Color.WHITE);
        batch.disableBlending();       
        ((SpriteBatch)batch).draw(camera.getCameraBufferTexture(), 0, getHeight(), getWidth(), -getHeight());
        batch.enableBlending();
    }

    public void setResolution (int width, int height) {
        camera.setResolution(width, height);
    }
}
