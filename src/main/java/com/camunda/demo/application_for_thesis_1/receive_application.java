package com.camunda.demo.application_for_thesis_1;

import static org.camunda.spin.Spin.JSON;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.spin.json.SpinJsonNode;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

public class receive_application implements JavaDelegate {
	private static final String host = "scientific-paper-swim-process-mailhog";
	private static final int port = 1025;
	private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private static ObjectMapper mapper = new ObjectMapper();
	

	public receive_application() {
		logger.setLevel(Level.ALL);
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		String datastring = (String) execution.getVariable("application_information"); // receiving datastring from API Call and parsing data
		Map<String, String> elements = null;
		
		
		try {
			elements = mapper.readValue(datastring, Map.class);
		}catch (IOException e) {
            e.printStackTrace();
        }
		
		execution.setVariable("application_info", elements);
		

	}

}
