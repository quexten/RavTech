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
package com.ravelsoftware.ravtech.dk.input;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import com.badlogic.gdx.controllers.PovDirection;
import com.ravelsoftware.ravtech.RavTech;
import com.ravelsoftware.ravtech.input.ControllerState;
import com.ravelsoftware.ravtech.util.Debug;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;

public class GamepadObserver {

    public static void init () {
        pollControllers();
    }

    public static void pollControllers () {
        try {
            Class<?> clazz = Class.forName("net.java.games.input.DefaultControllerEnvironment");
            Constructor<?> defaultConstructor = clazz.getDeclaredConstructor();
            defaultConstructor.setAccessible(true); // set visibility to public
            Field defaultEnvironementField = ControllerEnvironment.class.getDeclaredField("defaultEnvironment");
            defaultEnvironementField.setAccessible(true);
            defaultEnvironementField.set(ControllerEnvironment.getDefaultEnvironment(), defaultConstructor.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
        RavTech.input.controllerStates.clear();
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
        for (int i = 0; i < controllers.length; i++)
            if (controllers[i].getType() == Controller.Type.GAMEPAD) {
                Debug.log("Controller", controllers[i].getName());
                RavTech.input.controllerStates.add(new ControllerState());
            }
    }

    public static void update () {
        for (ControllerState state : RavTech.input.controllerStates)
            state.update();
        //
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
        for (int i = 0; i < controllers.length; i++) {
            if (controllers[i].getType() == Controller.Type.STICK) {
            }
            if (controllers[i].getType() == Controller.Type.GAMEPAD) {
            }
            if (!controllers[i].poll()) pollControllers();
            net.java.games.input.EventQueue queue = controllers[i].getEventQueue();
            Event event = new Event();
            if (controllers[i].getType() == Controller.Type.STICK || controllers[i].getType() == Controller.Type.GAMEPAD)
                while (queue.getNextEvent(event)) {
                net.java.games.input.Component comp = event.getComponent();
                boolean isAxis = comp.getName().startsWith("X") || comp.getName().startsWith("Y")
                    || comp.getName().startsWith("Z");
                boolean isButton = comp.getName().substring(comp.getName().length() - 1).matches("[-+]?\\d*\\.?\\d+");
                boolean isDPad = !isAxis && !isButton;
                ControllerState controllerState = RavTech.input.controllerStates.get(0);
                if (isAxis) {
                    String identifier = comp.getIdentifier().toString();
                    int axisId = identifier.equals("x") ? 0
                        : identifier.equals("y") ? 1
                            : identifier.equals("rx") ? 2 : identifier.equals("ry") ? 3 : identifier.equals("z") ? 4 : 5;
                    controllerState.axisValues.put(axisId, event.getValue());
                } else if (isButton) {
                    int id = Integer.valueOf(comp.getIdentifier().toString());
                    controllerState.buttonValues.put(id, event.getValue() == 1.0f);
                } else if (isDPad) {
                    int direction = (int)(comp.getPollData() * 8.0);
                    switch (direction) {
                        case 2:
                            break;
                        case 3:
                            break;
                        case 4:
                            break;
                        case 5:
                            break;
                        case 6:
                            break;
                        case 7:
                            break;
                        case 8:
                            break;
                        case 1:
                            break;
                    }
                    controllerState.povDirection = PovDirection.center;
                }
            }
        }
    }
}
