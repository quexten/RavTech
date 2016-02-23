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

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import com.ravelsoftware.ravtech.dk.zerobrane.ZeroBraneUtil;

public class OpenFileAction implements Runnable {

    File file;

    public OpenFileAction(File file) {
        this.file = file;
    }

    @Override
    public void run () {
        switch (file.getName().substring(file.getName().lastIndexOf('.'), file.getName().length())) {
            case ".map":
                break;
            case ".lua":
                ZeroBraneUtil.openFile(file);
                break;
            case ".particle":
                new Thread() {

                    @Override
                    public void run () {
                        try {
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }.start();
                break;
            default:
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
