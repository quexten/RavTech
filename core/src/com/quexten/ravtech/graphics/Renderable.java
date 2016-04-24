
package com.quexten.ravtech.graphics;

public abstract class Renderable {

	public String shaderName = "";

	public Renderable (String shaderName) {
		this.shaderName = shaderName;
	}

	public abstract void render ();
}
