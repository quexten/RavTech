/*******************************************************************************
 * Copyright 2014-2016 Bernd Schoolmann
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ravelsoftware.ravtech.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class JsonUtil {

	public static void writeColorToJson (Json json, Color color, String name) {
		json.writeObjectStart(name);
		json.writeValue("r", color.r);
		json.writeValue("g", color.g);
		json.writeValue("b", color.b);
		json.writeValue("a", color.a);
		json.writeObjectEnd();
	}

	public static Color readColorFromJson (JsonValue jsonData, String name) {
		JsonValue colordata = jsonData.getChild(name);
		if (colordata == null) return null;
		float r = 0;
		float g = 0;
		float b = 0;
		float a = 0;
		boolean hasnext = true;
		while (hasnext) {
			if (colordata.name().equals("r"))
				r = colordata.asFloat();
			else if (colordata.name().equals("g"))
				g = colordata.asFloat();
			else if (colordata.name().equals("b"))
				b = colordata.asFloat();
			else if (colordata.name().equals("a")) a = colordata.asFloat();
			colordata = colordata.next();
			hasnext = colordata != null;
		}
		return new Color(r, g, b, a);
	}
}
