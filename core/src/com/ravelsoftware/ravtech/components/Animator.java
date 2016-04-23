
package com.ravelsoftware.ravtech.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.ravelsoftware.ravtech.animation.Animation;
import com.ravelsoftware.ravtech.animation.Timeline;

public class Animator extends GameComponent
	implements Json.Serializable {

	@Override
	public ComponentType getType () {
		return ComponentType.Animator;
	}

	@Override
	public String getName () {
		return getType().toString();
	}

	public Animation currentAnimation;
	public ObjectMap<String, Animation> animations;
	private String onCompleteAnimation;
	public float speed = 1;

	public Animator () {
		super();
		animations = new ObjectMap<String, Animation>();
	}

	@Override
	public void update () {
		currentAnimation.update(Gdx.graphics.getDeltaTime() * speed);
		if (currentAnimation.getTime()
			+ Math.round(Gdx.graphics.getDeltaTime() * speed
				* 1000) > currentAnimation.getLength())
			if (onCompleteAnimation != null) {
				setAnimation(onCompleteAnimation);
				onCompleteAnimation = null;
			}
	}

	public void setAnimation (String animation) {
		currentAnimation = animations.get("Default");
		update();
		currentAnimation = animations.get(animation);
		update();
	}

	public void setOnCompleteAnimation (String animation) {
		onCompleteAnimation = animation;
	}

	public void addAnimation (String animation) {
		animations.put(animation, new Animation());
	}

	@Override
	public void write (Json json) {
		json.writeValue("animations", animations);
		json.writeValue("currentAnimation",
			animations.findKey(currentAnimation, true));
	}

	@Override
	public void read (Json json, JsonValue jsonData) {
		for (int i = 0; i < jsonData.get("animations").size; i++) {
			JsonValue animationValue = jsonData.get("animations").get(i);
			Animation animation = new Animation();
			animation.animator = this;
			animations.put(animationValue.name, animation);
			animation.read(json, animationValue);
		}
		if (animations.size == 0) {
			animations.put("Default", new Animation());
			setAnimation("Default");
			currentAnimation.animator = this;
		} else
			setAnimation(jsonData.getString("currentAnimation"));
	}

	@Override
	public String toString () {
		String returnString = "Animation: {\n";
		returnString += "Timelines.size = "
			+ currentAnimation.timelines.size;
		for (Timeline timeline : currentAnimation.timelines) {
			returnString += "TimeLine: {";
			for (int i = 0; i < timeline.keys.size; i++)
				returnString += "Key:" + timeline.keys.get(i).time + "\n";
			returnString += "}\n";
		}
		returnString += "}";
		return returnString;
	}

	@Override
	public void setVariable (int variableID, Object value) {
		// TODO Auto-generated method stub
	}

	@Override
	public int getVariableId (String variableName) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getVariable (int variableID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getVariableNames () {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] getValiables () {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void load (Array<AssetDescriptor> dependencies) {
	}

	@Override
	public void finishedLoading () {
	}

	@Override
	public void draw (SpriteBatch batch) {
	}

	@Override
	public void dispose () {
	}
}
