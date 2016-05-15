
package com.quexten.ravtech.components;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.util.JsonUtil;

import box2dLight.ChainLight;
import box2dLight.ConeLight;
import box2dLight.DirectionalLight;
import box2dLight.PointLight;
import box2dLight.RavChainLight;

public class Light extends Renderer implements Json.Serializable {

	@Override
	public ComponentType getType () {
		return ComponentType.Light;
	}

	@Override
	public String getName () {
		return getType().toString();
	}

	public enum LightType {
		ChainLight, ConeLight, DirectionalLight, PointLight
	};

	box2dLight.Light light;
	LightType type;
	private int rayCount = 360;
	final static float[] defaultChain = new float[] {-1, -1, 0, 0, 1,
		1};

	public Light () {
		this(LightType.PointLight);
	}

	public Light (LightType type) {
		setLightType(type);
	}

	@Override
	public void load (
		@SuppressWarnings("rawtypes") Array<AssetDescriptor> dependencies) {
	}

	@Override
	public void finishedLoading () {
	}

	@Override
	public void update () {
	}

	@Override
	public void draw (SpriteBatch batch) {
		light.setColor(light.getColor());
		light.setPosition(getParent().transform.getPosition().x,
			getParent().transform.getPosition().y);
		light.setDirection(getParent().transform.getRotation());

	}

	@Override
	public void dispose () {
		light.setActive(false);
	}

	public void setAngle (float angle) {
		if (getLightType() == LightType.ConeLight)
			((ConeLight)light).setConeDegree(angle);
	}

	public float getAngle () {
		return (getLightType() == LightType.ConeLight)
			? ((ConeLight)light).getConeDegree()
			: (getLightType() == LightType.PointLight) ? 180 : 0;
	}

	public void setChain (float[] values) {
		if (getLightType() == LightType.ChainLight) {
			((ChainLight)light).chain.clear();
			((ChainLight)light).chain.addAll(values, 0, values.length);
		}
	}

	public float[] getChain () {
		return (getLightType() == LightType.ChainLight)
			? ((RavChainLight)light).chain.shrink() : new float[0];
	}

	public void setColor (Color color) {
		light.getColor().set(color);
	}

	public Color getColor () {
		return light.getColor();
	}

	public void setDistance (float distance) {
		light.setDistance(distance);
	}

	public float getDistance () {
		return light.getDistance();
	}

	public box2dLight.Light getLight () {
		return light;
	}

	public void setLightType (LightType type) {
		if (light != null)
			light.setActive(false);

		LightType lastLightType = this.type;

		this.type = type;

		Vector2 position = getParent() != null
			? getParent().transform.getPosition() : Vector2.Zero;
		float rotation = getParent() != null
			? getParent().transform.getRotation() : 0;
		float angle = (lastLightType == LightType.ConeLight)
			? ((ConeLight)getLight()).getConeDegree() : 90;

		float[] chain;
		Color color;
		float distance;
		boolean soft;
		float softnessLength;

		if (light != null) {
			chain = (lastLightType == LightType.ChainLight)
				? (this.getChain()) : defaultChain;
			color = getColor();
			distance = (lastLightType != LightType.DirectionalLight)
				? getDistance() : 10f;
			soft = isSoft();
			softnessLength = getSoftnessLength();
		} else {
			chain = defaultChain;
			color = Color.YELLOW;
			distance = 10f;
			soft = true;
			softnessLength = 2;
		}

		switch (type) {
			case ChainLight:
				light = new RavChainLight(
					RavTech.sceneHandler.lightHandler, rayCount, color,
					distance, 0, chain);
				break;
			case ConeLight:
				light = new ConeLight(RavTech.sceneHandler.lightHandler,
					rayCount, color, distance, position.x, position.y,
					rotation, angle);
				break;
			case DirectionalLight:
				light = new DirectionalLight(
					RavTech.sceneHandler.lightHandler, rayCount, color,
					rotation);
				break;
			case PointLight:
				light = new PointLight(RavTech.sceneHandler.lightHandler,
					rayCount > 3 ? rayCount + 1 : 3, color, distance,
					position.x, position.y);
				break;
		}

		light.setSoft(soft);
		light.setSoftnessLength(softnessLength);
	}

	public LightType getLightType () {
		return type;
	}

	public void setRayCount (int amount) {
		this.rayCount = amount > 2 ? amount : 3;
		this.setLightType(getLightType());
	}

