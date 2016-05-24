
package com.quexten.ravtech.dk.packaging.platforms.android;

import java.io.File;

import com.quexten.ravtech.RavTech;
import com.quexten.ravtech.dk.shell.Shell;
import com.quexten.ravtech.dk.ui.utils.StreamGobbler.Printer;
import com.quexten.ravtech.util.Debug;

public class KeyStoreManager {

	public static void createKeyStore (File keystorePath, KeyStoreData data) {
		Shell.executeCommand(new File(RavTech.settings.getString("RavTechDK.java.jdk.dir") + "/bin/"),
			"keytool" + " -genkey -v -keyalg RSA " + " -alias " + data.alias + " -keysize " + data.keySize + " -validity "
				+ data.validity + " -keystore " + keystorePath.getAbsolutePath() + " -dname " + "\"CN=" + data.company + ", OU="
				+ data.organizationalUnit + ", O=" + data.organization + ", L=" + data.city + ", S=" + data.province + ", C="
				+ data.country + "\"" + " -storepass " + data.storePassword + " -keypass " + data.keyPassword,
			new Printer() {
				@Override
				public void run () {
					Debug.log("KeyStore", line);
				}
			}, new Printer() {
				@Override
				public void run () {
					Debug.logError("KeyStore-Error", line);
				}
			});
	}

}
