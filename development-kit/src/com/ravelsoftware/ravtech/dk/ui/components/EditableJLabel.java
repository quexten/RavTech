package com.ravelsoftware.ravtech.dk.ui.components;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Create a JPanel with a CardLayout which switches to another JPanel on hover from
 * http://jamesmcminn.com/2011/01/making-an-editable-jlabel-in-swing/
 *
 * @author James McMinn */
public class EditableJLabel extends JPanel {

    /** Listen for nearly everything happening */
    public class EditableListener implements MouseListener, KeyListener, FocusListener {

        boolean locked = false;

        /** Lock to the text field while we have focus */
        @Override
        public void focusGained (FocusEvent arg0) {
            locked = true;
            oldValue = textField.getText();
            hasFocus = true;
        }

        /** Update the text when focus is lost and release the lock */
        @Override
        public void focusLost (FocusEvent e) {
            setText(textField.getText());
            for (ValueChangedListener v : finalListeners)
                v.valueChanged(oldValue, textField.getText(), EditableJLabel.this);
            release();
            mouseExited(null);
        }

        @Override
        public void keyPressed (KeyEvent e) {
        }

        @Override
        public void keyReleased (KeyEvent e) {
        }

        /** Check for key presses. We're only interested in Enter (save the value of the field) and Escape (reset the field to its
         * previous value) */
        @Override
        public void keyTyped (KeyEvent e) {
            if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                setText(textField.getText());
                release();
                mouseExited(null);
            } else if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
                release();
                mouseExited(null);
                setText(oldValue);
            }
        }

        @Override
        public void mouseClicked (MouseEvent e) {
        }

        /** Check for mouse over */
        @Override
        public void mouseEntered (MouseEvent e) {
            setHoverState(true);
        }

        /** Check for the mouse exiting and set the sate back to normal if possible */
        @Override
        public void mouseExited (MouseEvent e) {
            if (!locked) setHoverState(false);
        }

        /*
         * We don't need anything below this point in the Listener Class
         */
        @Override
        public void mousePressed (MouseEvent e) {
        }

        @Override
        public void mouseReleased (MouseEvent e) {
        }

        /** Release the lock so that we can go back to a JLabel */
        public void release () {
            this.locked = false;
            hasFocus = false;
        }
    }

    public static String oldValue;
    private static final long serialVersionUID = 1L;
    private LinkedList<ValueChangedListener> finalListeners = new LinkedList<ValueChangedListener>();
    public boolean hasFocus;
    private JLabel label;
    private LinkedList<ValueChangedListener> listeners = new LinkedList<ValueChangedListener>();
    private JTextField textField;

    /** Create the new panel
     *
     * @param startText The starting text */
    public EditableJLabel(String startText) {
        super();
        oldValue = startText;
        // Create the listener and the layout
        CardLayout layout = new CardLayout(0, 0);
        this.setLayout(layout);
        EditableListener hl = new EditableListener();
        // Create the JPanel for the "normal" state
        JPanel labelPanel = new JPanel(new GridLayout(1, 1));
        label = new JLabel(" " + startText);
        label
            .setPreferredSize(new Dimension((int)label.getPreferredSize().getWidth(), (int)label.getPreferredSize().getHeight()));
        labelPanel.add(label);
        // Create the JPanel for the "hover state"
        JPanel inputPanel = new JPanel(new GridLayout(1, 1));
        textField = new JTextField(startText);
        textField.addMouseListener(hl);
        textField.addKeyListener(hl);
        textField.addFocusListener(hl);
        inputPanel.add(textField);
        this.addMouseListener(hl);
        // Set the states
        this.add(labelPanel, "NORMAL");
        this.add(inputPanel, "HOVER");
        // Show the correct panel to begin with
        layout.show(this, "NORMAL");
    }

    /** Add a value changed listener to this EditableJLabel
     *
     * @param l */
    public void addValueChangedListener (ValueChangedListener l) {
        this.listeners.add(l);
    }

    public void addValueFinallyChangedListener (ValueChangedListener l) {
        this.finalListeners.add(l);
    }

    public void changeValue (String value) {
        setText(value);
        for (ValueChangedListener v : listeners)
            v.valueChanged(null, textField.getText(), EditableJLabel.this);
    }

    public void finallyChangeValue (String value) {
        final String oldText = oldValue;
        setText(value);
        for (ValueChangedListener v : finalListeners)
            v.valueChanged(oldText, textField.getText(), EditableJLabel.this);
    }

    /** Get the label
     *
     * @return the label */
    public JLabel getLabel () {
        return label;
    }

    /** Get the text from the label
     *
     * @return The text from the label */
    public String getText () {
        return this.textField.getText();
    }

    /** Get the text field
     *
     * @return the text field component */
    public JTextField getTextField () {
        return textField;
    }

    /** Set the hover state of the Panel
     *
     * @param hover True will set the state to hovering and show the input box. False will show the label. */
    public void setHoverState (boolean hover) {
        CardLayout cl = (CardLayout)this.getLayout();
        if (hover)
            cl.show(this, "HOVER");
        else
            cl.show(this, "NORMAL");
    }

    /** Set the text of the component
     *
     * @param text The text to start with */
    public void setText (String text) {
        this.label.setText(" " + text);
        this.textField.setText(text);
    }
}
