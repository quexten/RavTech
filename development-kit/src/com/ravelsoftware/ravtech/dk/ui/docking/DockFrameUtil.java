package com.ravelsoftware.ravtech.dk.ui.docking;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionType;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.view.ActionViewConverter;
import bibliothek.gui.dock.action.view.ViewGenerator;
import bibliothek.gui.dock.action.view.ViewTarget;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.action.core.CommonDockAction;
import bibliothek.gui.dock.themes.basic.action.BasicTitleViewItem;
import bibliothek.gui.dock.themes.basic.action.dropdown.DropDownViewItem;
import bibliothek.gui.dock.themes.basic.action.menu.MenuViewItem;
import bibliothek.gui.dock.title.DockTitle.Orientation;

public class DockFrameUtil {

    /*
     * In the Common API an action is always a "CAction". "CAction" is just a wrapper around a DockAction, and in our case we do
     * not need any complex logic inside of the CAction. We can use this wrapper to present a nice API to the client.
     */
    public static class CJComponentAction extends CAction {

        private JComponentAction JComponentAction;

        public CJComponentAction() {
            super(null);
            /*
             * CActions delegate all their work to a DockAction. The best place to create and set this DockAction is in the
             * constructor.
             */
            JComponentAction = new JComponentAction(this);
            init(JComponentAction);
        }

        public String getText () {
            return JComponentAction.getText();
        }

        public void setText (String text) {
            JComponentAction.setText(text);
        }
    }

    /*
     * The DockAction "JComponentAction" is our model of a JComponent. The model of a JComponent consists of one String, the
     * "text". Notice the annotation "ButtonContentAction", this will cause a JComponent to appear on the button of a minimized
     * CDockable. In a real application you would probably remove this annotation.
     */
    private static class JComponentAction implements DockAction, CommonDockAction {

        /*
         * And with help of a ChangeListener our view can be informed when the model changes.
         */
        private List<ChangeListener> listeners = new ArrayList<ChangeListener>();
        /* This is the all important data: the text */
        private String text = "";
        /* This is the CAction that delegates all its work to this DockAction */
        private CJComponentAction wrapper;

        public JComponentAction(CJComponentAction wrapper) {
            this.wrapper = wrapper;
        }

        /*
         * There is not much to say about the next few methods: text can be set, and listeners are called if the text changes.
         */
        public void addChangeListener (ChangeListener listener) {
            listeners.add(listener);
        }

        /*
         * The methods "bind" and "unbind" inform the action that it is actually shown somewhere. As this DockAction does not
         * depend on any outside sources, they are not interesting.
         */
        public void bind (Dockable dockable) {
            // ignore
        }

        /*
         * Now this method is interesting. The model is responsible for creating its own view. The proper implementation of this
         * method is just to forward the call to the "ActionViewConverter". Subclasses may however override this method and tweak
         * the view.
         */
        public <V> V createView (ViewTarget<V> target, ActionViewConverter converter, Dockable dockable) {
            return converter.createView(TEXT_FIELD_TYPE, this, target, dockable);
        }

        public CAction getAction () {
            return wrapper;
        }

        public String getText () {
            return text;
        }

        public void removeChangeListener (ChangeListener listener) {
            listeners.remove(listener);
        }

        public void setText (String text) {
            this.text = text;
            ChangeEvent event = new ChangeEvent(this);
            for (ChangeListener listener : listeners)
                listener.stateChanged(event);
        }

        /*
         * "trigger" is the method usually called when the user clicks a button. For a text field it might be called if the user
         * hits "enter", but in this example we are going to ignore it.
         */
        public boolean trigger (Dockable dockable) {
            return false;
        }

        public void unbind (Dockable dockable) {
            // ignore
        }
    }

    private static class JComponentInDropDownGenerator implements ViewGenerator<JComponentAction, DropDownViewItem> {

        public DropDownViewItem create (ActionViewConverter converter, JComponentAction action, Dockable dockable) {
            return null;
        }
    }

    /*
     * ... although for menus and drop-down-menus we are not creating views. But the factories are required anyway.
     */
    private static class JComponentInMenuGenerator implements ViewGenerator<JComponentAction, MenuViewItem<JComponent>> {

        public MenuViewItem<JComponent> create (ActionViewConverter converter, JComponentAction action, Dockable dockable) {
            return null;
        }
    }

