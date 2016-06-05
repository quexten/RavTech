
package com.quexten.ravtech.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntFloatMap;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;

public class GamePadDevice extends InputDevice {

	final Controller gamePad;
	final int axisAmount;
	final int buttonAmount;
	final float[] values;
	final float[] oldValues;
	float deadzone = 0.5f;

	public GamePadDevice (Controller gamePad) {
		this.gamePad = gamePad;
		axisAmount = getAxisAmount();
		buttonAmount = getButtonAmount();
		values = new float[axisAmount + buttonAmount];
		oldValues = new float[axisAmount + buttonAmount];
	}

	@Override
	public void assignPlayer (Player player) {
		this.assignedPlayer = player;
		gamePad.addListener(new ControllerListener() {
			@Override
			public void connected (Controller controller) {
			}

			@Override
			public void disconnected (Controller controller) {
			}

			@Override
			public boolean buttonDown (Controller controller, int buttonCode) {
				GamePadDevice.this.lastPressedKey = buttonCode + GamePadDevice.this.axisAmount;
				changed();
				return true;
			}

			@Override
			public boolean buttonUp (Controller controller, int buttonCode) {
				GamePadDevice.this.lastPressedKey = buttonCode + GamePadDevice.this.axisAmount;
				changed();
				return true;
			}

			@Override
			public boolean axisMoved (Controller controller, int axisCode, float value) {
				if (Math.abs(value) > GamePadDevice.this.deadzone) {
					GamePadDevice.this.lastPressedKey = axisCode;
					changed();
				}
				return true;
			}

			@Override
			public boolean povMoved (Controller controller, int povCode, PovDirection value) {
				return true;
			}

			@Override
			public boolean xSliderMoved (Controller controller, int sliderCode, boolean value) {
				return true;
			}

			@Override
			public boolean ySliderMoved (Controller controller, int sliderCode, boolean value) {
				return true;
			}

			@Override
			public boolean accelerometerMoved (Controller controller, int accelerometerCode, Vector3 value) {
				return true;
			}

			void changed () {
				GamePadDevice.this.justPressed = true;
				if (GamePadDevice.this.assignedPlayer.primaryDevice != GamePadDevice.this)
					GamePadDevice.this.assignedPlayer.setPrimaryDevice(GamePadDevice.this);
			}
		});
	}

	@Override
	public String getType () {
		return "GamePad";
	}

	@Override
	public float getValue (int key) {
		return values[key];
	}

	@Override
	public float getLastValue (int key) {
		return oldValues[key];
	}

	public void update () {
		if (this.justPressed == true && this.lastJustPressed == false) {
			this.lastJustPressed = true;
		} else {
			this.justPressed = false;
			this.lastJustPressed = false;
		}
		for (int i = 0; i < values.length; i++) {
			oldValues[i] = values[i];
			values[i] = i < axisAmount ? ((Math.abs(gamePad.getAxis(i)) > GamePadDevice.this.deadzone) ? gamePad.getAxis(i) : 0)
				: (gamePad.getButton(i - axisAmount) ? 1f : 0f);
		}
	}

	public int getAxisAmount () {
		try {
			switch (Gdx.app.getType()) {
				case Android:
					Field androidAxes = ClassReflection
						.getDeclaredField(ClassReflection.forName("com.badlogic.gdx.controllers.android.AndroidController"), "axes");
					androidAxes.setAccessible(true);
					return ((float[])androidAxes.get(gamePad)).length;
				case Desktop:
					Field desktopAxes = ClassReflection
						.getDeclaredField(ClassReflection.forName("com.badlogic.gdx.controllers.lwjgl3.Lwjgl3Controller"), "axisState");
					desktopAxes.setAccessible(true);
					return ((float[])desktopAxes.get(gamePad)).length;
				case HeadlessDesktop:
					return 0;
				case WebGL:
					Field gwtAxes = ClassReflection
						.getDeclaredField(ClassReflection.forName("com.badlogic.gdx.controllers.gwt.GwtController"), "axes");
					gwtAxes.setAccessible(true);
					return ((float[])gwtAxes.get(gamePad)).length;
				case iOS:
					return 0;
				default:
					break;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return 0;
	}

	public int getButtonAmount () {
		try {
			switch (Gdx.app.getType()) {
				case Android:
					Field androidButtons = ClassReflection
						.getDeclaredField(ClassReflection.forName("com.badlogic.gdx.controllers.android.AndroidController"), "buttons");
					androidButtons.setAccessible(true);
					return ((IntIntMap)androidButtons.get(gamePad)).size;
				case Desktop:
					Field desktopButtons = ClassReflection.getDeclaredField(
						ClassReflection.forName("com.badlogic.gdx.controllers.lwjgl3.Lwjgl3Controller"), "buttonState");
					desktopButtons.setAccessible(true);
					return ((boolean[])desktopButtons.get(gamePad)).length;
				case HeadlessDesktop:
					return 0;
				case WebGL:
					Field gwtButtons = ClassReflection
						.getDeclaredField(ClassReflection.forName("com.badlogic.gdx.controllers.gwt.GwtController"), "buttons");
					gwtButtons.setAccessible(true);
					return ((IntFloatMap)gwtButtons.get(gamePad)).size;
				case iOS:
					return 0;
				default:
					break;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return 0;
	}

}
