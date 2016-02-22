package com.ravelsoftware.ravtech.dk.ui.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.components.Animator;
import com.ravelsoftware.ravtech.components.AudioEmitter;
import com.ravelsoftware.ravtech.components.BoxCollider;
import com.ravelsoftware.ravtech.components.CircleCollider;
import com.ravelsoftware.ravtech.components.GameComponent;
import com.ravelsoftware.ravtech.components.GameObject;
import com.ravelsoftware.ravtech.components.Light;
import com.ravelsoftware.ravtech.components.Rigidbody;
import com.ravelsoftware.ravtech.components.ScriptComponent;
import com.ravelsoftware.ravtech.components.SpriteRenderer;
import com.ravelsoftware.ravtech.dk.RavTechDKUtil;
import com.ravelsoftware.ravtech.dk.ui.components.SliderColorPicker;
import com.ravelsoftware.ravtech.dk.ui.editor.Panels.ComponentPanel;

public class Inspector extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = -2367124656714397021L;

    public enum InspectableType {
        GameComponents, RenderSettings
    }

    public Inspector() {
        super();
        BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        this.setLayout(layout);
        this.add(Box.createHorizontalGlue());
        rebuild();
        new Timer(100, new ActionListener() {

            @Override
            public void actionPerformed (ActionEvent arg0) {
                if (RavTechDKUtil.hasInspectorChanged()) {
                    RavTechDKUtil.inspectorSynced();
                    Inspector.this.rebuild();
                }
            }
        }).start();
    }

    public void addComponent (String name, ComponentPanel panel) {
        this.add(new CollapsablePanel(name, panel));
    }

    public Dimension getMinimumSize () {
        return new Dimension(210, super.getMinimumSize().height);
    }

    @Override
    public Dimension getPreferredSize () {
        int height = 0;
        for (Component comp : this.getComponents())
            height += comp.getPreferredSize().height;
        return new Dimension(300, height);
    }

    @Override
    public void paint (Graphics g) {
        super.paint(g);
        Graphics2D gg = (Graphics2D)g.create();
        new Line2D.Double(0, getHeight() - 1, getWidth(), getHeight() - 1);
        gg.setStroke(new BasicStroke(1));
        gg.setColor(new Color(196, 196, 196, 255));
        // gg.dispose();
    }

    public void rebuild () {
        this.removeAll();
        switch (RavTechDKUtil.inspectableType) {
            case GameComponents:
                if (RavTechDKUtil.selectedObjects.size > 0 && RavTechDKUtil.selectedObjects.get(0) != null) {
                    this.addComponent("Transform", Panels.transform(RavTechDKUtil.selectedObjects.get(0)));
                    for (GameComponent component : RavTechDKUtil.selectedObjects.get(0).getComponents()) {
                        if (component instanceof SpriteRenderer)
                            this.addComponent("SpriteRenderer", Panels.spriteRenderer((SpriteRenderer)component));
                        if (component instanceof Rigidbody) this.addComponent("Physics", Panels.rigidbody((Rigidbody)component));
                        if (component instanceof GameObject)
                            this.addComponent("GameObject", Panels.gameObject((GameObject)component));
                        if (component instanceof Light) this.addComponent("Light", Panels.light((Light)component));
                        if (component instanceof ScriptComponent)
                            this.addComponent("ScriptComponent", Panels.scriptComponent((ScriptComponent)component));
// if (component instanceof RavNetView)
// this.addComponent("RavNetViewComponent",
// Panels.RavNetViewComponentPanel((RavNetView)component));
// if (component instanceof ParticleSystemRenderer) this.addComponent("Particle
// System",
// Panels.ParticleSystemComponentPanel((ParticleSystemRenderer)component));
                        if (component instanceof AudioEmitter)
                            this.addComponent("AudioEmitter", Panels.audioEmitter((AudioEmitter)component));
// if (component instanceof SpriterRenderer)
// this.addComponent("SpriterRenderer",
// Panels.SpriterRendererComponentPanel((SpriterRenderer)component));
                        if (component instanceof Animator)
                            this.addComponent("Animator", Panels.animatorComponent((Animator)component));
                        if (component instanceof CircleCollider)
                            this.addComponent("CircleCollider", Panels.circleCollider((CircleCollider)component));
                        if (component instanceof BoxCollider)
                            this.addComponent("BoxCollider", Panels.boxCollider((BoxCollider)component));
                        // if (component instanceof PolygonCollider)
                        // this.addComponent("PolygonCollider",
                        // Panels.PolygonCollider((PolygonCollider)component));
// if (component instanceof FontRenderer)
// this.addComponent("FontRenderer",
// Panels.FontRenderere((FontRenderer)component));
                    }
                }
                break;
            case RenderSettings:
                // this.addComponent("Layers",Panels.LayersPanel());
                this.add(Panels.LayersPanel());
                JPanel panel = new JPanel(new GridBagLayout());
                GridBagConstraints c = new GridBagConstraints();
                c.gridx = 0;
                c.gridy = 0;
                final SliderColorPicker colorLabel = new SliderColorPicker("Ambient Color:",
                    RavTech.currentScene.renderProperties.ambientLightColor);
                panel.add(colorLabel.nameLabel, c);
                c.gridx = 1;
                panel.add(colorLabel.pairedComponent, c);
                colorLabel.pairedComponent.addPropertyChangeListener(new PropertyChangeListener() {

                    @Override
                    public void propertyChange (PropertyChangeEvent arg0) {
                        if (arg0.getPropertyName().equals("Color")) {
                            com.badlogic.gdx.graphics.Color newColor = new com.badlogic.gdx.graphics.Color();
                            com.badlogic.gdx.graphics.Color.rgba8888ToColor(newColor,
                                Integer.valueOf(arg0.getNewValue().toString()));
                            RavTech.currentScene.renderProperties.ambientLightColor.set(newColor);
                        }
                    }
                });
                this.add(panel);
                break;
        }
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run () {
                Inspector.this.revalidate();
                Inspector.this.repaint();
            }
        });
        RavTechDKUtil.inspectorSynced();
    }
}
