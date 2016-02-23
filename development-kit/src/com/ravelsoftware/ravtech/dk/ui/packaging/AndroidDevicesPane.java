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
package com.ravelsoftware.ravtech.dk.ui.packaging;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

import com.badlogic.gdx.utils.Array;
import com.ravelsoftware.ravtech.dk.RavTechDK;
import com.ravelsoftware.ravtech.dk.adb.AdbManager;
import com.ravelsoftware.ravtech.dk.packaging.Packager;
import com.ravelsoftware.ravtech.dk.packaging.Packager.TargetPlatform;

import se.vidstige.jadb.JadbDevice;

public class AndroidDevicesPane extends JPanel {

    private static final long serialVersionUID = 3207271971684413098L;
    JList<String> deviceList;

    public AndroidDevicesPane() {
        setLayout(new BorderLayout());
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.setPreferredSize(new Dimension(getWidth(), 32));
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JButton selectButton = new JButton("Select");
        bottomPanel.add(selectButton);
        JButton refreshButton = new JButton("Refresh");
        bottomPanel.add(refreshButton);
        LineBorder border = new LineBorder(Color.LIGHT_GRAY) {

            private static final long serialVersionUID = 6617679157612189390L;

            public void paintBorder (Component c, Graphics g, int x, int y, int width, int height) {
                Color oldColor = g.getColor();
                g.setColor(lineColor);
                g.drawLine(width - 1, 0, width - 1, height);
                g.setColor(oldColor);
            }
        };
        deviceList = new JList<String>();
        deviceList.setFont(new Font("Arial", Font.BOLD, 14));
        deviceList.setBackground(new Color(239, 239, 239));
        deviceList.setForeground(Color.DARK_GRAY);
        JScrollPane scrollPane = new JScrollPane(deviceList);
        scrollPane.setBorder(border);
        add(scrollPane, BorderLayout.CENTER);
        selectButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed (ActionEvent event) {
                Packager.run(RavTechDK.ui.buildWizard.buildReporterDialog, TargetPlatform.Android,
                    deviceList.getSelectedValue().substring(0, deviceList.getSelectedValue().indexOf(" ")));
                RavTechDK.ui.buildWizard.show("BuildReporterDialog");
            }
        });
        refreshButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed (ActionEvent event) {
                DefaultListModel<String> model = new DefaultListModel<String>();
                Array<JadbDevice> devices = AdbManager.getDevices();
                for (int i = 0; i < devices.size; i++)
                    try {
                        model.addElement(devices.get(i).getSerial() + " " + AdbManager.getDeviceName(devices.get(i)));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                deviceList.setModel(model);
            }
        });
        this.setVisible(true);
    }

    public void refreshDevices () {
        Array<JadbDevice> devices = AdbManager.getDevices();
        String[] deviceStringList = new String[devices.size];
        for (int i = 0; i < devices.size; i++)
            try {
                deviceStringList[i] = devices.get(i).getSerial() + "-" + AdbManager.getDeviceName(devices.get(i));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        this.deviceList.setListData(deviceStringList);
    }
}
