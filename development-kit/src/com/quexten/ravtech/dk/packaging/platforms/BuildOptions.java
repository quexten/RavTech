
package com.ravelsoftware.ravtech.dk.packaging.platforms;

public class BuildOptions {

	public enum AssetType {
		Internal, External
	};

	public AssetType assetType;

	public BuildOptions (AssetType assetType) {
		this.assetType = assetType;
	}

}
