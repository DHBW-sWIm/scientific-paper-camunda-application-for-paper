package com.camunda.demo.application_for_paper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import camundajar.com.google.gson.JsonElement;
import camundajar.com.google.gson.JsonObject;
import camundajar.com.google.gson.JsonParser;

public class DigitalSignature {
	static final Logger logger = LoggerFactory.getLogger(DigitalSignature.class);
	private String businessKey, callbackURL, callbackBody, documentID, documentLink;
	private HashMap<String, SignatureStep> signatures = new HashMap<String, SignatureStep>();

	public DigitalSignature(String businessKey) {
		this.businessKey=businessKey;
		this.callbackURL="";
		this.callbackBody="";
		this.documentID="";
		this.documentLink="";
	}
	
	public DigitalSignature() {
		this.businessKey="";
		this.callbackURL="";
		this.callbackBody="";
		this.documentID="";
		this.documentLink="";
	}
	
	public void setBusinessKey(String businessKey) {
		this.businessKey=businessKey;
	}
	
	public void setCallback(String callbackURL, String callbackBody) {
		this.callbackBody=callbackBody;
		this.callbackURL=callbackURL;
	}
	
	public void setDocument(String docID, String docLink) {
		this.documentID=docID;
		this.documentLink=docLink;
	}

	
	public void addStep(SignatureStep step) {
		this.signatures.put(String.valueOf(step.getID()), step);
	}
	
	public void addStep(int id) {
		SignatureStep step = new SignatureStep(id);
		this.signatures.put(String.valueOf(step.getID()), step);
	}
	
	public void addStep() {
		SignatureStep step = new SignatureStep(this.signatures.size()+1);
		this.signatures.put(String.valueOf(step.getID()), step);
	}
	
	public SignatureStep getStep(int id) {
		return this.signatures.get(String.valueOf(id));
	}
	
	public SignatureStep[] getSteps() {
		return this.signatures.entrySet().toArray(new SignatureStep[this.signatures.size()]);
	}
	
	public void addSigneeToStep(String step, String name, String surname, String mail) {
		this.signatures.get(step).addSignee(new Signee(name, surname, mail));
	}
	
	public String toJSON() {
		String json = String.format("{\"document\": {\"documentID\": \"%s\", \"documentLink\": \"%s\"}, \"signees\": {", this.documentID, this.documentLink);
		Set<Entry<String, SignatureStep>> entries = this.signatures.entrySet();
		boolean isFirst=true;
		for (Entry<String, SignatureStep> entry: entries) {
			if(!isFirst) {
				json+=", ";
			}
			json+=entry.getValue().toJson();
			isFirst=false;
		}
		json += String.format("}, \"callback\": {\"link\": \"%s\", \"body\": \"%s\"}}", this.callbackURL, this.callbackBody.replace("\"", "\\\""));
		return json;
	}
	
	public static DigitalSignature fromJSON(String json) {
		DigitalSignature ds = new DigitalSignature();
		
		JsonObject obj = (JsonObject) new JsonParser().parse(json);
		
		try {
			JsonObject document = obj.get("document").getAsJsonObject();
		
			ds.setDocument(document.get("documentID").getAsString(), document.get("documentLink").getAsString());
		} catch (NullPointerException e){
			ds.setDocument(null, null);
		}
		
		try {
			JsonObject callback = obj.get("callback").getAsJsonObject();
			
			ds.setCallback(callback.get("link").getAsString(), callback.get("body").getAsString());
		} catch (NullPointerException e) {
			logger.warn("no callback could be set");
		}
		
		try {
			JsonObject signatures = obj.get("signees").getAsJsonObject();
			SignatureStep tmp = null;
			Set<Entry<String, JsonElement>> entries = signatures.entrySet();
			for (Entry<String, JsonElement> entry: entries) {
				ds.addStep(SignatureStep.fromJSON(String.format("{\"%s\": %s}", entry.getKey(), entry.getValue()), Integer.valueOf(entry.getKey())));
			}
			
			
		} catch (NullPointerException e) {
			
		}
		
		return ds;
	}
	
	public boolean equals(Object obj) {
		try {
			DigitalSignature signIn = (DigitalSignature) obj;
			assert signIn.toJSON().equals(this.toJSON());
		}catch (Error e) {
			return false;
		}
		
		return true;
		
	}

}
