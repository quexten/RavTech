
package com.quexten.ravtech.animation;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.brashmonkey.spriter.Curve;
import com.brashmonkey.spriter.Curve.Type;

public class Key implements Json.Serializable {

	Object value;
	Timeline timeline;
	public Curve curve;
	public int time;

	public Key () {
	}

	public Key (int time, Object value, Timeline line, Curve curve) {
		this.time = time;
		this.value = value;
		timeline = line;
		this.curve = curve;
	}

	/** Updates the Key
	 * 
	 * @param nextKeyTime - time of the next key in milliseconds
	 * @param currentTime - current time of animation in milliseconds */
	public void update (int nextKeyTime, int currentTime) {
		float percentage;
		if (timeline.animator == null)
			return;
		if (time < nextKeyTime)
			percentage = (float)(currentTime - time) / (float)(nextKeyTime - time);
		else
			percentage = (float)(currentTime - time) / (float)(nextKeyTime + timeline.animator.currentAnimation.getLength() - time);
		tweenValue(value, percentage);
	}

	void tweenValue (Object obj, float percentage) {
		if (value instanceof Float)
			if (timeline.component != null)
				timeline.component.setVariable(timeline.variableId,
					timeline.variableType == Timeline.VAR_TYPE_FLOAT
						? curve.tween((Float)value, (Float)timeline.getNextKey().value, percentage)
						: curve.tweenAngle((Float)value, (Float)timeline.getNextKey().value, percentage));
		if (obj instanceof Color) {
			Color tempCol = ((Color)obj).cpy();
			tempCol.r = curve.tween(((Color)obj).r, ((Color)timeline.getNextKey().value).r, percentage);
			tempCol.g = curve.tween(((Color)obj).g, ((Color)timeline.getNextKey().value).g, percentage);
			tempCol.b = curve.tween(((Color)obj).b, ((Color)timeline.getNextKey().value).b, percentage);
			tempCol.a = curve.tween(((Color)obj).a, ((Color)timeline.getNextKey().value).a, percentage);
			if (timeline.component != null)
				timeline.component.setVariable(timeline.variableId, tempCol);
		}
		if (value instanceof Integer)
			timeline.component.setVariable(timeline.variableId, value);
	}

	@Override
	public String toString () {
		return time + "|" + value + "|" + timeline;
	}

	@Override
	public void write (Json json) {
		json.writeValue("time", time);
		json.writeValue("value", value);
		json.writeValue("curve", curve);
	}

	@Override
	public void read (Json json, JsonValue jsonData) {
		if (jsonData.has("time"))
			time = jsonData.getInt("time");
		if (jsonData.get("value").isNumber())
			value = jsonData.getFloat("value");
		else
			value = new Color();
		JsonValue curveValue = jsonData.get("curve");
		JsonValue constraintsValue = curveValue.get("constraints");
		int c1, c2, c3, c4;
		c1 = constraintsValue.getInt("c1");
		c2 = constraintsValue.getInt("c2");
		c3 = constraintsValue.getInt("c3");
		c4 = constraintsValue.getInt("c4");
		curve = new Curve();
		curve.constraints.set(c1, c2, c3, c4);
		if (curveValue.has("type"))
			curve.setType(json.readValue(Type.class, curveValue.get("type")));
	}
}
