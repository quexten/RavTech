
package com.ravelsoftware.ravtech.dk.packaging.platforms.android;

import java.io.File;

public class KeyStoreCredentials {

	public KeyStoreCredentials (File keystoreFile, String keystorePassword, String aliasName, String aliasPassword) {
		this.keystoreFile = keystoreFile;
		this.keystorePassword = keystorePassword;
		this.aliasName = aliasName;
		this.aliasPassword = aliasPassword;
	}

	public File keystoreFile;
	public String keystorePassword;
	public String aliasName;
	public String aliasPassword;

}
