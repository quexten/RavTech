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
package com.ravelsoftware.ravtech.dk.shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import com.ravelsoftware.ravtech.HookApi;
import com.ravelsoftware.ravtech.dk.ui.utils.StreamGobbler;
import com.ravelsoftware.ravtech.dk.ui.utils.StreamGobbler.Printer;

public class Shell {

    /** Executes the command at the specified directory
     *
     * @param directory the directory to execute the command in
     * @param command the command
     * @return the output of the command executed */
    public static String executeCommand (File directory, String command) {
        Process process;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            process = Runtime.getRuntime().exec(directory.getPath() + System.getProperty("file.separator") + command, null,
                directory);
            InputStreamReader in = new InputStreamReader(process.getInputStream());
            BufferedReader reader = new BufferedReader(in);
            String line;
            while ((line = reader.readLine()) != null)
                stringBuilder.append(line + "\n");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /** Executes the command at the specified directory and asyncronously logs the output
     *
     * @param directory the directory to execute the command in
     * @param command the command
     * @param logPrinter the printer for regular logging
     * @param errorPrinter the printer for error logging */
    public static void executeCommand (File directory, String command, Printer logPrinter, Printer errorPrinter) {
        String[] commands = command.split(" ");
        commands[0] = directory.getPath() + System.getProperty("file.separator") + commands[0];
        ProcessBuilder builder = new ProcessBuilder(commands);
        builder.directory(directory);
        try {
            final Process process = builder.start();
            new StreamGobbler(process.getInputStream(), logPrinter).start();
            new StreamGobbler(process.getErrorStream(), errorPrinter).start();
            HookApi.onShutdownHooks.add(new Runnable() {

                @Override
                public void run () {
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
