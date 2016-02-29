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

package com.ravelsoftware.ravtech.dk.ui.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.ravelsoftware.ravtech.components.ComponentType;
import com.ravelsoftware.ravtech.components.GameComponent;
import com.ravelsoftware.ravtech.components.Rigidbody;

public class ColorUtils {

	public static java.awt.Color gdxToSwing (Color c) {
		return new java.awt.Color(c.r, c.g, c.b, c.a);
	}

	public static Color swingToGdx (java.awt.Color c) {
		return new Color(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, c.getAlpha() / 255.0f);
	}

	public static Color getGizmoColor (GameComponent component) {
		switch (component.getType()) {
		case Animator:
			break;
		case AudioEmitter:
			break;
		case BoxCollider:
			break;
		case CircleCollider:
			return getPhysicsBodyTypeColor(
				((Rigidbody)component.getParent().getComponentByType(ComponentType.Rigidbody)).getBody().getType());
		case Default:
			break;
		case GameObject:
			break;
		case Light:
			break;
		case PolygonCollider:
			break;
		case Renderer:
			break;
		case Rigidbody:
			break;
		case ScriptComponent:
			break;
		case SpriteRenderer:
			break;
		case Transform:
			break;
		default:
			break;
		}
		return Color.WHITE;
	}

	private static Color getPhysicsBodyTypeColor (BodyType type) {
		final Color SHAPE_STATIC = new Color(0.5f, 0.9f, 0.5f, 1);
		final Color SHAPE_KINEMATIC = new Color(0.5f, 0.5f, 0.9f, 1);
		final Color SHAPE_DYNAMIC = new Color(0.9f, 0.7f, 0.7f, 1);
		switch (type) {
		case DynamicBody:
			return SHAPE_DYNAMIC;
		case KinematicBody:
			return SHAPE_KINEMATIC;
		case StaticBody:
			return SHAPE_STATIC;
		default:
			return null;
		}
	}

	public static Color getSelectionColor () {
		return Color.YELLOW;
	}
}
