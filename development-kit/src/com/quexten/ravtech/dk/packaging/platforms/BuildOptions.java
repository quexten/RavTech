
package com.quexten.ravtech.dk.packaging.platforms;

public class BuildOptions {

	public enum AssetType {
		Internal, External
	};

	public AssetType assetType;
	public boolean skipBuild;
	
	public BuildOptions (AssetType assetType, boolean skipBuild) {
		this.assetType = assetType;
		this.skipBuild = skipBuild;
	}

}
