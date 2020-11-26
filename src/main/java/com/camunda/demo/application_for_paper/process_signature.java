package com.camunda.demo.application_for_paper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class process_signature implements JavaDelegate {
	private static final String db_host = "jdbc:mysql://scientific-paper-swim-process-moodle-mariadb:3306/bitnami_moodle";
	private static final String db_driver = "org.gjt.mm.mysql.Driver";
	private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public process_signature() {
		logger.setLevel(Level.ALL);
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		int application_id = (int) execution.getVariable("application_id");
		int result_code = (int) execution.getVariable("status_code");
		String result_status = (String) execution.getVariable("status_description");
		
		if (result_code != 0) {
			execution.setVariable("signature_successfull", 0);
			execution.setVariable("result_description", result_status);
		}else {
			execution.setVariable("signature_successfull", 1);
			execution.setVariable("result_description", "");
		}
		execution.setVariable("application_id", application_id);
		
		Class.forName(db_driver);
	    Connection conn = DriverManager.getConnection(db_host, "bn_moodle", "swim-access");
	    logger.severe("Writing to database.");
	    String query = " insert into mdl_spam_application_status (application_id, status, description)"
		        + " values (?, ?, ?) on duplicate key update application_id = ?";
	    PreparedStatement preparedStmt = conn.prepareStatement(query);
	    preparedStmt.setInt (1, application_id);
	    preparedStmt.setInt (2, result_code);
		if (result_code != 0) {
		    preparedStmt.setString(3, result_status);
		}else {
			preparedStmt.setString(3, "");
		}
		preparedStmt.execute();


	    query = " SELECT st_mail, description FROM mdl_spam_studenthassupervisor "
	    		+ "JOIN mdl_spam_application_status ON mdl_spam_studenthassupervisor.application_id=mdl_spam_application_status.application_id"
		        + "WHERE mdl_spam_application_status.application_id = ? LIMIT 1";
	    preparedStmt = conn.prepareStatement(query);
	    preparedStmt.setInt (1, application_id);
		ResultSet result = preparedStmt.executeQuery();
		conn.close();
	    
		result.first();
		
		String mail_address = result.getString(1);
		if (result_code != 0) {
			String mail_subject = "Your scientific paper application"; 
			String mail_message = String.format("Your application was rejected because %s", result_code);
		}
			

	}

}
