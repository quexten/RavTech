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
package com.ravelsoftware.ravtech.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.ravelsoftware.ravtech.RavTech;

public class RavInput {

    public Array<MouseButtonState> mouseButtonStates;
    public Array<ControllerState> controllerStates;

    public RavInput() {
        controllerStates = new Array<ControllerState>();
        mouseButtonStates = new Array<MouseButtonState>();
        for (int i = 0; i <= 4; i++)
            mouseButtonStates.add(new MouseButtonState(i));
    }

    // Mouse - Touch input
    public int lastPointer = 0;

    public int getX () {
        return Gdx.input.getX();
    }

    public int getX (int pointer) {
        return Gdx.input.getX(pointer);
    }

    public int getDeltaX () {
        return Gdx.input.getDeltaX();
    }

    public int getDeltaX (int pointer) {
        return Gdx.input.getDeltaX();
    }

    public int getY () {
        return Gdx.input.getY();
    }

    public int getY (int pointer) {
        return Gdx.input.getY(pointer);
    }

    public int getDeltaY () {
        return Gdx.input.getDeltaY();
    }

    public int getDeltaY (int pointer) {
        return Gdx.input.getDeltaY(pointer);
    }

    public Vector2 getWorldPosition () {
        Vector3 mouseworldpos = RavTech.sceneHandler.worldCamera.unproject(new Vector3(getX(), getY(), 0));
        return new Vector2(mouseworldpos.x, mouseworldpos.y);
    }

    public Vector2 getWorldPosition (int pointer) {
        Vector3 mouseworldpos = RavTech.sceneHandler.worldCamera.unproject(new Vector3(getX(pointer), getY(pointer), 0));
        return new Vector2(mouseworldpos.x, mouseworldpos.y);
    }

    public boolean isTouched () {
        return Gdx.input.isTouched();
    }

    public boolean justTouched () {
        return Gdx.input.justTouched();
    }

    public boolean isTouched (int pointer) {
        return Gdx.input.isTouched(pointer);
    }

    public int lastPointer () {
        return lastPointer;
    }

    public boolean isButtonPressed (int button) {
        return mouseButtonStates.get(button).newValue;
    }

    public boolean isButtonJustPressed (int button) {
        return mouseButtonStates.get(button).newValue && !mouseButtonStates.get(button).lastValue;
    }

    // Keyboard input
    public boolean isKeyPressed (int key) {
        return Gdx.input.isKeyPressed(key);
    }

    public boolean isKeyJustPressed (int key) {
        return Gdx.input.isKeyJustPressed(key);
    }

    // Controller input
    public float getGamepadAxisValue (int controller, int axis) {
        return controllerStates.get(controller).axisValues.get(axis, 0f);
    }

    public boolean isGamepadButtonPressed (int controller, int button) {
        return controllerStates.get(controller).buttonValues.get(button, false);
    }

    public boolean isGamepadButtonJustPressed (int controller, int button) {
        return controllerStates.get(controller).lastButtonValues.get(button) == false
            && controllerStates.get(controller).buttonValues.get(button);
    }

    public PovDirection getGamepadPovDirection (int controller) {
        return controllerStates.get(controller).povDirection;
    }
}
