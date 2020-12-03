package com.camunda.demo.application_for_paper;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class start_digital_signing implements JavaDelegate {
	//scientific-paper-swim-process-moodle-mariadb:3306
	private static final String db_host = String.format("jdbc:mysql://%s/bitnami_moodle", System.getenv("PROCESS_DB_HOST"));
	private static final String db_user = System.getenv("PROCESS_DB_USER");
	private static final String db_pwd = System.getenv("PROCESS_DB_PWD");
	private static final String db_driver = "org.mariadb.jdbc.Driver";
	private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private Connection conn;

	public start_digital_signing() {
		logger.setLevel(Level.ALL);
	}

	@SuppressWarnings("unchecked")
	public void execute(DelegateExecution execution) throws Exception {
		
		logger.info("Preparing for digital signature");
		HashMap<String, String> application_information = (HashMap<String, String>) execution.getVariable("application_info");
		
		String camundaHost = "localhost:8080";//System.getenv("camunda_host");
		
		String callbackAfterSignature = "http://"+camundaHost+"/engine-rest/message";
		String callbackBody = String.format("\"{\"\"messageName\"\": \"\"signatureComplete\"\",\"\"businessKey\"\": \"\"%s\"\"}\"", execution.getBusinessKey());
		
		
		Connection dbConn = getConnection();
	    
		logger.info("retrieving student info");
		logger.info(application_information.get("stid"));
		ResultSet resultStudent = getUserInfo(application_information.get("stid"), dbConn);
		
		
		//Generate JSON to trigger digital signature
		String signatureJSON = "";
		
		//If scientific supervisor is defined, he needs to sign last
		if (application_information.containsKey("supervisorID")) {
			logger.info("found supervisor");
			ResultSet resultSupervisor = getUserInfo(application_information.get("supervisorID"), dbConn);
			resultSupervisor.first();
			
			signatureJSON = makeJSONWithSup(
					execution.getBusinessKey(), 
					application_information.get("doclink"), 
					resultStudent.getString(2), 
					resultStudent.getString(1), 
					resultStudent.getString(3), 
					application_information.get("chName"), 
					application_information.get("chSurname"), 
					application_information.get("chMail"),  
					resultSupervisor.getString(2), 
					resultSupervisor.getString(1), 
					resultSupervisor.getString(3), 
					callbackAfterSignature,
					callbackBody);
			
	    }else {
	    	logger.info("found no supervisor");
	    	signatureJSON = makeJSONNoSup(
					execution.getBusinessKey(),
	    			application_information.get("doclink"), 
	    			resultStudent.getString(2), 
	    			resultStudent.getString(1), 
	    			resultStudent.getString(3), 
	    			application_information.get("chName"), 
	    			application_information.get("chSurname"), 
	    			application_information.get("chMail"), 
	    			callbackAfterSignature,
	    			callbackBody
	    			);
	    }
		
		dbConn.close();
		
		
		if (System.getenv("ENV_TYPE")=="development") {
			System.out.println(signatureJSON);
		}
		
		signatureJSON = signatureJSON.replace("\"", "\\\"");
		String payload = String.format("{\"businessKey\": \"%s\", \"variables\": {\"signature_information\": {\"value\": \"{%s}\", \"type\":\"String\"}}}", execution.getBusinessKey(), signatureJSON);
		logger.info(payload);
		
		//prepare request to ds on camunda
		logger.info("preparing connection");
		URL url = null;
		try {
			url = new URL(String.format("http://%s/engine-rest/process-definition/key/digital_signature/start", camundaHost));
		} catch (MalformedURLException e3) {
			e3.printStackTrace();
		}
		logger.info("url inserted");
		HttpURLConnection restConn = (HttpURLConnection) url.openConnection();
		try {
			restConn.setRequestMethod("POST");
			restConn.setRequestProperty("Content-Type", "application/json");
			restConn.setDoOutput(true);
		} catch (ProtocolException e3) {
			e3.printStackTrace();
		}
		logger.info("getting output stream");
		OutputStream os=null;
		try {
		os = restConn.getOutputStream();
		}catch(Exception e) {
			logger.warning(camundaHost);
			e.printStackTrace();
			
		}
		logger.info("got output stream");
		OutputStreamWriter osw = new OutputStreamWriter(os);
  
		logger.info("writing json");
		osw.write(payload);
		osw.flush();
		osw.close();
		os.close();
		logger.info("executing connection");
		restConn.connect();
		if (restConn.getResponseCode()>202) {
			logger.info(url.toString());
			logger.info(restConn.getResponseMessage());
			throw new RuntimeException("Starting of digital signature failed with " + String.valueOf(restConn.getResponseCode()));
		}else {
			System.out.println("Process digital signature started");
		}


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
	
	/**
	 * Method to make JSON payload for digital signature without scientific supervisor
	 * @param documentID
	 * @param documentLink
	 * @param stName
	 * @param stSurname
	 * @param stMail
	 * @param chName
	 * @param chSurname
	 * @param chMail
	 * @return
	 */
	public static String makeJSONNoSup(
			String documentID, 
			String documentLink, 
			String stName, 
			String stSurname, 
			String stMail, 
			String chName, 
			String chSurname, 
			String chMail,
			String callbackLink,
			String callbackBody) {
		
		String JSON = makeJSONHead(documentID, documentLink, stName, stSurname, stMail, chName, chSurname, chMail);
		
		JSON += "}, ";
		JSON += String.format("\"callback\": {\"link\": \"%s\", \"body\": %s", callbackLink, callbackBody);
		JSON += "}";
		
		return JSON;
	
	}
	
	/**
	 * Method to make JSON payload for digital signature with scientific supervisor
	 * @param documentID
	 * @param documentLink
	 * @param stName
	 * @param stSurname
	 * @param stMail
	 * @param chName
	 * @param chSurname
	 * @param chMail
	 * @return
	 */
	public static String makeJSONWithSup(
			String documentID, 
			String documentLink, 
			String stName, 
			String stSurname, 
			String stMail, 
			String chName, 
			String chSurname, 
			String chMail,
			String supName,
			String supSurname,
			String supMail,
			String callbackLink,
			String callbackBody) {
		
		String JSON = makeJSONHead(documentID, documentLink, stName, stSurname, stMail, chName, chSurname, chMail);
		
		JSON += String.format("\"3\": [{\"name\": \"%s\", \"surname\": \"%s\", \"email\": \"%s\"}]", supName, supSurname, supMail);
		
		JSON += "}, ";
		JSON += String.format("\"callback\": {\"link\": \"%s\", \"body\": %s", callbackLink, callbackBody);
		JSON += "}}";
		
		return JSON;
	
	}
	
	public static String makeJSONHead(
			String documentID, 
			String documentLink, 
			String stName, 
			String stSurname, 
			String stMail, 
			String chName, 
			String chSurname, 
			String chMail) {
		
		String signatureJSON = "";
		signatureJSON += String.format(" \"document\": {\"documentid\": \"%s\", \"documentlink\": \"%s\"}, ", documentID, documentID);
		// Create Signees
		//Student needs to sign first
		signatureJSON += String.format("\"signees\": { \"1\":[{\"name\": \"%s\", \"surname\": \"%s\", \"email\": \"%s\"}],", stName, stSurname, stMail);
		//Corporate Partner needs to sign second
		signatureJSON += String.format("\"2\": [{\"name\":\"%s\", \"surname\": \"%s\", \"email\": \"%s\"}]", chName, chSurname, chMail);
		
		return signatureJSON;
	}
	
	public static ResultSet getUserInfo(String userID, Connection conn) throws SQLException {
		//Read student information from db
		assert conn!=null;
	    String query = "SELECT firstname, lastname, email FROM mdl_user WHERE id=?";
	    logger.info(conn.toString());
	    PreparedStatement preparedStmt = conn.prepareStatement(query);
		preparedStmt.setString (1, userID);
			
	    ResultSet result = null;
	    result = preparedStmt.executeQuery();
	    
	    result.first();
		
		return result;
	}

}