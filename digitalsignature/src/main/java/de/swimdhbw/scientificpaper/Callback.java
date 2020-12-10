package de.swimdhbw.scientificpaper;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Callback {
	private String link;
	private String body;
	private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public Callback(String link, String body) {
		this.link=link;
		this.body=body;
		logger.setLevel(Level.FINE);
	}
	
	public Callback(String link, String body, Level loggerLevel) {
		this.link=link;
		this.body=body;
		logger.setLevel(loggerLevel);
	}

	public String getLink() {
		return link;
	}

	public String getBody() {
		return body;
	}
	
	public void setBody(String body) {
		logger.finer("New body: "+body);
		this.body=body;
	}
	
	public void execute() throws IOException {
		//prepare request to ds on camunda
		URL url = new URL(this.link);
		HttpURLConnection restConn = (HttpURLConnection) url.openConnection();
		try {
			restConn.setRequestMethod("POST");
			restConn.setRequestProperty("Content-Type", "application/json");
			restConn.setDoOutput(true);
		} catch (ProtocolException e3) {
			e3.printStackTrace();
		}
		OutputStream os = restConn.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(os);
  
		
		osw.write(this.body);
		osw.flush();
		osw.close();
		os.close();
		restConn.connect();
		if (restConn.getResponseCode()>205) {
			logger.severe(this.link);
			logger.severe(body);
			throw new RuntimeException("Ending of digital signature request failed with" + String.valueOf(restConn.getResponseCode()));
		}
		
	}

}
