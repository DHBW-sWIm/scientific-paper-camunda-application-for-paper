package com.camunda.demo.application_for_paper;



import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class ReceiveApllication implements JavaDelegate {
	private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	public ReceiveApllication() {
		logger.setLevel(Level.INFO);
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		String processID = UUID.randomUUID().toString();
		
		logger.info(String.format("Process started. Assigned UUID: %s", processID.toString()));
		execution.setProcessBusinessKey(processID);
		
		logger.fine("Unpacking payload");
		
		HashMap<String, String> application_data = new HashMap<String, String>();
		
		application_data.put("stid", (String) execution.getVariable("stid"));
		application_data.put("kursInfo", (String) execution.getVariable("stcourse"));
		
		if (execution.hasVariable("supid")) {
			logger.fine("Scientific Supervisor found");
			application_data.put("supid", (String) execution.getVariable("supid"));
			application_data.put("wissenschaftlicherbetreuerArbeitgeber", (String) execution.getVariable("supcompany"));
			application_data.put("wissenschaftlicherbetreuerAbteilung", (String) execution.getVariable("supdepartment"));
			application_data.put("wissenschaftlicherbetreuerFunktion", (String) execution.getVariable("supfunction"));
		}
		
		application_data.put("unternehmenName", (String) execution.getVariable("company"));
		application_data.put("unternehmenAbteilung", (String) execution.getVariable("department"));
		application_data.put("unternehmensbetreuerNachname", (String) execution.getVariable("ch_name"));
		application_data.put("unternehmensbetreuerVorname", (String) execution.getVariable("ch_surname"));
		application_data.put("unternehmensvertreterNachname", (String) execution.getVariable("ch_name"));
		application_data.put("unternehmensvertreterVorname", (String) execution.getVariable("ch_surname"));
		application_data.put("unternehmensbetreuerEmail", (String) execution.getVariable("ch_mail"));
		application_data.put("unternehmensbetreuerTelefon", (String) execution.getVariable("ch_phone"));
		application_data.put("unternehmensbetreuerFunktion", (String) execution.getVariable("ch_function"));
		application_data.put("unternehmensvertreterFunktion", (String) execution.getVariable("ch_function"));
		
		application_data.put("BachelorarbeitInhalt", (String) execution.getVariable("content"));
		application_data.put("ProjektarbeitInhalt", (String) execution.getVariable("content"));
		
		//Dropdown
		application_data.put("ProjektarbeitThema", (String) execution.getVariable("topic"));
		application_data.put("ProjektarbeitThemenbereich", (String) execution.getVariable("area"));
		application_data.put("ProjektarbeitThemenschwerpunkt", (String) execution.getVariable("focus"));
		application_data.put("BachelorarbeitThema", (String) execution.getVariable("topic"));
		application_data.put("BachelorarbeitThemenbereich", (String) execution.getVariable("area"));
		application_data.put("BachelorarbeitThemenschwerpunkt", (String) execution.getVariable("focus"));
		
		//Dropdown
		application_data.put("sperrvermerkJaNein", (String) execution.getVariable("lock"));
		application_data.put("sperrvermerkBegruendung", (String) execution.getVariable("lock_reason"));
		
		
		
		execution.setVariable("application_info", application_data);
		
		logger.info("Parsing complete");

	}

}
