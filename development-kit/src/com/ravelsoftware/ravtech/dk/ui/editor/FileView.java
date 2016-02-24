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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JRootPane;

import org.fife.ui.breadcrumbbar.BreadcrumbBar;
import org.fife.ui.rtextfilechooser.FileSelector;
import org.fife.ui.rtextfilechooser.RTextFileChooser;

import com.ravelsoftware.ravtech.dk.actions.OpenFileAction;
import com.ravelsoftware.ravtech.dk.ui.utils.IconUtil;

public class FileView {

    public BreadcrumbBar breadcrumbBar;
    public RTextFileChooser chooser;
    public Component component;

    public FileView() {
        JFrame frame = new JFrame();
        frame.setSize(new Dimension(200, 100));
        chooser = new RTextFileChooser(false) {

            private static final long serialVersionUID = -7902223987246244389L;

            @Override
            public void approveSelection () {
                Field field;
                try {
                    field = chooser.getClass().getSuperclass().getDeclaredField("view");
                    field.setAccessible(true);
                    FileSelector selector = (FileSelector)field.get(chooser);
                    if (!selector.getSelectedFile().isDirectory())
                        FileView.this.approveSelection();
                    else
                        super.approveSelection();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        frame.add(chooser);
        // chooser.showOpenDialog(Launcher.ravtechDK);
        Method m = null;
        try {
            m = chooser.getClass().getSuperclass().getDeclaredMethod("initializeGUIComponents");
        } catch (NoSuchMethodException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (SecurityException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        m.setAccessible(true);
        try {
            m.invoke(chooser);
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Component component = getComponent(((JRootPane)frame.getComponents()[0]).getComponents(), 0,
            "org.fife.ui.rtextfilechooser.ListView");
        this.component = component;
        this.breadcrumbBar = (BreadcrumbBar)getComponent(((JRootPane)frame.getComponents()[0]).getComponents(), 0,
            "org.fife.ui.breadcrumbbar.BreadcrumbBar");
        Map<File, Icon> map = new HashMap<File, Icon>(50) {

            private static final long serialVersionUID = 3807080296311092626L;

            @SuppressWarnings("resource")
            @Override
            public Icon put (File file, Icon icon) {
                Icon previousIcon = this.get(file);
                if (file.getName().endsWith(".prefab"))
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        if (br.readLine() == null)
                            icon = IconUtil.getIcon("bullet_black.png");
                        else
                            icon = IconUtil.getIcon("bullet_blue.png");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                else if (file.getName().endsWith(".map"))
                    icon = IconUtil.getIcon("page_white_world");
                else if (file.getName().endsWith(".lua"))
                    icon = IconUtil.getIcon("page_white_code");
                else if (file.getName().endsWith(".png"))
                    icon = IconUtil.getIcon("picture");
                else if (file.getName().endsWith(".ogg"))
                    icon = IconUtil.getIcon("sound");
                else if (file.getName().endsWith(".vert"))
                    icon = IconUtil.getIcon("page_shader_vertex");
                else if (file.getName().endsWith(".frag")) icon = IconUtil.getIcon("page_shader_fragment");
                super.put(file, icon);
                return previousIcon;
            }
        };
        Field iconManagerField = null;
        try {
            iconManagerField = chooser.getClass().getSuperclass().getDeclaredField("iconManager");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        iconManagerField.setAccessible(true);
        Object fileChooserIconManager = null;
        try {
            fileChooserIconManager = iconManagerField.get(chooser);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Field iconCacheField = null;
        try {
            iconCacheField = fileChooserIconManager.getClass().getDeclaredField("iconCache");
        } catch (NoSuchFieldException e1) {
            e1.printStackTrace();
        } catch (SecurityException e1) {
            e1.printStackTrace();
        }
        iconCacheField.setAccessible(true);
        try {
            iconCacheField.set(fileChooserIconManager, map);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        // chooser.setCurrentDirectory(RavTechDKUtil.getWorkingDirectory());
        // frame.setVisible(true);
    }

    public void approveSelection () {
        try {
            Field field = chooser.getClass().getSuperclass().getDeclaredField("view");
            field.setAccessible(true);
            FileSelector selector = (FileSelector)field.get(chooser);
            new OpenFileAction(selector.getSelectedFile()).run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    Component getComponent (java.awt.Component[] components, int level, String className) {
        Component tempComponent = null;
        for (java.awt.Component component : components) {
            for (int i = 0; i <= level; i++)
                System.out.print("-");
            //
            if (component.getClass().getName().equals(className)) {
                tempComponent = component;
                break;
            }
            if (component instanceof Container) {
                Component recursivecomponent = getComponent(((Container)component).getComponents(), level + 1, className);
                if (recursivecomponent != null) {
                    tempComponent = recursivecomponent;
                    break;
                }
            }
        }
        return tempComponent;
    }

    public void setPath (String path) {
        chooser.setCurrentDirectory(path);
    }
}
