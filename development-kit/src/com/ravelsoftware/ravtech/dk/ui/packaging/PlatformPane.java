package com.ravelsoftware.ravtech.dk.ui.packaging;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
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
import com.ravelsoftware.ravtech.dk.packaging.platforms.GradleInvoker;
import com.ravelsoftware.ravtech.util.Debug;

import se.vidstige.jadb.JadbDevice;

public class PlatformPane extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 5574945054343284278L;

    public PlatformPane() {
        setLayout(new BorderLayout());
        int width = 600, height = 400;
        setBounds(960 - width / 2, 440 - height / 2, width, height);
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        this.add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.setPreferredSize(new Dimension(getWidth(), 32));
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        final JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed (ActionEvent event) {
                GradleInvoker.Invoke("--stop");
                stopButton.transferFocusBackward();
            }
        });
        bottomPanel.add(stopButton);
        JButton buildButton = new JButton("Build");
        bottomPanel.add(buildButton);
        JButton buildAndRunButton = new JButton("Build and Run");
        bottomPanel.add(buildAndRunButton);
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        JPanel centerPanel = new JPanel(layout);
        centerPanel.getInsets().set(0, 0, 0, 0);
        add(centerPanel, BorderLayout.CENTER);
        TargetPlatform[] systems = new TargetPlatform[] {TargetPlatform.Desktop, TargetPlatform.Windows, TargetPlatform.Mac,
            TargetPlatform.Linux, TargetPlatform.Android, TargetPlatform.iOS, TargetPlatform.WebGL};
        final JList<TargetPlatform> systemsList = new JList<TargetPlatform>(systems);
        systemsList.setFont(new Font("Arial", Font.BOLD, 14));
        systemsList.setBackground(new Color(239, 239, 239));
        systemsList.setForeground(Color.DARK_GRAY);
        systemsList.setBorder(BorderFactory.createEmptyBorder());
        systemsList.setPreferredSize(new Dimension(200, 200));
        JScrollPane scrollPane = new JScrollPane(systemsList);
        LineBorder border = new LineBorder(Color.LIGHT_GRAY) {

            /**
             *
             */
            private static final long serialVersionUID = 1727420985343315369L;

            public void paintBorder (Component c, Graphics g, int x, int y, int width, int height) {
                Color oldColor = g.getColor();
                g.setColor(lineColor);
                g.drawLine(width - 1, 0, width - 1, height);
                g.setColor(oldColor);
            }
        };
        scrollPane.setBorder(border);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.ipadx = 0;
        constraints.ipady = 0;
        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.insets = new Insets(0, 0, 0, 0);
        constraints.anchor = GridBagConstraints.WEST;
        centerPanel.add(scrollPane, constraints);
        buildButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed (ActionEvent event) {
                build(systemsList.getSelectedValue(), false);
            }
        });
        buildAndRunButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed (ActionEvent event) {
                build(systemsList.getSelectedValue(), true);
            }
        });
        JPanel eastPanel = new JPanel();
        eastPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 1;
        constraints.weightx = 2;
        centerPanel.add(eastPanel, constraints);
    }

    public void build (TargetPlatform targetPlatform, boolean run) {
        if (targetPlatform == TargetPlatform.Android) {
            if (!AdbManager.initialized) {
                Debug.logError("Adb Error", "Adb Path Not Delcared");
                AdbManager.initializeAdb();
            }
            Array<JadbDevice> devices = AdbManager.getDevices();
            if (devices.size == 1) {
                Packager.run(RavTechDK.ui.buildWizard.buildReporterDialog, targetPlatform, "");
                RavTechDK.ui.buildWizard.show("BuildReporterDialog");
            } else if (devices.size > 1) RavTechDK.ui.buildWizard.show("AndroidDevicesPane");
            return;
        }
        RavTechDK.ui.buildWizard.show("BuildReporterDialog");
        if (run)
            Packager.run(RavTechDK.ui.buildWizard.buildReporterDialog, targetPlatform, "");
        else
            Packager.dist(RavTechDK.ui.buildWizard.buildReporterDialog, targetPlatform, new File(""));
    }
}
