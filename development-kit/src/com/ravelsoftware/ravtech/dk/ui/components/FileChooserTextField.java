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
package com.ravelsoftware.ravtech.dk.ui.components;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class FileChooserTextField extends JPanel {

    private static final long serialVersionUID = 2822040981161679230L;
    public JButton button;
    public JTextField field;
    public JLabel label;
    String defaultPath = System.getProperty("user.home");

    public FileChooserTextField(String labelText, File selectedPath, final Component parent, final int fileSelectionMode) {
        label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        field = new JTextField(selectedPath.getPath().replace('\\', '/'));
        field.setEditable(false);
        button = new JButton("Select");
        final JFileChooser chooser = new JFileChooser();
        // Add listener on chooser to detect changes to selected file
        chooser.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange (PropertyChangeEvent evt) {
                if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(evt.getPropertyName())) {
                    JFileChooser chooser = (JFileChooser)evt.getSource();
                    evt.getOldValue();
                    evt.getNewValue();
                    File curFile = chooser.getSelectedFile();
                    if (curFile != null) field.setText(curFile.getPath());
                }
            }
        });
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed (ActionEvent evt) {
                chooser.setFileSelectionMode(fileSelectionMode);
                chooser.setCurrentDirectory(new File(defaultPath));
                chooser.showOpenDialog(parent);
                ActionListener[] actionListeners = field.getActionListeners();
                for (int i = 0; i < actionListeners.length; i++)
                    actionListeners[i].actionPerformed(new ActionEvent(this, 0, field.getText()));
            }
        });
        this.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0;
        constraints.weighty = 1;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.ipadx = 0;
        constraints.ipady = 0;
        constraints.insets = new Insets(0, 0, 0, 0);
        this.add(label, constraints);
        constraints.gridx++;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        this.add(field, constraints);
        constraints.gridx++;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        this.add(button, constraints);
        this.setMaximumSize(new Dimension((int)this.getMaximumSize().getWidth(), 32));
    }

    public String getText () {
        return field.getText();
    }

    public void setText (String path) {
        field.setText(path);
    }

    public void setDefaultPath (String path) {
        this.defaultPath = path;
    }
}
