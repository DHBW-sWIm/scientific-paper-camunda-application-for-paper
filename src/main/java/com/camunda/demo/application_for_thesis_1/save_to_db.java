package com.camunda.demo.application_for_thesis_1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class save_to_db implements JavaDelegate {
	private static final String db_host = "jdbc:mysql://scientific-paper-swim-process-moodle-mariadb:3306/bitnami_moodle";
	private static final String db_driver = "org.gjt.mm.mysql.Driver";
	private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public save_to_db() {
		logger.setLevel(Level.ALL);
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		Map<String, String> application_information = (Map<String, String>) execution.getVariable("application_info");
		
		Class.forName(db_driver);
	    Connection conn = DriverManager.getConnection(db_host, "bn_moodle", "swim-access");
	    logger.severe("Writing to database.");
	    String query = " insert into mdl_spam_studenthassupervisor (studentid, supervisorid, assignmentid)"
		        + " values (?, ?, ?) on duplicate key update supervisorid = ?";
	    PreparedStatement preparedStmt = conn.prepareStatement(query);
	    preparedStmt.setString (1, application_information.get("st_matno"));
	    preparedStmt.setString (2, application_information.get("st_name"));
	    preparedStmt.setString (3, application_information.get("st_surname"));
	    preparedStmt.setString (4, application_information.get("st_mail"));
	    preparedStmt.setString (5, application_information.get("st_tel"));
	    preparedStmt.setString (6, application_information.get("st_company"));
	    preparedStmt.setString (7, application_information.get("st_division"));
	    preparedStmt.setString (8, application_information.get("st_course"));
	    
	    preparedStmt.setString (9, application_information.get("title"));
	    preparedStmt.setString (10, application_information.get("area"));
	    preparedStmt.setString (11, application_information.get("focus"));
	    preparedStmt.setString (12, application_information.get("description"));
	    preparedStmt.setBoolean(13, application_information.get("lock").equals("yes") ? true : false);
	    preparedStmt.setString (14, application_information.get("lock_reason"));
	    preparedStmt.setString (15, application_information.get("ch_name"));
	    preparedStmt.setString (16, application_information.get("ch_surname"));
	    preparedStmt.setString (17, application_information.get("ch_mail"));
	    preparedStmt.setString (18, application_information.get("ch_tel"));
	    preparedStmt.setString (19, application_information.get("ch_position"));
	    preparedStmt.execute();
		conn.close();

	}

}
