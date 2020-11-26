package com.camunda.demo.application_for_paper;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

/**
 * @author NHG 
 * This class functions as a generic mail interface.
 * in the execution object it is expected that mail_subject, mail_message, mail_address is set
 */
public class send_notification implements JavaDelegate {
	private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private static final String host = "scientific-paper-swim-process-mailhog";
	private static final int port = 1025;

	public send_notification() {
		logger.setLevel(Level.ALL);
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		String subject = (String) execution.getVariable("mail_subject");
		String message = (String) execution.getVariable("mail_message");
		String mail_address = (String) execution.getVariable("mail_address");
		
		
		
		Email email = new SimpleEmail();
		email.setHostName(host);
		email.setSmtpPort(port);
		//email.setAuthentication(USER,PWD);
		email.setFrom("noreply@moodle.dhbw-mannheim.de");
		email.setSubject(subject);
		email.setMsg(message);
		email.addTo(mail_address);
		email.send();

	}

}
