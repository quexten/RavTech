
package com.quexten.ravtech.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputMultiplexer;

public class KeyboardMouseDevice extends InputDevice {

	int scroll;
	boolean scrollChanged;

	boolean[] lastJustPressedStates = new boolean[255];
	boolean[] justPressedStates = new boolean[255];

	public InputProcessor processor;

	public KeyboardMouseDevice (InputMultiplexer multiplexer) {
		processor = new InputProcessor() {
			int lastX;
			int lastY;

			@Override
			public boolean keyDown (int keycode) {
				KeyboardMouseDevice.this.setLastPressed(keycode + 6);
				changed();
				return false;
			}

			@Override
			public boolean keyUp (int keycode) {
				KeyboardMouseDevice.this.setLastPressed(keycode + 6);
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
				KeyboardMouseDevice.this.setLastPressed(button);
				changed();
				return false;
			}

			@Override
			public boolean touchUp (int screenX, int screenY, int pointer, int button) {
				KeyboardMouseDevice.this.setLastPressed(button);
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
		};
		multiplexer.addProcessor(processor);
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
			: key < 5 ? (lastJustPressedStates[key] ? 0f : getValue(key)) : key == 5 ? scroll : 0f;
	}

	@Override
	public void update () {
		if (!scrollChanged)
			this.scroll = 0;
		scrollChanged = false;
		for (int i = 0; i < this.justPressedStates.length; i++) {
			this.lastJustPressedStates[i] = this.justPressedStates[i];
			this.justPressedStates[i] = false;
		}

		if (this.justPressed == true && this.lastJustPressed == false) {
			this.lastJustPressed = true;
		} else {
			this.justPressed = false;
			this.lastJustPressed = false;
		}
	}

	@Override
	public void assignPlayer (Player player) {
		this.assignedPlayer = player;
	}

	private void setLastPressed (int key) {
		this.lastPressedKey = key;
		this.justPressedStates[key] = true;
		this.justPressed = true;
	}

}
