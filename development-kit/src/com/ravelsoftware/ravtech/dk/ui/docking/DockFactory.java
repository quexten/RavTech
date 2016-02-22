package com.ravelsoftware.ravtech.dk.ui.docking;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import bibliothek.extension.gui.dock.theme.FlatTheme;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.themes.NoStackTheme;
import bibliothek.gui.dock.util.IconManager;

public class DockFactory {

    public static DefaultSingleCDockable create (String id, String title, Component component) {
        DefaultSingleCDockable dockable = new DefaultSingleCDockable(id, title);
        dockable.getColors().setColor("title.background", new Color(228, 228, 228, 255));
        dockable.getColors().setColor("title.foreground", Color.GRAY);
        dockable.getColors().setColor("title.background.focused", new Color(180, 180, 180, 255));
        dockable.getColors().setColor("title.foreground.focused", Color.BLACK);
        dockable.setTitleText(title);
        dockable.setCloseable(false);
        dockable.setMaximizable(false);
        dockable.setMinimizable(false);
        dockable.setExternalizable(false);
        dockable.add(component);
        return dockable;
    }

    public static SingleCDockable create (String id, String title, Component component, ImageIcon icon) {
        DefaultSingleCDockable dockable = create(id, title, component);
        dockable.setTitleIcon(icon);
        return dockable;
    }

    @SuppressWarnings("deprecation")
    public static CControl dockFrame (JFrame frame) {
        CControl control = new CControl(frame);
        FlatTheme theme = new FlatTheme();
        control.setTheme(new NoStackTheme(theme));
        IconManager icons = control.getController().getIcons();
        Icon iconExternalize = new ImageIcon("resources//icons//arrow_out.png");
        icons.setIconClient("locationmanager.maximize", iconExternalize);
        Icon iconUnexternalize = new ImageIcon("resources//icons//arrow_in.png");
        icons.setIconClient("locationmanager.normalize", iconUnexternalize);
        return control;
    }
}
