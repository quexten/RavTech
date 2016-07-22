
package com.quexten.ravtech.headless;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.quexten.ravtech.EngineConfiguration;
import com.quexten.ravtech.Hook;
import com.quexten.ravtech.HookApi;
import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.net.kryonet.KryonetTransportLayer;
import com.quexten.ravtech.remoteedit.RemoteEdit;
import com.quexten.ravtech.scripts.lua.LuaJScriptLoader;
import com.quexten.ravtech.util.Debug;

public class HeadlessLauncher {

	public static void main (String[] arg) {
		HeadlessApplicationConfiguration appConfig = new HeadlessApplicationConfiguration();
		appConfig.renderInterval = 1f / 60f;

		EngineConfiguration engineConfig = new EngineConfiguration();
		engineConfig.title = "Remote Edit";

		final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

		final RavTech ravtech = new RavTech(new FileHandleResolver() {
			@Override
			public FileHandle resolve (String path) {
				return Gdx.files.local("temp").child(path);
			}
		}, engineConfig);

		HookApi.onUpdateHooks.add(new Hook() {
			@Override
			public void run () {
				try {
					if (bufferedReader.ready())
						Debug.runScript(bufferedReader.readLine());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		RavTech.scriptLoader = new LuaJScriptLoader();

		HookApi.onBootHooks.add(new Hook() {
			@Override
			public void run () {
				Debug.log("Hook", "run");
				RavTech.net.transportLayers.add(new KryonetTransportLayer(RavTech.net));
				RemoteEdit.host();
			}
		});

		new HeadlessApplication(ravtech, appConfig);
	}
}
