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
package com.ravelsoftware.ravtech.dk.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.ravelsoftware.ravtech.components.Animator;
import com.ravelsoftware.ravtech.components.BoxCollider;
import com.ravelsoftware.ravtech.components.CircleCollider;
import com.ravelsoftware.ravtech.components.GameComponent;
import com.ravelsoftware.ravtech.components.GameObject;
import com.ravelsoftware.ravtech.components.Light;
import com.ravelsoftware.ravtech.components.Rigidbody;
import com.ravelsoftware.ravtech.components.SpriteRenderer;
import com.ravelsoftware.ravtech.components.Transform;
import com.ravelsoftware.ravtech.dk.RavTechDKUtil;
import com.ravelsoftware.ravtech.dk.ui.utils.IconUtil;

public class AddComponentAction implements Runnable {

    Component invoker;

    public AddComponentAction(Component invoker) {
        this.invoker = invoker;
    }

    @Override
    public void run () {
        JPopupMenu menu = new JPopupMenu();
        addPopupMenuComponent(menu, Transform.class);
        addPopupMenuComponent(menu, SpriteRenderer.class);
        addPopupMenuComponent(menu, Rigidbody.class);
        addPopupMenuComponent(menu, BoxCollider.class);
        addPopupMenuComponent(menu, CircleCollider.class);
        addPopupMenuComponent(menu, Animator.class);
        addPopupMenuComponent(menu, GameObject.class);
        addPopupMenuComponent(menu, Light.class);
        menu.show(invoker, 0, invoker.getHeight());
    }

    public void addPopupMenuComponent (JPopupMenu menu, final Class<? extends GameComponent> componentClass) {
        JMenuItem item = new JMenuItem(componentClass.getSimpleName());
        item.setIcon(IconUtil.getIconByComponentClass(componentClass));
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed (ActionEvent evt) {
                try {
                    GameComponent component = ClassReflection.newInstance(componentClass);
                    RavTechDKUtil.selectedObjects.get(0).addComponent(component);
                    component.finishedLoading();
                } catch (ReflectionException e) {
                    e.printStackTrace();
                }
            }
        });
        menu.add(item);
    }
}
