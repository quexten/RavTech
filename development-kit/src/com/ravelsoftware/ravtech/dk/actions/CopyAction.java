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
package com.ravelsoftware.ravtech.dk.actions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.ravelsoftware.ravtech.components.GameObject;
import com.ravelsoftware.ravtech.dk.RavTechDKUtil;
import com.ravelsoftware.ravtech.util.PrefabManager;

public class CopyAction implements Runnable {

    @Override
    public void run () {
        Array<GameObject> objects = RavTechDKUtil.selectedObjects;
        Gdx.app.getClipboard().setContents(PrefabManager.makePrefab(objects.get(0)));
    }
}
