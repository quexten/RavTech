
package com.quexten.ravtech;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class HookApi {

	private static ObjectMap<String, Array<Hook>> hooks = new ObjectMap<String, Array<Hook>>();
	
	public static void addHook(String id, Hook hook) {
		if(!hooks.containsKey(id)) {
			hooks.put(id, new Array<Hook>());
		}
		
		hooks.get(id).add(hook);
	}
	
	public static void runHooks (Array<Hook> hooks) {
		for (int i = 0; i < hooks.size; i++)
			hooks.get(i).run();
	}

	public static void runHooks (Array<Hook> hooks, Object arg) {
		for (int i = 0; i < hooks.size; i++)
			hooks.get(i).run(arg);
	}
	
	public static void runHooks(String id) {
		if(hooks.containsKey(id))
			runHooks(hooks.get(id));
	}
	
	public static void runHooks (String id, Object arg) {
		if(hooks.containsKey(id))
			runHooks(hooks.get(id), arg);
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
	
	public static void postHooks(String id) {
		if(hooks.containsKey(id))
			postHooks(hooks.get(id));
	}
	
	public static void postHooks(String id, Object arg) {
		if(hooks.containsKey(id))
			postHooks(hooks.get(id), arg);
	}

}
