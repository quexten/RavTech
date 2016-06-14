
package com.quexten.ravtech.animation;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Json.Serializable;
import com.quexten.ravtech.components.Animator;
import com.quexten.ravtech.components.GameComponent;

public interface Animation extends Serializable {

	void update (float delta);

	void draw (SpriteBatch batch);

	int getTime ();

	int getLength ();

	void setAnimator (Animator animator);

	void removeComponent (GameComponent component);

}
