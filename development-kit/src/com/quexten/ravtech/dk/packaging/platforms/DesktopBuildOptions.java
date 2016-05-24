
package com.quexten.ravtech.dk.packaging.platforms;

public class DesktopBuildOptions extends BuildOptions {

	public DesktopBuildOptions (AssetType assetType) {
		super(assetType);
		this.targetPlatform = "Desktop";
	}

}
