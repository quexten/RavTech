package com.quexten.ravtech.input;

public abstract class InputDevice {
	
	protected Player assignedPlayer;
	protected int lastPressedKey;	
	
	public abstract String getType();
	
	public abstract void assignPlayer(Player player);
	
	public abstract float getValue(int key);
	
	public abstract float getLastValue(int key);
	
	public boolean justPressed(int key) {
		return getValue(key) > getLastValue(key);
	}
	
	public int getLastPressed() {
		return lastPressedKey;
	}
	
	public abstract void update();	
	
}
