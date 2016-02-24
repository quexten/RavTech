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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;

import com.ravelsoftware.ravtech.history.ChangeManager;
import com.ravelsoftware.ravtech.history.Changeable;

public class HistoryView extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 2540738533816039579L;

    protected static void redo () {
        ChangeManager.redo();
    }

    protected static void undo () {
        ChangeManager.undo();
    }

    public HistoryView() {
        final JList<String> list = new JList<String>();
        this.add(new JScrollPane(list) {

            /**
             *
             */
            private static final long serialVersionUID = -2641260926431647767L;

            @Override
            public Dimension getPreferredSize () {
                return new Dimension(260, 260);
            }
        }, BorderLayout.CENTER);
        JPanel panel = new JPanel();
        final JButton buttonDo = new JButton("Do");
        buttonDo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed (ActionEvent arg0) {
                HistoryView.redo();
            }
        });
        final JButton buttonUndo = new JButton("Undo");
        buttonUndo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed (ActionEvent arg0) {
                HistoryView.undo();
            }
        });
        panel.add(buttonDo, BorderLayout.WEST);
        panel.add(buttonUndo, BorderLayout.EAST);
        new Timer(100, new ActionListener() {

            @Override
            public void actionPerformed (ActionEvent arg0) {
                DefaultListModel<String> resultList = new DefaultListModel<String>();
                list.setModel(resultList);
                for (Changeable changeable : ChangeManager.changeables)
                    resultList.addElement(changeable.getChangeLabel());
                list.setSelectedIndex(ChangeManager.currentChangeable - 1);
                buttonDo.setEnabled(ChangeManager.canRedo());
                buttonUndo.setEnabled(ChangeManager.canUndo());
            }
        }).start();
        this.add(panel, BorderLayout.SOUTH);
    }
}
