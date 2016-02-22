package com.ravelsoftware.ravtech.dk.ui.components;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComboBox;

public class SliderDropdownList extends SliderComponentPair {

    int lastX = 0;

    public SliderDropdownList(String tag, String[] values) {
        super(tag);
        nameLabel.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged (MouseEvent arg0) {
                JComboBox<?> box = (JComboBox<?>)pairedComponent;
                if (arg0.getXOnScreen() + screenPassBuffer - lastX > 400) {
                    lastX = arg0.getXOnScreen() + screenPassBuffer;
                    box.setSelectedIndex(box.getSelectedIndex() == box.getItemCount() - 1 ? 0 : box.getSelectedIndex() + 1);
                } else if (arg0.getXOnScreen() + screenPassBuffer - lastX < -400) {
                    lastX = arg0.getXOnScreen() + screenPassBuffer;
                    box.setSelectedIndex(box.getSelectedIndex() == 0 ? box.getItemCount() - 1 : box.getSelectedIndex() - 1);
                }
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
                SliderDropdownList.this.xVal = arg0.getX();
                screenPassBuffer = 0;
            }

            @Override
            public void mouseReleased (MouseEvent arg0) {
            }
        });
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        pairedComponent = new XComboBox<String>(values);
        pairedComponent.setFocusable(false);
        pairedComponent.setPreferredSize(new Dimension(200, 20));
    }
}
