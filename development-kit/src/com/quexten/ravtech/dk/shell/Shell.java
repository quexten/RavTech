
package com.quexten.ravtech.dk.shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import com.quexten.ravtech.dk.ui.utils.StreamGobbler;
import com.quexten.ravtech.dk.ui.utils.StreamGobbler.Printer;

public class Shell {

	/** Executes the command at the specified directory
	 *
	 * @param directory the directory to execute the command in
	 * @param command the command
	 * @return the output of the command executed */
	public static String executeCommand (File directory, String command, boolean sameDirectory) {
		Process process;
		StringBuilder stringBuilder = new StringBuilder();
		try {
			process = Runtime.getRuntime()
				.exec((sameDirectory ? (directory.getPath() + System.getProperty("file.separator")) : "") + command, null, directory);
			InputStreamReader in = new InputStreamReader(process.getInputStream());
			BufferedReader reader = new BufferedReader(in);
			String line;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line + '\n');
				System.err.println(line);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return stringBuilder.toString();
	}

	public static String executeCommand (File directory, String command) {
		return executeCommand(directory, command, true);
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
