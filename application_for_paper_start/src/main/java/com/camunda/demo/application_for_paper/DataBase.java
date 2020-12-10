package com.camunda.demo.application_for_paper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataBase {
	private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	//scientific-paper-swim-process-moodle-mariadb:3306
	private final String db_host = String.format("jdbc:mysql://%s/bitnami_moodle", System.getenv("PROCESS_DB_HOST"));
	private final String db_user = System.getenv("PROCESS_DB_USER");
	private final String db_pwd = System.getenv("PROCESS_DB_PWD");
	private final String db_driver = "org.gjt.mm.mysql.Driver";
	private Connection conn;

	public DataBase() throws ClassNotFoundException {
		logger.setLevel(Level.INFO);
		
		logger.fine(db_host);
		logger.fine(db_user);
		logger.fine(db_pwd);
		
		this.conn = getConnection();
	}
	
	public Connection getConnection() throws ClassNotFoundException {
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
	
	public PreparedStatement prepareQuery(String query) throws SQLException {
		logger.fine(query);
	    if(conn==null) {
	    	logger.warning("DB connection not established");
	    }
	    return this.conn.prepareStatement(query);
	}
	
	public void close() throws SQLException {
		this.conn.close();
	}

}
