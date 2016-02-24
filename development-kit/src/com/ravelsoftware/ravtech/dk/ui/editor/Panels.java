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
package com.ravelsoftware.ravtech.dk.ui.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicComboBoxUI;

import com.badlogic.gdx.utils.Array;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.components.Animator;
import com.ravelsoftware.ravtech.components.AudioEmitter;
import com.ravelsoftware.ravtech.components.BoxCollider;
import com.ravelsoftware.ravtech.components.CircleCollider;
import com.ravelsoftware.ravtech.components.ComponentType;
import com.ravelsoftware.ravtech.components.GameComponent;
import com.ravelsoftware.ravtech.components.GameObject;
import com.ravelsoftware.ravtech.components.Light;
import com.ravelsoftware.ravtech.components.Rigidbody;
import com.ravelsoftware.ravtech.components.ScriptComponent;
import com.ravelsoftware.ravtech.components.SpriteRenderer;
import com.ravelsoftware.ravtech.dk.RavTechDK;
import com.ravelsoftware.ravtech.dk.ui.components.EditableJLabel;
import com.ravelsoftware.ravtech.dk.ui.components.FileChooserTextField;
import com.ravelsoftware.ravtech.dk.ui.components.SliderColorPicker;
import com.ravelsoftware.ravtech.dk.ui.components.SliderDropdownList;
import com.ravelsoftware.ravtech.dk.ui.components.SliderEditableLabel;
import com.ravelsoftware.ravtech.dk.ui.components.TextButtonPair;
import com.ravelsoftware.ravtech.dk.ui.components.ValueChangedListener;
import com.ravelsoftware.ravtech.dk.ui.components.XComboBox;
import com.ravelsoftware.ravtech.graphics.SortingLayer;
import com.ravelsoftware.ravtech.history.ChangeManager;
import com.ravelsoftware.ravtech.history.ModifyChangeable;

public class Panels {

    public static class ComponentPanel extends JPanel {

        private static final long serialVersionUID = 8763976192411897768L;
        public GameObject object;
        public GameComponent component;
        public GridBagConstraints constraints;

        public ComponentPanel(GameObject object, GameComponent component) {
            this.object = object;
            this.component = component;
            this.setLayout(new GridBagLayout());
            constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.weightx = 1;
            constraints.weighty = 1;
            constraints.fill = GridBagConstraints.BOTH;
        }

        public void addSliderLabel (final String variable) {
            final SliderEditableLabel label = new SliderEditableLabel(
                variable.substring(0, 1).toUpperCase() + variable.substring(1) + ":",
                Float.valueOf(String.valueOf(component.getVariable(component.getVariableId(variable)))));
            ((EditableJLabel)label.pairedComponent).addValueChangedListener(new ValueChangedListener() {

                @Override
                public void valueChanged (String oldValue, String newValue, JComponent source) {
                    new ModifyChangeable(component, "", variable, Float.valueOf(newValue), Float.valueOf(newValue)).redo();
                }
            });
            ((EditableJLabel)label.pairedComponent).addValueFinallyChangedListener(new ValueChangedListener() {

                @Override
                public void valueChanged (String oldValue, String newValue, JComponent source) {
                    component.setVariable(component.getVariableId(variable), Float.valueOf(oldValue));
                    ChangeManager.addChangeable(new ModifyChangeable(component, "Set " + variable, variable,
                        Float.valueOf(oldValue), Float.valueOf(newValue)));
                }
            });
            this.add(label.nameLabel, constraints);
            constraints.gridx++;
            this.add(label.pairedComponent, constraints);
            constraints.gridx = 0;
            constraints.gridy++;
        }

