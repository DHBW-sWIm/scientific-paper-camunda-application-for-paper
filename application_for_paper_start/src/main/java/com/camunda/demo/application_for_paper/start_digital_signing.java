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
	private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public start_digital_signing() {
		logger.setLevel(Level.INFO);
	}

	@SuppressWarnings("unchecked")
	public void execute(DelegateExecution execution) throws Exception {
		
		logger.fine("Preparing for digital signature");
		HashMap<String, String> application_information = (HashMap<String, String>) execution.getVariable("application_info");
		
		String camundaHost = "localhost:8080";//System.getenv("camunda_host");
		
		String callbackAfterSignature = "http://"+camundaHost+"/engine-rest/message";
		String callbackBody = String.format("\"{\"\"messageName\"\": \"\"signatureComplete\"\",\"\"businessKey\"\": \"\"%s\"\", \"\"processVariables\"\" : {\"\"status\"\" : {\"\"value\"\" : completionValue, \"\"type\"\": \"\"Boolean\"\"}}}\"", execution.getBusinessKey());
		
		
		DataBase db = new DataBase();
	    
		logger.fine("retrieving student info");
		logger.fine(application_information.get("stid"));
		ResultSet resultStudent = getUserInfo(application_information.get("stid"), db);
		
		
		//Generate JSON to trigger digital signature
		String signatureJSON = "";
		
		//If scientific supervisor is defined, he needs to sign last
		if (application_information.containsKey("supid")) {
			logger.fine("found supervisor");
			logger.finer(application_information.get("supid"));
			logger.finer(application_information.get("chName"));
			ResultSet resultSupervisor = getUserInfo(application_information.get("supid"), db);
			resultSupervisor.first();
			
			signatureJSON = makeJSONWithSup(
					execution.getBusinessKey(), 
					application_information.get("doclink"), 
					resultStudent.getString(2), 
					resultStudent.getString(1), 
					resultStudent.getString(3), 
					application_information.get("ch_name"), 
					application_information.get("ch_surname"), 
					application_information.get("ch_mail"),  
					resultSupervisor.getString(2), 
					resultSupervisor.getString(1), 
					resultSupervisor.getString(3), 
					callbackAfterSignature,
					callbackBody);
			
	    }else {
	    	logger.fine("found no supervisor");
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
		
		db.close();
		
		signatureJSON = signatureJSON.replace("\"", "\\\"");
		String payload = String.format("{\"businessKey\": \"%s\", \"variables\": {\"signature_information\": {\"value\": \"{%s}\", \"type\":\"String\"}}}", execution.getBusinessKey(), signatureJSON);
		logger.fine(payload);
		
		//prepare request to ds on camunda
		logger.fine("preparing connection");
		URL url = null;
		try {
			url = new URL(String.format("http://%s/engine-rest/process-definition/key/digital_signature/start", camundaHost));
		} catch (MalformedURLException e3) {
			e3.printStackTrace();
		}
		logger.fine("url inserted");
		HttpURLConnection restConn = (HttpURLConnection) url.openConnection();
		try {
			restConn.setRequestMethod("POST");
			restConn.setRequestProperty("Content-Type", "application/json");
			restConn.setDoOutput(true);
		} catch (ProtocolException e3) {
			e3.printStackTrace();
		}
		logger.fine("getting output stream");
		OutputStream os=null;
		try {
		os = restConn.getOutputStream();
		}catch(Exception e) {
			logger.warning(camundaHost);
			e.printStackTrace();
			
		}
		OutputStreamWriter osw = new OutputStreamWriter(os);
  
		logger.fine("writing json");
		osw.write(payload);
		osw.flush();
		osw.close();
		os.close();
		logger.fine("executing connection");
		restConn.connect();
		if (restConn.getResponseCode()>202) {
			logger.info(url.toString());
			logger.info(restConn.getResponseMessage());
			throw new RuntimeException("Starting of digital signature failed with " + String.valueOf(restConn.getResponseCode()));
		}else {
			logger.info("Process digital signature started");
		}


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
		
		JSON += String.format(",\"3\": [{\"name\": \"%s\", \"surname\": \"%s\", \"email\": \"%s\"}]", supName, supSurname, supMail);
		
		JSON += "}, ";
		JSON += String.format("\"callback\": {\"link\": \"%s\", \"body\": %s", callbackLink, callbackBody);
		JSON += "}";
		
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
	
	public static ResultSet getUserInfo(String userID, DataBase db) throws SQLException {
		//Read student information from db
		assert db!=null;
	    String query = "SELECT firstname, lastname, email FROM mdl_user WHERE id=?";
	    logger.finer(db.toString());
	    PreparedStatement preparedStmt = db.prepareQuery(query);
		preparedStmt.setString (1, userID);
			
	    ResultSet result = null;
	    result = preparedStmt.executeQuery();
	    
	    result.first();
		
		return result;
	}

}
