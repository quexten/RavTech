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

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.ravelsoftware.ravtech.dk.RavTechDK;

public class SliderEditableLabel extends SliderComponentPair {

    long lastTime;

    public SliderEditableLabel(String tag, float value) {
        super(tag);
        nameLabel.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged (MouseEvent arg0) {
                RavTechDK.ui.ravtechDKFrame.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                if (System.nanoTime() - lastTime > 10000000) {
                    ((EditableJLabel)pairedComponent)
                        .changeValue(String.valueOf(initialValue + (arg0.getX() - xVal + screenPassBuffer) * changeFactor));
                    lastTime = System.nanoTime();
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
                SliderEditableLabel.this.xVal = arg0.getX();
                initialValue = Float.valueOf(((EditableJLabel)pairedComponent).getText());
                EditableJLabel.oldValue = ((EditableJLabel)pairedComponent).getText();
                screenPassBuffer = 0;
            }

            @Override
            public void mouseReleased (MouseEvent arg0) {
                RavTechDK.ui.ravtechDKFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                ((EditableJLabel)pairedComponent)
                    .finallyChangeValue(String.valueOf(initialValue + (arg0.getX() - xVal + screenPassBuffer) * changeFactor));
            }
        });
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        pairedComponent = new EditableJLabel(String.valueOf(value));
        pairedComponent.setPreferredSize(new Dimension(175, 20));
        pairedComponent.setMaximumSize(new Dimension(175, 20));
    }
}