        public void addColorPicker (final String variable) {
            final SliderColorPicker label = new SliderColorPicker(
                variable.substring(0, 1).toUpperCase() + variable.substring(1) + ":",
                (com.badlogic.gdx.graphics.Color)component.getVariable(component.getVariableId(variable)));
            label.pairedComponent.addPropertyChangeListener(new PropertyChangeListener() {

                @Override
                public void propertyChange (PropertyChangeEvent arg0) {
                    if (arg0.getPropertyName().equals("Color")) {
                        com.badlogic.gdx.graphics.Color oldColor = new com.badlogic.gdx.graphics.Color();
                        com.badlogic.gdx.graphics.Color newColor = new com.badlogic.gdx.graphics.Color();
                        com.badlogic.gdx.graphics.Color.rgba8888ToColor(oldColor, Integer.valueOf(arg0.getOldValue().toString()));
                        com.badlogic.gdx.graphics.Color.rgba8888ToColor(newColor, Integer.valueOf(arg0.getNewValue().toString()));
                        ChangeManager
                            .addChangeable(new ModifyChangeable(component, "Changed color", variable, oldColor, newColor));
                        ChangeManager.redo();
                    }
                }
            });
            this.add(label.nameLabel, constraints);
            constraints.gridx++;
            this.add(label.pairedComponent, constraints);
            constraints.gridx = 0;
            constraints.gridy++;
        }

