
package com.ravelsoftware.ravtech;

import com.badlogic.gdx.utils.Array;

public class HookApi {

	public static Array<Runnable> onUpdateHooks = new Array<Runnable>();
	public static Array<Runnable> onRenderHooks = new Array<Runnable>();
	public static Array<Runnable> onShutdownHooks = new Array<Runnable>();
	public static Array<Runnable> onResizeHooks = new Array<Runnable>();
}
