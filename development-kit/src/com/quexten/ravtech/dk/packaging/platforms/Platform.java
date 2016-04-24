
package com.ravelsoftware.ravtech.dk.packaging.platforms;

import com.badlogic.gdx.files.FileHandle;
import com.ravelsoftware.ravtech.dk.ui.packaging.BuildReporterDialog;

public interface Platform {

	/** builds the Project
	 *
	 * @param buildReporterDialog the BuildReporterDialog to pipe the console log into
	 * @return wether the build has been started */
	boolean build (FileHandle directory,
		BuildReporterDialog buildReporterDialog);

	/** runs the Project
	 *
	 * @param buildReporterDialog the BuildReporterDialog to pipe the console log into
	 * @return wether the build has been started */
	boolean run (BuildReporterDialog buildReporterDialog);
}