        public void addFileSelector (final String variable) {
            final FileChooserTextField fileChooserTextField = new FileChooserTextField("Path:",
                new File(String.valueOf(component.getVariable(component.getVariableId(variable)))), this,
                JFileChooser.FILES_ONLY);
            fileChooserTextField.setDefaultPath(RavTechDK.projectHandle.path());
            fileChooserTextField.field.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed (ActionEvent event) {
                    String subPath = event.getActionCommand().replace('\\', '/')
                        .replace(RavTechDK.projectHandle.path().replace('\\', '/') + "/assets", "").substring(1);
                    component.setVariable(component.getVariableId(variable), event.getActionCommand().replace('\\', '/')
                        .replace(RavTechDK.projectHandle.path().replace('\\', '/') + "/assets", ""));
                    fileChooserTextField.field.setText(subPath);
                }
            });
            constraints.fill = GridBagConstraints.NONE;
            constraints.anchor = GridBagConstraints.WEST;
            constraints.weightx = 0.5;
            this.add(fileChooserTextField.label, constraints);
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.anchor = GridBagConstraints.CENTER;
            constraints.gridx++;
            constraints.weightx = 4;
            this.add(fileChooserTextField.field, constraints);
            constraints.weightx = 0.5;
            constraints.gridx++;
            this.add(fileChooserTextField.button, constraints);
            constraints.fill = GridBagConstraints.BOTH;
            constraints.weightx = 1;
            constraints.gridx = 0;
            constraints.gridy++;
        }

        public void addDropdown (final String variable, String[] options) {
            final SliderDropdownList sliderDropdown = new SliderDropdownList(
                variable.substring(0, 1).toUpperCase() + variable.substring(1) + ":", options);
            ((XComboBox<?>)sliderDropdown.pairedComponent).setSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged (ListSelectionEvent e) {
                    new ModifyChangeable(component, "", variable, e.getSource(), e.getSource()).redo();
                }
            });
            ((XComboBox<?>)sliderDropdown.pairedComponent).addActionListener(new ActionListener() {

                @Override
                public void actionPerformed (ActionEvent arg0) {
                    String selectedItem = ((XComboBox<?>)sliderDropdown.pairedComponent).getSelectedItem().toString();
                    component.setVariable(component.getVariableId(variable), selectedItem);
                    ChangeManager
                        .addChangeable(new ModifyChangeable(component, "Set " + variable, variable, selectedItem, selectedItem));
                }
            });
            this.add(sliderDropdown.nameLabel, constraints);
            constraints.gridx++;
            this.add(sliderDropdown.pairedComponent, constraints);
            constraints.gridx = 0;
            constraints.gridy++;
        }

        public void addButton (final ActionListener listener, String label, String text) {
            final TextButtonPair textPair = new TextButtonPair(label, text, listener);
            this.add(textPair.nameLabel, this.constraints);
            constraints.gridx++;
            this.add(textPair.pairedComponent, this.constraints);
            constraints.gridx = 0;
            constraints.gridy++;
        }
    }

    public static ComponentPanel transform (final GameObject object) {
        ComponentPanel panel = new ComponentPanel(object, object.transform);
        panel.addSliderLabel("x");
        panel.addSliderLabel("y");
        panel.addSliderLabel("rotation");
        return panel;
    }

    public static ComponentPanel light (final Light component) {
        final ComponentPanel panel = new ComponentPanel(component.getParent(), component);
        panel.addColorPicker("color");
        panel.addSliderLabel("angle");
        panel.addSliderLabel("distance");
        return panel;
    }

    public static ComponentPanel scriptComponent (ScriptComponent component) {
        final ComponentPanel panel = new ComponentPanel(component.getParent(), component);
        panel.addFileSelector("path");
        return panel;
    }

    public static ComponentPanel gameObject (GameObject component) {
        return new ComponentPanel(component, component);
    }

    public static ComponentPanel audioEmitter (AudioEmitter component) {
        final ComponentPanel panel = new ComponentPanel(component.getParent(), component);
        panel.addFileSelector("path");
        return panel;
    }

    public static ComponentPanel animatorComponent (Animator component) {
        final ComponentPanel panel = new ComponentPanel(component.getParent(), component);
        return panel;
    }

    public static ComponentPanel rigidbody (Rigidbody component) {
        final ComponentPanel panel = new ComponentPanel(component.getParent(), component);
        panel.addDropdown("bodyType", new String[] {"Static", "Dynamic", "Kinematic"});
        return panel;
    }

    public static void addColliderPanels (ComponentPanel panel) {
        panel.addSliderLabel("density");
        panel.addSliderLabel("friction");
        panel.addSliderLabel("restitution");
    }

    public static ComponentPanel circleCollider (final CircleCollider component) {
        final ComponentPanel panel = new ComponentPanel(component.getParent(), component);
        addColliderPanels(panel);
        panel.addSliderLabel("x");
        panel.addSliderLabel("y");
        panel.addSliderLabel("radius");
        panel.addButton(new ActionListener() {

            @Override
            public void actionPerformed (ActionEvent e) {
                RavTechDK.gizmoHandler.setExclusiveGizmo(component);
            }
        }, "Edit Collider", "Edit");
        panel.addButton(new ActionListener() {

            @Override
            public void actionPerformed (ActionEvent event) {
                SpriteRenderer spriteRenderer = (SpriteRenderer)component.getParent().getComponentByType(ComponentType.SpriteRenderer);
                if(spriteRenderer != null) {
                    component.setRadius(spriteRenderer.height / 2);
                    component.setPosition(-spriteRenderer.originX * spriteRenderer.width / 2, -spriteRenderer.originY * spriteRenderer.height / 2);
                }
            }
        }, "Auto Fit", "Fit");
        return panel;
    }

    public static ComponentPanel boxCollider (BoxCollider component) {
        final ComponentPanel panel = new ComponentPanel(component.getParent(), component);
        addColliderPanels(panel);
        panel.addSliderLabel("x");
        panel.addSliderLabel("y");
        panel.addSliderLabel("width");
        panel.addSliderLabel("height");
        panel.addSliderLabel("angle");
        return panel;
    }

    public static ComponentPanel spriteRenderer (final SpriteRenderer component) {
        ComponentPanel panel = new ComponentPanel(component.getParent(), component);
        panel.addSliderLabel("width");
        panel.addSliderLabel("height");
        panel.addFileSelector("texture");
        panel.addSliderLabel("srcX");
        panel.addSliderLabel("srcY");
        panel.addSliderLabel("srcWidth");
        panel.addSliderLabel("srcHeight");
        panel.addSliderLabel("originX");
        panel.addSliderLabel("originY");
        panel.addDropdown("minFilter", new String[] {"Linear", "Nearest"});
        panel.addDropdown("magFilter", new String[] {"Linear", "Nearest"});
        Array<SortingLayer> layers = RavTech.currentScene.renderProperties.sortingLayers;
        String[] layernames = new String[layers.size];
        for (int i = 0; i < layers.size; i++)
            layernames[i] = layers.get(i).name;
        panel.addDropdown("sortingLayerName", layernames);
        panel.addSliderLabel("sortingOrder");
        panel.addColorPicker("tint");
        panel.addDropdown("uTextureWrap", new String[] {"ClampToEdge", "Repeat", "MirroredRepeat"});
        panel.addDropdown("vTextureWrap", new String[] {"ClampToEdge", "Repeat", "MirroredRepeat"});
        return panel;
    }

    public static JPanel LayersPanel () {
        return new ComponentPanel(null, null);
    }
}

class ComboBoxUI extends BasicComboBoxUI {

    public static ComponentUI createUI (JComponent c) {
        return new ComboBoxUI();
    }

    protected JButton createArrowButton () {
        JButton button = new BasicArrowButton(SwingConstants.EAST);
        return button;
    }
}
