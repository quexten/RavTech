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
