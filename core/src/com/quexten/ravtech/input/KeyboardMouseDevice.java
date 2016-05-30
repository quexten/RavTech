
package com.quexten.ravtech.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Buttons;
import com.quexten.ravtech.util.Debug;
import com.badlogic.gdx.InputMultiplexer;

public class KeyboardMouseDevice extends InputDevice {

	int scroll;
	boolean scrollChanged;
	boolean[] justPressed = new boolean[5];

	public KeyboardMouseDevice (InputMultiplexer multiplexer) {
		multiplexer.addProcessor(new InputProcessor() {
			int lastX;
			int lastY;

			@Override
			public boolean keyDown (int keycode) {
				changed();
				return false;
			}

			@Override
			public boolean keyUp (int keycode) {
				changed();
				return false;
			}

			@Override
			public boolean keyTyped (char character) {
				changed();
				return false;
			}

			@Override
			public boolean touchDown (int screenX, int screenY, int pointer, int button) {
				Debug.log("TouchDown", button);
				changed();
				justPressed[button] = true;
				return false;
			}

			@Override
			public boolean touchUp (int screenX, int screenY, int pointer, int button) {
				changed();
				return false;
			}

			@Override
			public boolean touchDragged (int screenX, int screenY, int pointer) {
				changed();
				return false;
			}

			@Override
			public boolean mouseMoved (int screenX, int screenY) {
				if (lastX != screenX || lastY != screenY) {
					changed();
					lastX = screenX;
					lastY = screenY;
				}
				return false;
			}

			@Override
			public boolean scrolled (int amount) {
				changed();
				KeyboardMouseDevice.this.scroll = amount;
				KeyboardMouseDevice.this.scrollChanged = true;
				return false;
			}

			void changed () {
				if (KeyboardMouseDevice.this.assignedPlayer.primaryDevice != KeyboardMouseDevice.this)
					KeyboardMouseDevice.this.assignedPlayer.setPrimaryDevice(KeyboardMouseDevice.this);
			}
		});
	}

	@Override
	public String getType () {
		return "KeyboardMouse";
	}

	// 0-5 Mouse [Left / Right / Middle / Forward / Back / Scroll]
	@Override
	public float getValue (int key) {
		return key > 5 ? (Gdx.input.isKeyPressed(key - 6) ? 1f : 0f)
			: key == 0 ? (Gdx.input.isButtonPressed(Buttons.LEFT) ? 1f : 0f)
				: key == 1 ? (Gdx.input.isButtonPressed(Buttons.RIGHT) ? 1f : 0f)
					: key == 2 ? (Gdx.input.isButtonPressed(Buttons.MIDDLE) ? 1f : 0f)
						: key == 3 ? (Gdx.input.isButtonPressed(Buttons.FORWARD) ? 1f : 0f)
							: key == 4 ? (Gdx.input.isButtonPressed(Buttons.BACK) ? 1f : 0f) : key == 5 ? scroll : 0f;
	}

	@Override
	public float getLastValue (int key) {
		return key > 5 ? (Gdx.input.isKeyJustPressed(key - 6) ? 0f : getValue(key))
			: key < 5 ? (justPressed[key] ? 0f : getValue(key)) : key == 5 ? scroll : 0f;
	}

	@Override
	public void update () {
		if (!scrollChanged)
			this.scroll = 0;
		scrollChanged = false;
	}

	@Override
	public void assignPlayer (Player player) {
		this.assignedPlayer = player;
	}

}
