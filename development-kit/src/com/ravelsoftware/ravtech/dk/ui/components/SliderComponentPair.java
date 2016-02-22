package com.ravelsoftware.ravtech.dk.ui.components;

import javax.swing.JComponent;
import javax.swing.JLabel;

public class SliderComponentPair {

    public float changeFactor = 0.05f;
    public float initialValue;
    public JLabel nameLabel;
    public JComponent pairedComponent;
    int screenPassBuffer = 0; // buffer for when mouse moves
                              // off
                              // screen, back to other side
    public int xVal;

    public SliderComponentPair(String tag) {
        nameLabel = new JLabel(tag);
    }
}
