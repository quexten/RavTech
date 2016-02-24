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
package com.ravelsoftware.ravtech.dk.ui.animation;

import com.badlogic.gdx.utils.Array;
import com.ravelsoftware.ravtech.animation.Timeline;
import com.ravelsoftware.ravtech.components.GameComponent;

public class AnimationNode {

    public Array<AnimationNode> children = new Array<AnimationNode>();
    public GameComponent component;
    public String name;
    public Timeline timeline;
    public int variableId;

    public AnimationNode(String name) {
        this.name = name;
    }
}
