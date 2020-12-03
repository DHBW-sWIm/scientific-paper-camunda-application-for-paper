package de.swimdhbw.scientificpaper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import camundajar.impl.com.google.gson.JsonArray;
import camundajar.impl.com.google.gson.JsonElement;
import camundajar.impl.com.google.gson.JsonObject;
import camundajar.impl.com.google.gson.JsonParser;


public class CamundaTest {
	private static final String db_host = String.format("jdbc:mariadb://%s/bitnami_moodle", "localhost:8336");
	private static final String db_user = "bn_moodle";//System.getenv("PROCESS_DB_USER");
	private static final String db_pwd = "test";//System.getenv("PROCESS_DB_PWD");
	private static final String db_driver = "org.mariadb.jdbc.Driver";
	private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private static Connection conn;

	public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
		int currentstep = 1;
		logger.info("sending mails for step"+ String.valueOf(currentstep));
		String payloadJSON = "{ \"document\": {\"documentid\": \"c1fc1f14-fc9a-45ed-9192-19d3720c42ed\", \"documentlink\": \"c1fc1f14-fc9a-45ed-9192-19d3720c42ed\"}, \"signees\": { \"1\":[{\"name\": \" \", \"surname\": \"Guest user\", \"email\": \"root@localhost\"}],\"2\": [{\"name\":\"null\", \"surname\": \"null\", \"email\": \"null\"}]}, \"callback\": {\"link\": \"https://localhost:8080/camunda/engine-rest/message\", \"body\": \"{\\\"messageName\\\": \\\"signatureComplete\\\",\\\"businessKey\\\": \\\"c1fc1f14-fc9a-45ed-9192-19d3720c42ed\\\"}\"}}";
		
		logger.info("Finding current tasks");
		JsonObject payload = (JsonObject) new JsonParser().parse(payloadJSON);
		JsonObject signees = payload.get("signees").getAsJsonObject();
		SignatureStep[] signatures = parseSignees(signees);
		
		Iterator<SignatureStep> it = Arrays.asList(signatures).iterator();
		SignatureStep tmp = null;
		int parsestep = -1;
		while (it.hasNext() &&parsestep<currentstep) {
			tmp = it.next();
			parsestep = tmp.getStep();
		}
		if (it.hasNext()) {
			tmp = it.next();
		}else {
			logger.info("allsigned");
			return;
		}
		tmp.execute();
		
		logger.info(String.valueOf(tmp.getStep()));
	}
	
	private static SignatureStep[] parseSignees(JsonObject obj) {
		//Create Signees
			SignatureStep[] signatures;
			ArrayList<SignatureStep> signaturesList = new ArrayList<SignatureStep>();
			
			Iterator<String> it = obj.keySet().iterator();
			
			while (it.hasNext()) {
				String step = it.next();
				JsonArray signeesInStep = obj.get(step).getAsJsonArray();
				Iterator<JsonElement> signIt = signeesInStep.iterator();
				
				ArrayList<Signee> stepList = new ArrayList<Signee>();
				while (signIt.hasNext()) {
					JsonObject tmpJSON = signIt.next().getAsJsonObject();
					Signee temp = new Signee(
							tmpJSON.get("name").getAsString(), 
							tmpJSON.get("surname").getAsString(),
							tmpJSON.get("email").getAsString()
							);
					stepList.add(temp);
					
				}
				SignatureStep fullStep = new SignatureStep(step, stepList.toArray(new Signee[stepList.size()]));
				signaturesList.add(fullStep);
			}
			signatures = signaturesList.toArray(new SignatureStep[signaturesList.size()]);
			
			return signatures;
	}

	
}
