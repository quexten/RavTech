
package com.quexten.ravtech.dk.packaging.platforms;

import com.quexten.ravtech.dk.packaging.platforms.android.KeyStoreCredentials;

public class BuildOptions {

	public enum AssetType {
		Internal, External
	};

	public boolean run;
	public String deviceId;

	// Shared
	public String targetPlatform;
	public AssetType assetType;
	public boolean skipBuild;

	// Android
	public KeyStoreCredentials credentials;
	public boolean sign;

	public BuildOptions (AssetType assetType, boolean skipBuild) {
		this.assetType = assetType;
		this.skipBuild = skipBuild;
	}

	public boolean isExternal () {
		return assetType == AssetType.External;
	}

}
