package com.camunda.demo.application_for_paper;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.spin.json.SpinJsonNode;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

import java.util.Collection;
import java.util.Iterator;
//import java.util.logging.ConsoleHandler;
//import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.camunda.spin.Spin.JSON;

import java.sql.*;

public class sendmail_cp_Delegate implements JavaDelegate {
private static final String host = "scientific-paper-swim-process-mailhog";
private static final int port = 1025;
//private static final String USER = "";
//private static final String PWD = "";
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		logger.setLevel(Level.ALL);
		try {
			String datastring = (String) execution.getVariable("datastring");
			SpinJsonNode json = JSON(datastring);
			Collection<SpinJsonNode> changeCollection = json.prop("changes").elements();
			Iterator<SpinJsonNode> iterator = changeCollection.iterator();
			while(iterator.hasNext()){
				SpinJsonNode entry = JSON(iterator.next().toString());
				String corporatepartnerid = entry.prop("corporatepartnerid").toString();
				String cpmail = getMailAddress(corporatepartnerid);
				logger.severe(cpmail);
				sendMail(cpmail);
			}
		} catch (Exception e) {
			logger.severe(e.toString());
			sendWarning(e);
		}
	}
	
	public void sendMail (String mailaddress){
		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		logger.setLevel(Level.ALL);
		try {
			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(port);
			//email.setAuthentication(USER,PWD);
			email.setFrom("noreply@moodle.dhbw-mannheim.de");
			email.setSubject("Application for Paper has been submitted.");
			email.setMsg("Hey Corporate Partner! \n A new scientific paper application has been submitted. Please Take the following actions: \n 1. Log into moodle with your moodle account. \n 2. Navigate to into open applications. \n 3. You are now able to digitally sign the new application following the steps in Moodle.");
			email.addTo(mailaddress);
			email.send();
		} catch (Exception e) {
			logger.warning(e.toString());
			sendWarning(e);
		}
	}
	
	public String getMailAddress (String id) {
		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		logger.setLevel(Level.ALL);
		String mailaddress = "initialMailAddress";
		try {
			logger.severe("CP: Now connecting to database.");
		    String myDriver = "org.gjt.mm.mysql.Driver";
		    String myUrl = "jdbc:mysql://scientific-paper-swim-process-moodle-mariadb:3306/bitnami_moodle";
		    Class.forName(myDriver);
		    Connection conn = DriverManager.getConnection(myUrl, "bn_moodle", "swim-access");
		    logger.severe("CP: Now reading from database.");
		    String query = "SELECT email FROM mdl_user WHERE id=?";
		    PreparedStatement preparedStmt = conn.prepareStatement(query);
		    preparedStmt.setString (1, id);
		    ResultSet rs = preparedStmt.executeQuery();
		    if (rs != null && rs.next()) {
		    	mailaddress = rs.getString("email");
			} else {
				mailaddress = "noMailAddressFound";
				logger.severe("No mail address found for this corporate partner");
			}
		    preparedStmt.close();
			conn.close();
			logger.severe("Mail found: " + mailaddress);
		} catch (Exception e) {
			logger.severe(e.toString());
			sendWarning(e);
		}
		return mailaddress;
	}
	
	public void sendWarning (Exception e) {
		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		logger.setLevel(Level.ALL);
		try {
			Email email = new SimpleEmail();
			email.setHostName(host);
			email.setSmtpPort(port);
			//email.setAuthentication(USER,PWD);
			email.setFrom("error@error.er");
			email.setSubject("Mist, hier ist ein Fehler unterlaufen. Error.");
			email.setMsg(e.toString());
			email.addTo("warning@warner.bros");
			email.send();
		} catch (Exception f) {
			logger.severe(f.toString());
			f.printStackTrace();
		}
	} 
}
