
package com.ravelsoftware.ravtech.dk.shell;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ProcessExecutor {

	public static int exec (Class<?> klass, String... args) throws IOException, InterruptedException {
		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
		String classpath = System.getProperty("java.class.path");
		String className = klass.getCanonicalName();
		ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath, className, args[0]);
		builder.redirectErrorStream(true);
		byte[] buffer = new byte[1024];
		Process process = builder.start();
		InputStream in = process.getInputStream();
		while (true) {
			int r = in.read(buffer);
			if (r <= 0) break;
			System.out.write(buffer, 0, r);
		}
		process.waitFor();
		return process.exitValue();
	}
}
