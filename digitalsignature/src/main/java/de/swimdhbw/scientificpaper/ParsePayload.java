package de.swimdhbw.scientificpaper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import camundajar.impl.com.google.gson.JsonArray;
import camundajar.impl.com.google.gson.JsonElement;
import camundajar.impl.com.google.gson.JsonObject;
import camundajar.impl.com.google.gson.JsonParser;

public class ParsePayload implements JavaDelegate {
	private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public ParsePayload() {
		logger.setLevel(Level.INFO);
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		logger.info(String.format("Started signature process with UUID: %s", execution.getBusinessKey()));
		
		String jsonPayload = (String) execution.getVariable("signature_information");
		jsonPayload = jsonPayload.replace("\"\"", "\\\"");
		
		logger.fine(jsonPayload);
		
		JsonObject obj = (JsonObject) new JsonParser().parse(jsonPayload);
		
		SignatureStep[] signatures = parseSignees(obj.get("signees").getAsJsonObject());
		logger.fine("Signatures instance created");
		
		
		logger.fine("setting steps");
		execution.setVariable("currentStep", -1);
		execution.setVariable("lastStep", signatures[signatures.length-1].getStep());
		
		
		logger.fine("setting payload");
		execution.setVariable("payload", jsonPayload);
		
		logger.info("Parsing completed");
		
	}	
	
	
	
	private SignatureStep[] parseSignees(JsonObject obj) {
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
