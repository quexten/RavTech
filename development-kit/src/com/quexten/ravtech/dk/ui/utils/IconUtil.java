
package com.quexten.ravtech.dk.ui.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.quexten.ravtech.components.Animator;
import com.quexten.ravtech.components.AudioEmitter;
import com.quexten.ravtech.components.BoxCollider;
import com.quexten.ravtech.components.CircleCollider;
import com.quexten.ravtech.components.GameComponent;
import com.quexten.ravtech.components.GameObject;
import com.quexten.ravtech.components.Light;
import com.quexten.ravtech.components.Rigidbody;
import com.quexten.ravtech.components.ScriptComponent;
import com.quexten.ravtech.components.SpriteRenderer;
import com.quexten.ravtech.components.Transform;

public class IconUtil {

	public static BufferedImage getBufferedImage (String filename) {
		try {
			return ImageIO.read(new File(System.getProperty("user.dir")
				+ "//resources//icons//" + filename + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ImageIcon getIcon (String filename) {
		return new ImageIcon("resources//icons//" + filename + ".png");
	}

	public static ImageIcon getIconByComponent (
		GameComponent component) {
		return getIconByComponentClass(component.getClass());
	}

	public static ImageIcon getIconByComponentClass (
		Class<? extends GameComponent> componentClass) {
		if (componentClass.isAssignableFrom(Transform.class))
			return IconUtil.getIcon("transform");
		else if (componentClass.isAssignableFrom(SpriteRenderer.class))
			return IconUtil.getIcon("image");
		else if (componentClass.isAssignableFrom(Rigidbody.class))
			return IconUtil.getIcon("rigidbody");
		else if (componentClass.isAssignableFrom(CircleCollider.class))
			return IconUtil.getIcon("collider_circle");
		else if (componentClass.isAssignableFrom(BoxCollider.class))
			return IconUtil.getIcon("collider_box");
		else if (componentClass.isAssignableFrom(ScriptComponent.class))
			return IconUtil.getIcon("page_white_code");
		else if (componentClass.isAssignableFrom(Light.class))
			return IconUtil.getIcon("lightbulb");
		else if (componentClass.isAssignableFrom(GameObject.class))
			return IconUtil.getIcon("package");
		// else if (component instanceof RavNetView)
		// return IconUtil.getIcon("connect");
		// else if (component instanceof ParticleSystemRenderer)
		// return IconUtil.getIcon("weather_snow");
		else if (componentClass.isAssignableFrom(AudioEmitter.class))
			return IconUtil.getIcon("sound");
		else if (componentClass.isAssignableFrom(Animator.class))
			return IconUtil.getIcon("timeline_marker");
		return null;
	}
}
