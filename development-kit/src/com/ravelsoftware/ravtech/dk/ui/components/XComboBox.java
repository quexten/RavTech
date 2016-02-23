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

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.ComboPopup;

public class XComboBox<T> extends JComboBox<T> {

    private static final long serialVersionUID = -7090701244877006936L;
    private ListSelectionListener listener;
    public ListSelectionListener selectionListener;

    public XComboBox() {
        uninstall();
        install();
    }

    public XComboBox(T[] values) {
        super(values);
        uninstall();
        install();
    }

    @Override
    public void updateUI () {
        uninstall();
        super.updateUI();
        install();
    }

    private void uninstall () {
        if (listener == null) return;
        getPopupList().removeListSelectionListener(listener);
        listener = null;
    }

    protected void install () {
        listener = new ListSelectionListener() {

            @Override
            public void valueChanged (ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
                JList<T> list = getPopupList();
                if (selectionListener != null) {
                    ListSelectionEvent event = new ListSelectionEvent(list.getSelectedValue(), e.getFirstIndex(),
                        e.getLastIndex(), e.getValueIsAdjusting());
                    selectionListener.valueChanged(event);
                }
            }
        };
        getPopupList().addListSelectionListener(listener);
    }

    @SuppressWarnings("unchecked")
    private JList<T> getPopupList () {
        ComboPopup popup = (ComboPopup)getUI().getAccessibleChild(this, 0);
        return popup.getList();
    }

    public void setSelectionListener (ListSelectionListener listener) {
        this.selectionListener = listener;
    }
}
