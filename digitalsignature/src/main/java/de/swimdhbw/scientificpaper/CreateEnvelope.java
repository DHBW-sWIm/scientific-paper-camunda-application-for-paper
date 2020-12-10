package de.swimdhbw.scientificpaper;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import camundajar.impl.com.google.gson.JsonObject;
import camundajar.impl.com.google.gson.JsonParser;

public class CreateEnvelope implements JavaDelegate {
	private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public CreateEnvelope() {
		logger.setLevel(Level.INFO);
	}
	
	public CreateEnvelope(Level loggerLevel) {
		logger.setLevel(loggerLevel);
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		String payload = (String) execution.getVariable("payload");
		JsonObject obj = (JsonObject) new JsonParser().parse(payload);
		Document doc = parseDocument(obj.get("document").getAsJsonObject());
		
		
		execution.setVariable("status", true);
		logger.info("Envelope created");
	}
	
	private Document parseDocument(JsonObject obj) {
		//Create Document instance
		Document doc = new Document(
						obj.get("documentid").getAsString(), 
						obj.get("documentlink").getAsString()
						);
		return doc;
	}

}
