
package com.quexten.ravtech.dk.packaging.platforms;

public class BuildOptions {

	public enum AssetType {
		Internal, External
	};

	public boolean run;

	public String targetPlatform = "Default";
	public AssetType assetType;
	public boolean skipBuild;

	public BuildOptions (AssetType assetType) {
		this.assetType = assetType;
	}

	public boolean isExternal () {
		return assetType == AssetType.External;
	}

	public void copyTo (BuildOptions options) {
		options.assetType = this.assetType;
		options.skipBuild = this.skipBuild;
	}

}
