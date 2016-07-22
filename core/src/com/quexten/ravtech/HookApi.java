
package com.quexten.ravtech;

import com.badlogic.gdx.utils.Array;

public class HookApi {

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

}