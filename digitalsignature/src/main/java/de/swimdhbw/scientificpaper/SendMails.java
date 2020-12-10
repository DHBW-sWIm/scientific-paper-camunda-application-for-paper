package de.swimdhbw.scientificpaper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import camundajar.impl.com.google.gson.JsonArray;
import camundajar.impl.com.google.gson.JsonElement;
import camundajar.impl.com.google.gson.JsonObject;
import camundajar.impl.com.google.gson.JsonParser;

public class SendMails implements JavaDelegate {
	private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public SendMails() {
		logger.setLevel(Level.INFO);
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		int currentstep = (int) execution.getVariable("currentStep");
		logger.fine("sending mails for step"+ String.valueOf(currentstep));
		
		logger.fine("Finding current tasks");
		JsonObject payload = (JsonObject) new JsonParser().parse((String)execution.getVariable("payload"));
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
			logger.info("All documents were already signed");
			return;
		}
		tmp.execute();
		
		execution.setVariable("currentStep", tmp.getStep());
		logger.info("Completed sending mails for step: " + String.valueOf(tmp.getStep()));
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
