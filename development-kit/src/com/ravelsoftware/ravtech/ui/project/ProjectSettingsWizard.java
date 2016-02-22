package com.ravelsoftware.ravtech.ui.project;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.badlogic.gdx.utils.ObjectMap;
import com.ravelsoftware.ravtech.dk.RavTechDK;
import com.ravelsoftware.ravtech.dk.ui.components.FileChooserTextField;
import com.ravelsoftware.ravtech.dk.ui.components.TextLabelPair;
import com.ravelsoftware.ravtech.project.Project;

public class ProjectSettingsWizard extends JFrame {

    private static final long serialVersionUID = 6115199948289342131L;
    GridBagConstraints constraints = new GridBagConstraints();
    ObjectMap<String, JTextField> fieldMap = new ObjectMap<String, JTextField>();
    boolean isCreation;
    FileChooserTextField fileChooser;
    public ActionListener onSaveListener;

    public ProjectSettingsWizard(boolean creation) {
        this.isCreation = creation;
    }

    public void show (final Project project) {
        this.setLayout(new GridBagLayout());
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.weightx = 1;
        constraints.insets = new Insets(10, 10, 0, 10);
        if (isCreation) {
            constraints.gridwidth = 3;
            fileChooser = new FileChooserTextField("Directory:   ", new File(System.getProperty("user.home")), this,
                JFileChooser.DIRECTORIES_ONLY);
            this.add(fileChooser, constraints);
            constraints.gridwidth = 1;
            constraints.gridy++;
        }
        addTextLabelPair("Developer Name:", project.developerName);
        addTextLabelPair("App Name:", project.appName);
        addTextLabelPair("BuildVersion:", String.valueOf(project.buildVersion));
        addTextLabelPair("MajorVersion:", String.valueOf(project.majorVersion));
        addTextLabelPair("MinorVersion:", String.valueOf(project.minorVersion));
        addTextLabelPair("MicroVersion:", String.valueOf(project.microVersion));
        addTextLabelPair("StartScene:", project.startScene);
        constraints.gridx = 1;
        constraints.anchor = GridBagConstraints.EAST;
        JPanel panel = new JPanel();
        panel.add(Box.createHorizontalStrut(332), BorderLayout.WEST);
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed (ActionEvent event) {
                project.developerName = fieldMap.get("Developer Name:").getText();
                project.appName = fieldMap.get("App Name:").getText();
                project.buildVersion = Integer.valueOf(fieldMap.get("BuildVersion:").getText());
                project.majorVersion = Integer.valueOf(fieldMap.get("MajorVersion:").getText());
                project.minorVersion = Integer.valueOf(fieldMap.get("MinorVersion:").getText());
                project.microVersion = Integer.valueOf(fieldMap.get("MicroVersion:").getText());
                project.startScene = fieldMap.get("StartScene:").getText();
                if (ProjectSettingsWizard.this.isCreation)
                    RavTechDK.createProject(fileChooser.field.getText(), project);
                else {
                    project.save(RavTechDK.projectHandle);
                }
                if (onSaveListener != null) {
                    onSaveListener.actionPerformed(new ActionEvent(this, 0, fileChooser.field.getText()));
                }
            }
        });
        panel.add(saveButton, BorderLayout.EAST);
        panel.setBorder(new EmptyBorder(0, 0, 0, 0));
        this.add(panel, constraints);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dimension.width / 2 - 225, dimension.height / 2 - 165);
        this.setSize(550, 330);
        this.setVisible(true);
        this.setResizable(false);
        this.setTitle("Project Settings");
    }

    void addTextLabelPair (String name, String initialValue) {
        TextLabelPair label = new TextLabelPair(name, initialValue);
        constraints.weightx = 1;
        this.add(label.nameLabel, constraints);
        constraints.gridx++;
        constraints.weightx = 8;
        this.add(label.pairedComponent, constraints);
        constraints.gridx = 0;
        constraints.gridy++;
        fieldMap.put(name, (JTextField)label.pairedComponent);
    }
}
