
package com.ravelsoftware.ravtech.animation;

import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.brashmonkey.spriter.Curve;
import com.ravelsoftware.ravtech.components.Animator;
import com.ravelsoftware.ravtech.components.GameComponent;
import com.ravelsoftware.ravtech.components.Transform;
import com.ravelsoftware.ravtech.util.GameObjectTraverseUtil;

public class Timeline implements Json.Serializable {

	public Array<Key> keys = new Array<Key>();
	static final int VAR_TYPE_FLOAT = 0, VAR_TYPE_ANGLE = 1, VAR_TYPE_OBJECT = 2;
	int variableType = 0;
	public int variableId;
	int currentTime;
	public GameComponent component;
	public Animator animator;
	public Animation animation;

	public Timeline () {
	}

	public Timeline (GameComponent component, int varId) {
		this.component = component;
		variableId = varId;
		if (component instanceof Transform && variableId == 2)
			variableType = VAR_TYPE_ANGLE;
	}

	public void update (int time) {
		currentTime = time;
		if (getCurrentKey() != null && getNextKey() != null)
			getCurrentKey().update(getNextKey().time, currentTime);
	}

	public void addKey (Key key) {
		for (int i = 0; i < keys.size; i++)
			if (keys.get(i).time == key.time) {
				keys.removeIndex(i);
				break;
			}
		keys.add(key);
		keys.sort(new Comparator<Key>() {

			@Override
			public int compare (Key arg0, Key arg1) {
				return arg0.time > arg1.time ? 1 : arg0.time < arg1.time ? -1 : 0;
			}
		});
		key.timeline = this;
	}

	public Key getNextKey () {
		for (int i = 0; i < keys.size; i++)
			if (keys.get(i).time >= currentTime)
				return keys.get(i);
		return keys.get(0);
	}

	public Key getCurrentKey () {
		if (currentTime == 0)
			return keys.first();
		for (int i = 0; i < keys.size; i++)
			if (keys.get(i).time >= currentTime)
				return i > 0 ? keys.get(i - 1) : keys.get(keys.size - 1);
		return keys.get(keys.size - 1);
	}

	@Override
	public void write (Json json) {
		json.writeValue("keys", keys);
		json.writeValue("variableId", variableId);
		String componentPath = GameObjectTraverseUtil.pathFromGameComponent(component);
		if (componentPath.length() > 0)
			componentPath = componentPath.substring(GameObjectTraverseUtil.pathFromGameComponent(animator.getParent()).length(),
				componentPath.length());
		else
			componentPath = GameObjectTraverseUtil.pathFromGameComponent(animator.getParent());
		json.writeValue("component", componentPath);
	}

	@Override
	public void read (Json json, final JsonValue jsonData) {
		for (int i = 0; i < jsonData.get("keys").size; i++) {
			JsonValue keyData = jsonData.get("keys").get(i);
			Key key = new Key();
			key.curve = new Curve();
			key.timeline = this;
			key.read(json, keyData);
			keys.add(key);
		}
		variableId = jsonData.getInt("variableId");
		String componentPath = jsonData.getString("component");
		String ownPath = GameObjectTraverseUtil.pathFromGameComponent(animator.getParent());
		component = GameObjectTraverseUtil.gameComponentFromPath(ownPath + componentPath);
		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run () {
				Gdx.app.postRunnable(new Runnable() {

					@Override
					public void run () {
						String componentPath = jsonData.getString("component");
						String ownPath = GameObjectTraverseUtil.pathFromGameComponent(animator.getParent());
						component = GameObjectTraverseUtil.gameComponentFromPath(ownPath + componentPath);
						if (component == null)
							animation.timelines.removeValue(Timeline.this, true);
						if (component instanceof Transform && variableId == 2)
							variableType = VAR_TYPE_ANGLE;
						update(0);
					}
				});
			}
		});
	}

	public void removeKeyAtTime (int time) {
		for (int i = 0; i < keys.size; i++)
			if (keys.get(i).time == time) {
				keys.removeIndex(i);
				break;
			}
	}

	public Key getLastKey () {
		for (int i = keys.size - 1; i >= 0; i--)
			if (keys.get(i).time <= currentTime)
				return keys.get(i);
		return keys.get(0);
	}
}
