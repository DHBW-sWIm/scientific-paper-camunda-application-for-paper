package de.swimdhbw.scientificpaper;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Signee {
	private String name;
	private String surname;
	private String mail;
	private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public Signee(String name, String surname, String mail) {
		this.name=name;
		this.surname=surname;
		this.mail=mail;
		logger.setLevel(Level.INFO);
	}
	
	public Signee(String name, String surname, String mail, Level loggerLevel) {
		this.name=name;
		this.surname=surname;
		this.mail=mail;
		logger.setLevel(loggerLevel);
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}


	public String getMail() {
		return mail;
	}
	
	public void sendMail() {
		logger.warning("No mail sent for " + this.surname + " " + this.name);
	}

}
