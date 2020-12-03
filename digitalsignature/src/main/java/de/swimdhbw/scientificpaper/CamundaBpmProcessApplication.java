package de.swimdhbw.scientificpaper;

import org.camunda.bpm.application.ProcessApplication;
import org.camunda.bpm.application.impl.ServletProcessApplication;

/**
 * Process Application exposing this application's resources to the process engine.
 */
@ProcessApplication
public class CamundaBpmProcessApplication extends ServletProcessApplication {

	@SuppressWarnings("unused")
	  private static final String PROCESS_DEFINITION_KEY = "digitalsignature";
}
