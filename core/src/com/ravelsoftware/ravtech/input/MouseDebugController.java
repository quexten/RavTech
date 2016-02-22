package com.ravelsoftware.ravtech.input;

import com.badlogic.gdx.InputProcessor;
import com.ravelsoftware.ravtech.RavTech;

public class MouseDebugController implements InputProcessor {

    @Override
    public boolean keyDown (int keycode) {
        return false;
    }

    @Override
    public boolean keyUp (int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped (char character) {
        return false;
    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        // RavTech.input.lastPointer = pointer;
        return false;
    }

    @Override
    public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        // RavTech.input.lastPointer = pointer;
        return false;
    }

    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved (int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled (int amount) {
        RavTech.sceneHandler.worldCamera.zoom += amount * 0.01f;
        return true;
    }
}
