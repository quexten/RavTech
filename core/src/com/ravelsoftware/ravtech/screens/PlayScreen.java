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
package com.ravelsoftware.ravtech.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.ravelsoftware.ravtech.RavTech;

public class PlayScreen implements Screen {

    @Override
    public void show () {
    }

    @Override
    public void render (float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        RavTech.sceneHandler.update(1f / 60f);
        RavTech.sceneHandler.render();
    }

    @Override
    public void resize (int width, int height) {
        RavTech.sceneHandler.resize(width, height);
    }

    @Override
    public void pause () {
    }

    @Override
    public void resume () {
    }

    @Override
    public void hide () {
    }

    @Override
    public void dispose () {
        RavTech.sceneHandler.dispose();
    }
}
