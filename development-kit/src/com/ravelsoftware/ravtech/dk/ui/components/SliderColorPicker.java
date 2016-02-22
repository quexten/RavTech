package com.ravelsoftware.ravtech.dk.ui.components;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.badlogic.gdx.graphics.Color;
import com.ravelsoftware.ravtech.dk.RavTechDK;
import com.ravelsoftware.ravtech.dk.ui.utils.ColorUtils;

public class SliderColorPicker extends SliderComponentPair {

    public SliderColorPicker(String tag, Color value) {
        super(tag);
        nameLabel.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged (MouseEvent arg0) {
                if (arg0.getXOnScreen() > Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 5) {
                    screenPassBuffer += Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 10;
                    try {
                        Robot robot = new Robot();
                        robot.mouseMove(6, arg0.getYOnScreen());
                    } catch (AWTException e) {
                        e.printStackTrace();
                    }
                } else if (arg0.getXOnScreen() < 5) {
                    screenPassBuffer -= Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 10;
                    try {
                        Robot robot = new Robot();
                        robot.mouseMove((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 6), arg0.getYOnScreen());
                    } catch (AWTException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void mouseMoved (MouseEvent arg0) {
            }
        });
        nameLabel.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked (MouseEvent arg0) {
            }

            @Override
            public void mouseEntered (MouseEvent arg0) {
            }

            @Override
            public void mouseExited (MouseEvent arg0) {
            }

            @Override
            public void mousePressed (MouseEvent arg0) {
            }

            @Override
            public void mouseReleased (MouseEvent arg0) {
            }
        });
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        pairedComponent = new JPanel();
        pairedComponent.setBackground(ColorUtils.gdxToSwing(value));
        pairedComponent.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked (MouseEvent e) {
                JColorChooser cc = new JColorChooser();
                AbstractColorChooserPanel[] panels = cc.getChooserPanels();
                for (final AbstractColorChooserPanel accp : panels)
                    if (accp.getDisplayName().equals("HSV")) {
                        final JPopupMenu menu = new JPopupMenu();
                        menu.add(accp);
                        menu.show(RavTechDK.ui.ravtechDKFrame, 50, 50 + pairedComponent.getHeight());
                        accp.getColorSelectionModel().addChangeListener(new ChangeListener() {

                            @Override
                            public void stateChanged (ChangeEvent arg0) {
                            }
                        });
                        final AWTEventListener listener = new AWTEventListener() {

                            @Override
                            public void eventDispatched (AWTEvent arg0) {
                                if (arg0.getID() == MouseEvent.MOUSE_RELEASED) pairedComponent.firePropertyChange("Color",
                                    Color.rgba8888(ColorUtils.swingToGdx(pairedComponent.getBackground())),
                                    Color.rgba8888(ColorUtils.swingToGdx(accp.getColorSelectionModel().getSelectedColor())));
                                pairedComponent.setBackground(accp.getColorSelectionModel().getSelectedColor());
                            }
                        };
                        Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.MOUSE_EVENT_MASK);
                        menu.addPopupMenuListener(new PopupMenuListener() {

                            @Override
                            public void popupMenuCanceled (PopupMenuEvent arg0) {
                                Toolkit.getDefaultToolkit().removeAWTEventListener(listener);
                            }

                            @Override
                            public void popupMenuWillBecomeInvisible (PopupMenuEvent arg0) {
                            }

                            @Override
                            public void popupMenuWillBecomeVisible (PopupMenuEvent arg0) {
                            }
                        });
                    }
            }

            @Override
            public void mouseEntered (MouseEvent e) {
            }

            @Override
            public void mouseExited (MouseEvent e) {
            }

            @Override
            public void mousePressed (MouseEvent e) {
            }

            @Override
            public void mouseReleased (MouseEvent e) {
            }
        });
        pairedComponent.setPreferredSize(new Dimension(175, 20));
        pairedComponent.setMaximumSize(new Dimension(175, 20));
    }
}
