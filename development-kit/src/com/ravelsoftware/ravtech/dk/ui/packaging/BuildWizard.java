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
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.badlogic.gdx.utils.Array;
import com.ravelsoftware.ravtech.dk.packaging.platforms.Platform;
import com.ravelsoftware.ravtech.dk.ui.editor.MessageConsole;
import com.ravelsoftware.ravtech.dk.ui.editor.MessageConsole.ConsoleOutputStream;
import com.ravelsoftware.ravtech.dk.ui.utils.IconUtil;

public class BuildWizard extends JDialog {

    public class BuildReporterDialog extends JPanel {

        private static final long serialVersionUID = -7122945076305274439L;
        public MessageConsole console;
        final ConsoleOutputStream errorOutputStream;
        final ConsoleOutputStream logOutputStream;
        public Array<PrinterListener> printerListeners = new Array<PrinterListener>();

        public BuildReporterDialog() {
            this.setLayout(new BorderLayout());
            JPanel bottomPanel = new JPanel();
            bottomPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            add(bottomPanel, BorderLayout.SOUTH);
            bottomPanel.setPreferredSize(new Dimension(getWidth(), 32));
            bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
            JButton backButton = new JButton("Back");
            bottomPanel.add(backButton);
            backButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed (ActionEvent evt) {
                    BuildWizard.this.cardLayout.show(BuildWizard.this.getContentPane(), "PlatformPane");
                }
            });
            JTextArea area = new JTextArea();
            console = new MessageConsole(area);
            this.add(new JScrollPane(area), BorderLayout.CENTER);
            logOutputStream = console.new ConsoleOutputStream(Color.BLACK, null);
            errorOutputStream = console.new ConsoleOutputStream(Color.RED, null);
        }

        public void log (String line) {
            for (int i = 0; i < printerListeners.size; i++)
                printerListeners.get(i).onPrint(line);
            if (!line.isEmpty()) logOutputStream.handleAppend("error: " + line + "\n");
        }

        public void logError (String line) {
            for (int i = 0; i < printerListeners.size; i++)
                printerListeners.get(i).onPrint(line);
            if (!line.isEmpty()) errorOutputStream.handleAppend("log: " + line + "\n");
        }

        public void run (Platform platform, boolean run) {
            if (!run) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (fileChooser.showSaveDialog(this) == JFileChooser.CANCEL_OPTION) return;
                if (!platform.build(fileChooser.getSelectedFile(), this)) return;
            } else if (!platform.run(this)) return;
        }
    }

    public static abstract class PrinterListener {

        public abstract void onPrint (String line);
    }

    private static final long serialVersionUID = -5696518299115083474L;
    public AndroidDevicesPane androidDevicesPane = new AndroidDevicesPane();
    public BuildReporterDialog buildReporterDialog = new BuildReporterDialog();
    CardLayout cardLayout;
    public PlatformPane platformPane = new PlatformPane();

    public BuildWizard(JFrame parent) {
        super(parent, "Build");
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        setSize(new Dimension(600, 400));
        this.setLocationRelativeTo(parent);
        add(platformPane, "PlatformPane");
        add(androidDevicesPane, "AndroidDevicesPane");
        add(buildReporterDialog, "BuildReporterDialog");
        this.setIconImage(IconUtil.getIcon("package_go").getImage());
    }

    public void show (String name) {
        cardLayout.show(this.getContentPane(), name);
    }
}
