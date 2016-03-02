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

package com.ravelsoftware.ravtech.history;

import com.badlogic.gdx.utils.Array;

public class ChangeManager {

	public static int currentChangeable = 0;
	public static Array<Changeable> changeables = new Array<Changeable>();
	static Array<ChangeListener> changeListeners = new Array<ChangeListener>();

	public ChangeManager () {
	}

	public static void addChangeable (Changeable changeable) {
		/*
		 * if (RavTech.net.isInLobby() && changeable instanceof Changeable && changeable.isLocal) { Packet_DKChangeable packet = new
		 * Packet_DKChangeable(); packet.changeable = changeable; RavTech.net.sendToAll(packet, false); }
		 */
		if (!changeable.isDummy) {
			if (currentChangeable == changeables.size) {
				changeables.add(changeable);
				currentChangeable = changeables.size;
			} else {
				changeables.removeRange(currentChangeable, changeables.size - 1);
				changeables.add(changeable);
				currentChangeable++;
			}
			for (ChangeListener listener : changeListeners)
				listener.changed(changeable);
		}
	}

	public static void redo () {
		if (canRedo()) changeables.get(currentChangeable).redo();
		if (currentChangeable < changeables.size) currentChangeable++;
		if (currentChangeable < changeables.size - 1 && changeables.get(currentChangeable + 1).previousConnected) redo();
	}

	public static void undo () {
		if (currentChangeable > 0) currentChangeable--;
		changeables.get(currentChangeable).undo();
		if (changeables.get(currentChangeable).previousConnected) undo();
	}

	public static boolean canUndo () {
		return currentChangeable != 0;
	}

	public static boolean canRedo () {
		return currentChangeable != changeables.size;
	}

	public static void addChangeableListener (ChangeListener listener) {
		changeListeners.add(listener);
	}
}