	public int getRayCount () {
		return light.getRayNum() - ((getLightType() == LightType.PointLight) ? 1 : 0);
	}

	public void setSoft (boolean soft) {
		light.setSoft(soft);
	}

	public boolean isSoft () {
		return light.isSoft();
	}

	public void setSoftnessLength (float length) {
		light.setSoftnessLength(length);
	}

	public float getSoftnessLength () {
		return light.getSoftShadowLength();
	}

	@Override
	public void write (Json json) {
		json.writeValue("angle", getAngle());
		json.writeObjectStart("color");
		json.writeValue("r", getColor().r);
		json.writeValue("g", getColor().g);
		json.writeValue("b", getColor().b);
		json.writeValue("a", getColor().a);
		json.writeObjectEnd();
		json.writeValue("distance", getDistance());
		json.writeValue("type", getLightType().toString());
		json.writeValue("rayCount", getRayCount());
		json.writeValue("isSoft", isSoft());
		json.writeValue("softnessLength", getSoftnessLength());
	}

	@Override
	public void read (Json json, JsonValue jsonData) {
		if (jsonData.has("type"))
			setLightType(
				jsonData.getString("type")
					.equals(
						LightType.ChainLight.toString())
							? LightType.ChainLight
							: jsonData.getString("type")
								.equals(LightType.ConeLight.toString())
									? LightType.ConeLight
									: jsonData.getString("type")
										.equals(LightType.DirectionalLight
											.toString())
												? LightType.DirectionalLight
												: jsonData.getString("type")
													.equals(LightType.PointLight
														.toString())
															? LightType.PointLight
															: LightType.ConeLight);
		setAngle(
			jsonData.has("angle") ? jsonData.getFloat("angle") : 90);
		setColor(JsonUtil.readColorFromJson(jsonData, "color"));
		setDistance(jsonData.has("distance")
			? jsonData.getFloat("distance") : 10);
		setRayCount(jsonData.has("rayCount")
			? jsonData.getInt("rayCount") : 360);
		setSoft(jsonData.has("isSoft") ? jsonData.getBoolean("isSoft")
			: true);
		setSoftnessLength(jsonData.has("softnessLength")
			? jsonData.getFloat("softnessLength") : 2);
	}

	@Override
	public String[] getVariableNames () {
		return new String[] {"angle", "chain", "color", "distance",
			"type", "rayCount", "isSoft", "softnessLength"};
	}

	@Override
	public void setVariable (int variableId, Object value) {
		switch (variableId) {
			case 0:
				setAngle(Float.valueOf(String.valueOf(value)));
				break;
			case 1:
				setChain((float[])value);
				break;
			case 2:
				setColor((Color)value);
				break;
			case 3:
				setDistance(Float.valueOf(String.valueOf(value)));
				break;
			case 4:
				setLightType(
					value.toString()
						.equals(LightType.ChainLight.toString())
							? LightType.ChainLight
							: value.toString()
								.equals(LightType.ConeLight.toString())
									? LightType.ConeLight
									: value.toString()
										.equals(LightType.DirectionalLight
											.toString())
												? LightType.DirectionalLight
												: value.toString()
													.equals(LightType.PointLight
														.toString())
															? LightType.PointLight
															: LightType.PointLight);
				break;
			case 5:
				String amount = String.valueOf(value);
				amount = (amount.contains(".")
					? amount.substring(0, amount.lastIndexOf('.'))
					: amount);
				setRayCount(Integer.valueOf(amount));
				break;
			case 6:
				setSoft(Boolean.valueOf(String.valueOf(value)));
				break;
			case 7:
				setSoftnessLength(Float.valueOf(String.valueOf(value)));
				break;
		}
	}

	@Override
	public int getVariableId (String variableName) {
		String[] variables = getVariableNames();
		for (int i = 0; i < variables.length; i++)
			if (variables[i].equals(variableName))
				return i;
		return -1;
	}

	@Override
	public Object getVariable (int variableId) {
		switch (variableId) {
			case 0:
				return getAngle();
			case 1:
				return getChain();
			case 2:
				return getColor();
			case 3:
				return getDistance();
			case 4:
				return getLightType();
			case 5:
				return getRayCount();
			case 6:
				return isSoft();
			case 7:
				return getSoftnessLength();
		}
		return null;
	}

	@Override
	public Object[] getValiables () {
		return new Object[] {getAngle(), getChain(), getColor(),
			getDistance(), getLightType(), getRayCount(), isSoft(),
			getSoftnessLength()};
	}

}
