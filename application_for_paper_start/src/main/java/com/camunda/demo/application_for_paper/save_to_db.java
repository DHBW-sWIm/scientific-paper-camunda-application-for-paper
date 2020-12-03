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
	private static final String db_host = String.format("jdbc:mysql://%s/bitnami_moodle", System.getenv("PROCESS_DB_HOST"));
	private static final String db_user = System.getenv("PROCESS_DB_USER");
	private static final String db_pwd = System.getenv("PROCESS_DB_PWD");
	private static final String db_driver = "org.gjt.mm.mysql.Driver";
	private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private Connection conn;

	public save_to_db() {
		logger.setLevel(Level.ALL);
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		@SuppressWarnings("unchecked")
		Map<String, String> application_information = (Map<String, String>) execution.getVariable("application_info");
		String query = "";
		
		logger.info(db_host);
		logger.info(db_user);
		logger.info(db_pwd);
		
		Connection conn = getConnection();
		
	    
	    logger.info("Preparing data");
	    
	    if (application_information.containsKey("supid")) {
	    	query = " insert into mdl_spam_studenthassupervisor (studentid, supervisorid, ch_name, ch_surname, ch_email)"
			        + " values (?, ?, ?, ?, ?)";
		    
	    }else {
	    	query = " insert into mdl_spam_studenthassupervisor (studentid, ch_name, ch_surname, ch_email)"
			        + " values (?, ?, ?, ?)";
	    }
	    logger.info(query);
	    if(conn==null) {
	    	logger.warning("DB connection not established");
	    }
	    PreparedStatement preparedStmt = conn.prepareStatement(query);
	    
	    logger.info("Inserting data");
	    preparedStmt.setString (1, application_information.get("stid"));
	    int pos = 2;
	    if (application_information.containsKey("supid")) {
	    	preparedStmt.setString (2, application_information.get("supid"));
	    	pos++;
	    }
	    preparedStmt.setString (pos, application_information.get("ch_name"));
	    preparedStmt.setString (pos+1, application_information.get("ch_surname"));
	    preparedStmt.setString (pos+2, application_information.get("ch_mail"));
	    
	    logger.info("executing query");
	    preparedStmt.execute();
		conn.close();
		logger.info("Data inserted to db");

	}
	
	private Connection getConnection() throws ClassNotFoundException {
		if (this.conn != null) {
			return this.conn;
		}else {
			logger.severe("Connecting to database.");
			Class.forName(db_driver);
		
			Connection conn=null;
			
		    try{
		    	conn = DriverManager.getConnection(db_host, db_user, db_pwd);
		    	this.conn=conn;
		    	return conn;
		    }catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return conn;
		
	}

}
