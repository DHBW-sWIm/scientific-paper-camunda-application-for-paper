package com.camunda.demo.application_for_paper;

import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class CreateGroups implements JavaDelegate {
	private static final String db_host = String.format("jdbc:mysql://%s/bitnami_moodle", System.getenv("PROCESS_DB_HOST"));
	private static final String db_user = System.getenv("PROCESS_DB_USER");
	private static final String db_pwd = System.getenv("PROCESS_DB_PWD");
	private static final String db_driver = "org.gjt.mm.mysql.Driver";
	private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		//Implementation for creating groups of students with supervisor

	}

}
