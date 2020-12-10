package de.swimdhbw.scientificpaper;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import camundajar.impl.com.google.gson.JsonObject;
import camundajar.impl.com.google.gson.JsonParser;

public class SendCompletion implements JavaDelegate {
	private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public SendCompletion() {
		logger.setLevel(Level.INFO);
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		boolean status = (boolean) execution.getVariable("status");
		
		logger.fine("Finding callback");
		
		String payload = (String) execution.getVariable("payload");
		JsonObject obj = (JsonObject) new JsonParser().parse(payload);
		Callback callback = parseCallback(obj.get("callback").getAsJsonObject());
		
		
		logger.fine("setting status");
		callback.setBody(callback.getBody().replace("completionValue", "\""+String.valueOf((boolean)execution.getVariable("status"))+"\""));
		
		logger.fine("Executing callback");
		callback.execute();
		logger.info("Signature-process "+ execution.getBusinessKey() + " completed");
	}
	
	private Callback parseCallback(JsonObject obj) {
		//Create Callback
		Callback callback = new Callback(
					obj.get("link").getAsString(), 
					obj.get("body").getAsString());
			return callback;
	}

}
