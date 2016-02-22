package com.ravelsoftware.ravtech.dk.ui.components;

import java.awt.Font;

import javax.swing.JTextField;

public class TextLabelPair extends SliderComponentPair {

    public TextLabelPair(String tag, String initialValue) {
        super(tag);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        this.pairedComponent = new JTextField(initialValue);
    }
}
