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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.ravelsoftware.ravtech.RavTech;

public class Shader {

    Array<String> passes;
    Matrix4 matrix = new Matrix4();

    public Shader() {
        matrix.setToOrtho2D(0, Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), -Gdx.graphics.getHeight());
    }

    public Shader(Array<String> passes) {
        this.passes = passes;
        matrix.setToOrtho2D(0, Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), -Gdx.graphics.getHeight());
    }

    public FrameBuffer applyPasses (SpriteBatch batch, FrameBuffer inputBuffer, FrameBuffer outputBuffer) {
        batch.begin();
        batch.setProjectionMatrix(matrix);
        FrameBuffer iBuffer = inputBuffer;
        FrameBuffer oBuffer = outputBuffer;
        for (int i = 0; i < passes.size; i++) {
            oBuffer.begin();
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            batch.setShader(RavTech.sceneHandler.shaderManager.get(passes.get(i)));
            batch.draw(iBuffer.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.setShader(RavTech.sceneHandler.shaderManager.get("default"));
            oBuffer.end();
            FrameBuffer tempBuffer = oBuffer;
            oBuffer = iBuffer;
            iBuffer = tempBuffer;
        }
        batch.end();
        return oBuffer;
    }

    public int getShaderPassCount () {
        return passes.size;
    }

    public void setPasses (Array<String> programs) {
        this.passes = programs;
    }
}
