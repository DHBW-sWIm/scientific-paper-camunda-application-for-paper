package com.camunda.demo.application_for_paper;

import java.util.ArrayList;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import camundajar.com.google.gson.JsonElement;
import camundajar.com.google.gson.JsonObject;
import camundajar.com.google.gson.JsonParser;

public class SignatureStep {
	private int id;
	private ArrayList<Signee> signees = new ArrayList<Signee>();

	public SignatureStep(int id) {
		this.id=id;
	}
	
	public SignatureStep() {
		this.id=(Integer) null;
	}
	
	public void setID(int id) {
		this.id=id;
	}
	
	public void setID(String id) {
		this.id=Integer.valueOf(id);
	}
	
	public int getID() {
		return this.id;
	}
	
	public void addSignee(Signee signee) {
		this.signees.add(signee);
	}
	
	public String toJson() {
		Signee[] signees = this.signees.toArray(new Signee[this.signees.size()]); 
		String json = String.format("\"%d\": [", this.id);
		boolean isFirst=true;
		for (int i=0; i<signees.length; i++) {
			if(!isFirst)
				json+=", ";
			json += signees[i].toJSON();
			isFirst=false;
		}
		json+="]";
		return json;
		
	}
	
	public static SignatureStep fromJSON(String json, int id) {
		JsonObject steps = (JsonObject) new JsonParser().parse(json);
		SignatureStep step = new SignatureStep(id);
		
		
		Iterator<JsonElement> it = steps.get(String.valueOf(id)).getAsJsonArray().iterator();
		
		Signee tmp = null;
		while (it.hasNext()) {
			JsonElement temp = it.next();
			tmp = Signee.fromJSON(temp.toString());
			step.addSignee(tmp);
		}
		
		return step;
	}
	
	public boolean equals(Object obj) {
		try {
			SignatureStep stepIn = (SignatureStep) obj;
			Signee[] signeesThis = this.signees.toArray(new Signee[this.signees.size()]);
			Signee[] signeesIn = stepIn.signees.toArray(new Signee[stepIn.signees.size()]); 
			assert signeesIn.length==signeesThis.length;
			
			boolean oneMissing = false;
			for (int i=0; i<signeesThis.length; i++) {
				boolean error = true;
				for (int j=0; j<signeesIn.length; j++) {
					if (signeesIn[j].equals(signeesThis[i])) {
						error=false;
					}
				}
				if (error) {
					oneMissing = true;
				}
			}
			assert !oneMissing;
			
		} catch (Exception e) {
			return false;
		}
		
		
		return true;
	}

}
