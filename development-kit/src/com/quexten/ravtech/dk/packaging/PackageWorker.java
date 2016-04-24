
package com.ravelsoftware.ravtech.dk.packaging;

public class PackageWorker {

	PackageStep packageStep;

	public PackageWorker (PackageStep packageStep) {
		this.packageStep = packageStep;
	}

	public void run () {
		Thread thread = new Thread() {

			@Override
			public void run () {
				packageStep.run();
			}
		};
		thread.start();
	}
}
