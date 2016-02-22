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
