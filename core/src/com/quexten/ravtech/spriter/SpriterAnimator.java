
package com.quexten.ravtech.spriter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.brashmonkey.spriter.Data;
import com.brashmonkey.spriter.LibGdxDrawer;
import com.brashmonkey.spriter.LibGdxLoader;
import com.brashmonkey.spriter.Player;
import com.brashmonkey.spriter.PlayerTweener;
import com.brashmonkey.spriter.SCMLReader;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.components.Animator;

public class SpriterAnimator extends Animator {

	Player currentPlayer;
	ObjectMap<String, Player> animations = new ObjectMap<String, Player>();
	LibGdxDrawer drawer;
	LibGdxLoader loader;

	public String path = "";
	public String animation = "";

	public SpriterAnimator () {
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void load (Array<AssetDescriptor> dependencies) {

	}

	@Override
	public void finishedLoading () {
		FileHandle handle = RavTech.files.getAssetHandle(path);
		Data data = new SCMLReader(handle.read()).getData();

		loader = new LibGdxLoader(data);
		loader.load(handle);

		drawer = new LibGdxDrawer(loader);

		for (int i = 0; i < data.getEntity(0).animations(); i++) {
			Player player = new Player(data.getEntity(0));
			player.setAnimation(data.getEntity(0).getAnimation(i).name);
			animations.put(data.getEntity(0).getAnimation(i).name, player);
		}
		setAnimation(animation);
	}

	@Override
	public void update () {
	}

	@Override
	public void draw (SpriteBatch batch) {
		float scale = Math.min(Math.abs(this.getParent().transform.getLocalScale().x),
			Math.abs(this.getParent().transform.getLocalScale().y));
		currentPlayer.setScale(0.01f * scale);
		currentPlayer.setPosition(this.getParent().transform.getPosition());
		currentPlayer.update(Gdx.graphics.getDeltaTime());

		
		boolean flippedX = getParent().transform.getLocalScale().x < 0;
		boolean flippedY = getParent().transform.getLocalScale().y < 0;

		if (currentPlayer.flippedX() != (flippedX ? -1 : 1))
			currentPlayer.flipX();
		if (currentPlayer.flippedY() != (flippedY ? -1 : 1))
			currentPlayer.flipY();

		drawer.setBatch(batch);
		drawer.draw(currentPlayer);
	}

	@Override
	public void dispose () {
	}

	public void setAnimation (String animation) {
		this.animation = animation;
		currentPlayer = animations.get(animation);
		currentPlayer.setTime(0);
		currentPlayer.update(0.16f);
	}

	public Player getAnimation (String animation) {
		return animations.get(animation);
	}

	public void setNextAnimation (String animation, float tweenTime) {
		PlayerTweener tweener = new PlayerTweener(currentPlayer, getAnimation(animation));
		currentPlayer = tweener;
	}

	@Override
	public void write (Json json) {
		json.writeValue("path", path);
		json.writeValue("animation", animation);
	}

	@Override
	public void read (Json json, JsonValue jsonData) {
		this.path = jsonData.getString("path");
		this.animation = jsonData.getString("animation");
	}

}
