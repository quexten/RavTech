package com.ravelsoftware.ravtech.dk.ui.components;

import javax.swing.JComponent;

/** A listener for the EditableJLabel. Called when the value of the JLabel is updated.
 *
 * @author James McMinn */
public interface ValueChangedListener {

    void valueChanged (String oldValue, String newValue, JComponent source);
}
