
package com.quexten.ravtech;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

public class HookApi {

	public static Array<Hook> onPreBootHooks = new Array<Hook>();
	public static Array<Hook> onBootHooks = new Array<Hook>();
	public static Array<Hook> onUpdateHooks = new Array<Hook>();
	public static Array<Hook> onRenderHooks = new Array<Hook>();
	public static Array<Hook> onShutdownHooks = new Array<Hook>();
	public static Array<Hook> onResizeHooks = new Array<Hook>();

	public static void runHooks (Array<Hook> hooks) {
		for (int i = 0; i < hooks.size; i++)
			hooks.get(i).run();
	}

	public static void runHooks (Array<Hook> hooks, Object arg) {
		for (int i = 0; i < hooks.size; i++)
			hooks.get(i).run(arg);
	}

	/** Posts hooks to run in the main thread
	 * 
	 * @param hooks - the hooks to execute */
	public static void postHooks (final Array<Hook> hooks) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run () {
				runHooks(hooks);
			}
		});
	}

	/** Posts hooks to run in the main thread
	 * 
	 * @param hooks - the hooks to execute
	 * @param arg - the argument to pass */
	public static void postHooks (final Array<Hook> hooks, final Object arg) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run () {
				runHooks(hooks, arg);
			}
		});
	}

}
