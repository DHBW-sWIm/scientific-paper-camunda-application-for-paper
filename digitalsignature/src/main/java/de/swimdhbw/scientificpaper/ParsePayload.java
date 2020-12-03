package de.swimdhbw.scientificpaper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import camundajar.impl.com.google.gson.JsonArray;
import camundajar.impl.com.google.gson.JsonElement;
import camundajar.impl.com.google.gson.JsonObject;
import camundajar.impl.com.google.gson.JsonParser;

public class ParsePayload implements JavaDelegate {
	private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public ParsePayload() {
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		logger.info(String.format("Started signature process with UUID: %s", execution.getBusinessKey()));
		
		String jsonPayload = (String) execution.getVariable("signature_information");
		jsonPayload = jsonPayload.replace("\"\"", "\\\"");
		
		logger.info(jsonPayload);
		
		JsonObject obj = (JsonObject) new JsonParser().parse(jsonPayload);
		
		
		//Document doc = parseDocument(obj.get("document").getAsJsonObject());
		logger.info("Document instance created");
		
		SignatureStep[] signatures = parseSignees(obj.get("signees").getAsJsonObject());
		logger.info("Signatures instance created");
		
		logger.info("Using businessKey: "+execution.getBusinessKey());
		Payload payload = new Payload(execution.getBusinessKey(), null, signatures, null);
		
		logger.info("setting setps");
		execution.setVariable("currentStep", payload.getSignees()[0].getStep());
		execution.setVariable("lastStep", payload.getSignees()[payload.getSignees().length-1].getStep());
		
		logger.info("setting payload");

		execution.setVariable("payload", jsonPayload);
		//execution.setVariable("payload", payload);
		
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
