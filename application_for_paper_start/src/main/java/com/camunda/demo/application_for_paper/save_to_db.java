package com.camunda.demo.application_for_paper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class save_to_db implements JavaDelegate {
	private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public save_to_db() {
		logger.setLevel(Level.INFO);
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		@SuppressWarnings("unchecked")
		Map<String, String> application_information = (Map<String, String>) execution.getVariable("application_info");
		String query = "";
		DataBase db = new DataBase();
		
	    logger.fine("Preparing data");
	    
	    if (application_information.containsKey("supid")) {
	    	query = " insert into mdl_spam_studenthassupervisor (studentid, supervisorid, ch_name, ch_surname, ch_email)"
			        + " values (?, ?, ?, ?, ?)";
		    
	    }else {
	    	query = " insert into mdl_spam_studenthassupervisor (studentid, ch_name, ch_surname, ch_email)"
			        + " values (?, ?, ?, ?)";
	    }
	    
	    PreparedStatement preparedStmt = db.prepareQuery(query);
	    
	    logger.fine("Inserting data");
	    preparedStmt.setString (1, application_information.get("stid"));
	    int pos = 2;
	    if (application_information.containsKey("supid")) {
	    	preparedStmt.setString (2, application_information.get("supid"));
	    	pos++;
	    }
	    preparedStmt.setString (pos, application_information.get("ch_name"));
	    preparedStmt.setString (pos+1, application_information.get("ch_surname"));
	    preparedStmt.setString (pos+2, application_information.get("ch_mail"));
	    
	    logger.fine("executing query");
	    preparedStmt.execute();
		logger.info("Data inserted to db");
		db.close();

	}
	
	

}
