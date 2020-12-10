package com.camunda.demo.application_for_paper;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class UpdateDBStatus implements JavaDelegate {
	private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public UpdateDBStatus() {
		logger.setLevel(Level.INFO);
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		//Update status in db
		
		logger.warning("DB status not updated, not implemented");

	}

}
