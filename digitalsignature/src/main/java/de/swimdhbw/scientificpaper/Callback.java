package de.swimdhbw.scientificpaper;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.logging.Logger;

public class Callback {
	private String link;
	private String body;
	private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public Callback(String link, String body) {
		this.link=link;
		this.body=body;
	}

	public String getLink() {
		return link;
	}

	public String getBody() {
		return body;
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
		if (restConn.getResponseCode()!=204) {
			logger.warning(this.link);
			logger.warning(body);
			throw new RuntimeException("Ending of digital signature request failed with" + String.valueOf(restConn.getResponseCode()));
		}else {
			System.out.println("Process digital signature ended");
		}
		
	}

}
