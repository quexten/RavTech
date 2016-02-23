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
package com.ravelsoftware.ravtech.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.ravelsoftware.ravtech.RavTech;

public class Camera extends OrthographicCamera {

    int resolutionX;
    int resolutionY;
    FrameBuffer cameraBuffer;
    FrameBuffer cameraPingPongBuffer;
    Array<Shader> shaders = new Array<Shader>();
    String cameraBufferName = "TestCameraBuffer";
    String cameraPingPongBufferName = "TestCameraPingPongBufferName";
    boolean renderToFramebuffer = false;

    public Camera(int width, int height) {
        super(width, height);
        setResolution(width, height);
    }

    public float getRotation () { // converts from -180|180 to 0|359
        float camAngle = -(float)Math.atan2(up.x, up.y) * MathUtils.radiansToDegrees + 180;
        return camAngle;
    }

    public void setRotation (float rotation) {
        rotate(getRotation() - rotation + 180);
        update();
    }

    public void render (SpriteBatch spriteBatch) {
        RavTech.sceneHandler.renderer.render(spriteBatch, this);
        int passes = 0;
        for (int i = 0; i < shaders.size; i++) {
            shaders.get(i).applyPasses(spriteBatch, passes % 2 == 0 ? cameraBuffer : cameraPingPongBuffer,
                passes % 2 == 1 ? cameraBuffer : cameraPingPongBuffer);
            passes += shaders.get(i).getShaderPassCount();
        }
        if (renderToFramebuffer) {
            spriteBatch.begin();
            Matrix4 matrix = new Matrix4();
            matrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            spriteBatch.disableBlending();
            spriteBatch.setProjectionMatrix(matrix);
            spriteBatch.draw(
                passes % 2 == 0 ? cameraBuffer.getColorBufferTexture() : cameraPingPongBuffer.getColorBufferTexture(), 0,
                Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), -Gdx.graphics.getHeight());
            spriteBatch.end();
            spriteBatch.enableBlending();
        }
    }

    public void setResolution (int width, int height) {
        dispose();
        resolutionX = width;
        resolutionY = height;
        if (RavTech.sceneHandler.shaderManager != null && renderToFramebuffer) {
            float downSample = 1f;
            RavTech.sceneHandler.shaderManager.createFB(cameraBufferName, (int)(resolutionX / downSample),
                (int)(resolutionY / downSample));
            RavTech.sceneHandler.shaderManager.createFB(cameraPingPongBufferName, (int)(resolutionX / downSample),
                (int)(resolutionY / downSample));
            cameraBuffer = RavTech.sceneHandler.shaderManager.getFB(cameraBufferName);
            cameraPingPongBuffer = RavTech.sceneHandler.shaderManager.getFB(cameraPingPongBufferName);
            shaders.clear();
        }
    }

    public void dispose () {
        if (RavTech.sceneHandler.shaderManager != null && RavTech.sceneHandler.shaderManager.getFB(cameraBufferName) != null) {
            RavTech.sceneHandler.shaderManager.disposeFB(cameraBufferName);
            RavTech.sceneHandler.shaderManager.disposeFB(cameraPingPongBufferName);
        }
    }
}
