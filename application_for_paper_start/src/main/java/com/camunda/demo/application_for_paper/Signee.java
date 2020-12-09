package com.camunda.demo.application_for_paper;

import camundajar.com.google.gson.JsonObject;
import camundajar.com.google.gson.JsonParser;

public class Signee {
	private String name, surname, email;

	public Signee(String name, String surname, String email) {
		this.name=name;
		this.surname=surname;
		this.email=email;
	}
	
	public String toJSON() {
		return String.format("{\"name\": \"%s\", \"surname\": \"%s\", \"email\": \"%s\"}", this.name, this.surname, this.email);
	}
	
	public static Signee fromJSON(String json) {
		JsonObject obj = (JsonObject) new JsonParser().parse(json);
		return new Signee(obj.get("name").getAsString(), obj.get("surname").getAsString(), obj.get("email").getAsString());
	}
	
	public boolean equals(Object obj) {
		try {
			Signee in = (Signee) obj;
			assert in.name==this.name;
			assert in.surname==this.surname;
			assert in.email==this.email;
		}catch (Exception e) {
			return false;
		}
		
		return true;
	}

}
