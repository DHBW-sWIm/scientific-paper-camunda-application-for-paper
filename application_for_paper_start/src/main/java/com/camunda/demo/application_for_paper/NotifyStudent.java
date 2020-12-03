package com.camunda.demo.application_for_paper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class NotifyStudent implements JavaDelegate {
	private static final String db_host = String.format("jdbc:mysql://%s/bitnami_moodle", System.getenv("PROCESS_DB_HOST"));
	private static final String db_user = System.getenv("PROCESS_DB_USER");
	private static final String db_pwd = System.getenv("PROCESS_DB_PWD");
	private static final String db_driver = "org.mariadb.jdbc.Driver";
	private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private Connection conn;

	public NotifyStudent() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		String stid = (String) execution.getVariable("stid");
		boolean wasSuccessfull = (boolean) execution.getVariable("status");
		
		Connection conn = getConnection();
		
		ResultSet studentInfo = getUserInfo(stid, conn);
		
		sendMail(studentInfo.getString(3), wasSuccessfull);
		
	}
	
	private Connection getConnection() throws ClassNotFoundException {
		if (this.conn != null) {
			return this.conn;
		}else {
			logger.fine("Connecting to database.");
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
	
	public static ResultSet getUserInfo(String userID, Connection conn) throws SQLException {
		//Read student information from db
		assert conn!=null;
	    String query = "SELECT firstname, lastname, email FROM mdl_user WHERE id=?";
	    logger.fine(conn.toString());
	    PreparedStatement preparedStmt = conn.prepareStatement(query);
		preparedStmt.setString (1, userID);
			
	    ResultSet result = null;
	    result = preparedStmt.executeQuery();
	    
	    result.first();
		
		return result;
	}
	
	private void sendMail(String toMail, boolean wasSuccessfull) {
		if (wasSuccessfull) {
			logger.info("Sending mail for successfull application to "+ toMail);
		}else {
			logger.info("Sending mail for unsuccessfull application to "+ toMail);
		}
	}

}
