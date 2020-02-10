package com.camunda.demo.application_for_paper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.spin.json.SpinJsonNode;
import static org.camunda.spin.Spin.*;

public class getArray_Delegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		logger.setLevel(Level.ALL);
		try {
			String datastring = (String) execution.getVariable("dbchanges"); // receiving datastring from API Call and parsing data
			execution.setVariable("datastring", datastring);
			SpinJsonNode json = JSON(datastring);
			Collection<SpinJsonNode> changeCollection = json.prop("changes").elements();
			Iterator<SpinJsonNode> iterator = changeCollection.iterator();
			while(iterator.hasNext()){
				SpinJsonNode entry = JSON(iterator.next().toString());
				String studentid = entry.prop("studentid").toString();
				execution.setVariable("studentid", studentid);
				String supervisorid = entry.prop("supervisorid").toString();
				execution.setVariable("supervisorid", supervisorid);
				String assignid = entry.prop("assignid").toString();
				execution.setVariable("assignid", assignid);
				String corporatepartnerid = entry.prop("corporatepartnerid").toString();
				execution.setVariable("corporatepartnerid", corporatepartnerid);
				try {
					logger.severe("Connecting to database.");
				    String myDriver = "org.gjt.mm.mysql.Driver";
				    String myUrl = "jdbc:mysql://scientific-paper-swim-process-moodle-mariadb:3306/bitnami_moodle";
				    Class.forName(myDriver);
				    Connection conn = DriverManager.getConnection(myUrl, "bn_moodle", "swim-access");
				    logger.severe("Writing to database.");
				    String query = " insert into mdl_spam_studenthassupervisor (studentid, supervisorid, assignmentid)"
					        + " values (?, ?, ?) on duplicate key update supervisorid = ?";
				    PreparedStatement preparedStmt = conn.prepareStatement(query);
				    preparedStmt.setString (1, studentid);
				    preparedStmt.setString (2, supervisorid);
				    preparedStmt.setString (3, assignid);
				    preparedStmt.setString (4, supervisorid);
				    preparedStmt.execute();
					conn.close();
				} catch (Exception e) {
					logger.warning(e.toString());
					sendWarning(e);
				}
			}
		} catch (Exception e) {
			logger.warning(e.toString());
			sendWarning(e);
		}
	}
	
	public void sendWarning (Exception e) {
		try {
			Email email = new SimpleEmail();
			email.setHostName("scientific-paper-swim-process-mailhog");
			email.setSmtpPort(1025);
			//email.setAuthentication(USER,PWD);
			email.setFrom("error@error.er");
			email.setSubject("Mist, hier ist ein Fehler unterlaufen. Error.");
			email.setMsg(e.toString());
			email.addTo("warning@warner.bros");
			email.send();
		} catch (Exception f) {
			f.printStackTrace();
		}
	} 
}
