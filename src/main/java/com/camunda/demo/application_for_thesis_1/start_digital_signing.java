package com.camunda.demo.application_for_thesis_1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class start_digital_signing implements JavaDelegate {
	private static final String db_host = "jdbc:mysql://scientific-paper-swim-process-moodle-mariadb:3306/bitnami_moodle";
	private static final String db_driver = "org.gjt.mm.mysql.Driver";
	private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public start_digital_signing() {
		logger.setLevel(Level.ALL);
	}

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		// Fill when Digital Signature is implemented

	}

}
