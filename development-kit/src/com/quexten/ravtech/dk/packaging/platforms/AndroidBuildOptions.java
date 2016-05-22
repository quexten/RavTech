package com.quexten.ravtech.dk.packaging.platforms;

import com.quexten.ravtech.dk.packaging.platforms.android.KeyStoreCredentials;

public class AndroidBuildOptions extends BuildOptions {
	
	boolean sign;
	String deviceId;
	KeyStoreCredentials credentials;
	
	public AndroidBuildOptions (AssetType assetType) {
		super(assetType);
		this.targetPlatform = "Android";
	}

}
