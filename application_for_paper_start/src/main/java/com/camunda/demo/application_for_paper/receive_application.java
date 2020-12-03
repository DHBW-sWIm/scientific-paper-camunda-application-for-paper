package com.camunda.demo.application_for_paper;



import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class receive_application implements JavaDelegate {
	private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	public receive_application() {
		logger.setLevel(Level.ALL);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		String processID = UUID.randomUUID().toString();
		
		logger.info(String.format("Process started. Assigned UUID: %s", processID.toString()));
		execution.setProcessBusinessKey(processID);
		
		logger.info("Unpacking payload");
		
		String datastring = (String) execution.getVariable("application_information"); // receiving datastring from API Call and parsing data
		
		HashMap<String, String> application_data = new HashMap<String, String>();
		
		application_data.put("stid", (String) execution.getVariable("stid"));
		
		if (execution.hasVariable("supid")) {
			logger.info("Scientific Supervisor found");
			application_data.put("supid", (String) execution.getVariable("supid"));
		}
		
		application_data.put("ch_name", (String) execution.getVariable("ch_name"));
		application_data.put("ch_surname", (String) execution.getVariable("ch_surname"));
		application_data.put("ch_mail", (String) execution.getVariable("ch_mail"));
		
		execution.setVariable("application_info", application_data);
		
		logger.info("Parsing complete");

	}

}
