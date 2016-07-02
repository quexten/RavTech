
package com.quexten.ravtech;

import com.badlogic.gdx.utils.Array;

public class HookApi {

	public static Array<Runnable> onBootHooks = new Array<Runnable>();
	public static Array<Runnable> onUpdateHooks = new Array<Runnable>();
	public static Array<Runnable> onRenderHooks = new Array<Runnable>();
	public static Array<Runnable> onShutdownHooks = new Array<Runnable>();
	public static Array<Runnable> onResizeHooks = new Array<Runnable>();

	public static void runHooks (Array<Runnable> hooks) {
		for (int i = 0; i < hooks.size; i++)
			hooks.get(i).run();
	}

}