    /*
     * At the very end we can define the factories which will create new views for our new type of action.
     */
    private static class JComponentOnTitleGenerator implements ViewGenerator<JComponentAction, BasicTitleViewItem<JComponent>> {

        JComponent component;

        public JComponentOnTitleGenerator(JComponent component) {
            this.component = component;
        }

        @Override
        public BasicTitleViewItem<JComponent> create (ActionViewConverter converter, JComponentAction action, Dockable dockable) {
            return new JComponentView(dockable, action, component);
        }
    }

    /*
     * We have our model, the JComponentAction, but now we need a way to actually show the text on screen. The JComponentView is a
     * JJComponent showing the text of the action.
     */
    private static class JComponentView
        implements BasicTitleViewItem<JComponent>, ChangeListener, DocumentListener, ActionListener {

        /* And this is the model */
        private JComponentAction action;
        /* This is the component we are placing on the title */
        private JComponent component;
        /* This is the Dockable using this action */
        private Dockable dockable;
        /*
         * Some flags to prevent our methods from running into a StackTraceException
         */
        private boolean updatingAction = false;

        public JComponentView(Dockable dockable, JComponentAction action, JComponent c) {
            this.dockable = dockable;
            this.action = action;
            this.component = c;
        }

        public void actionPerformed (ActionEvent e) {
            action.trigger(dockable);
        }

        /*
         * This method is called if the view is made visible. We create and wire all the Components we need at this place.
         */
        public void bind () {
            action.addChangeListener(this);
        }

        /*
         * The next few methods deal with changes in the model and on the view: - If the user enters some text we forward the new
         * text to the action. - If the text of the action changed, we update the text in "JComponent".
         */
        public void changedUpdate (DocumentEvent e) {
        }

        public DockAction getAction () {
            return action;
        }

        /*
         * And finally some methods required by the interface BasicTitleViewItem.
         */
        public JComponent getItem () {
            return component;
        }

        public void insertUpdate (DocumentEvent e) {
            changedUpdate(e);
        }

        public void removeUpdate (DocumentEvent e) {
            changedUpdate(e);
        }

        public void setBackground (Color background) {
            // not supported
        }

        public void setForeground (Color foreground) {
            // not supported
        }

        public void setOrientation (Orientation orientation) {
        }

        public void stateChanged (ChangeEvent e) {
            if (!updatingAction) try {
            } finally {
            }
        }

        /*
         * And this method is called once this view is no longer visible, we can clean up all the listeners.
         */
        public void unbind () {
            action.removeChangeListener(this);
            component = null;
        }
    }

    /*
     * As we are going to introduce a new type of action, we need an identifier for this kind of action. The identifier is
     * necessary to find appropriate factories when creating the view of the action.
     */
    public static final ActionType<JComponentAction> TEXT_FIELD_TYPE = new ActionType<DockFrameUtil.JComponentAction>(
        "text field");

    public static DefaultSingleCDockable getDockable (String id, String title, Component component, ImageIcon icon,
        CControl control, JComponent titleComponent) {
        ActionViewConverter converter = control.getController().getActionViewConverter();
        converter.putDefault(TEXT_FIELD_TYPE, ViewTarget.TITLE, new JComponentOnTitleGenerator(titleComponent));
        converter.putDefault(TEXT_FIELD_TYPE, ViewTarget.MENU, new JComponentInMenuGenerator());
        converter.putDefault(TEXT_FIELD_TYPE, ViewTarget.DROP_DOWN, new JComponentInDropDownGenerator());
        /*
         * This is the action representing a JComponent. As you see, it is really easy to create...
         */
        CJComponentAction sharedAction = new CJComponentAction();
        /*
         * ... and to apply to some Dockables. Note that we forward the same action to both Dockables, as a result editing the
         * text on one Dockable will change the text on the other as well.
         */
        DefaultSingleCDockable dockable = new DefaultSingleCDockable(id, title, component, sharedAction);
        dockable.getColors().setColor("title.background", new Color(228, 228, 228, 255));
        dockable.getColors().setColor("title.foreground", Color.GRAY);
        dockable.getColors().setColor("title.background.focused", new Color(180, 180, 180, 255));
        dockable.getColors().setColor("title.foreground.focused", Color.BLACK);
        if (icon != null) dockable.setTitleIcon(icon);
        dockable.setTitleText(title);
        dockable.setCloseable(false);
        dockable.setExternalizable(false);
        dockable.setMaximizable(true);
        dockable.setMinimizable(false);
        dockable.add(component);
        return dockable;
    }
}
