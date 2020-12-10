package com.camunda.demo.application_for_paper;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FillDocument implements JavaDelegate {
	private PDDocument doc;
	static final Logger logger = LoggerFactory.getLogger(FillDocument.class);
	private DataBase db;
	
	public void setDB(DataBase db) {
		this.db=db;
	}
	
	public DataBase getDB() throws ClassNotFoundException {
		if (this.db==null) {
			this.db=new DataBase();
		}
		return this.db;
	}

	/*  studentNachname
		studentVorname
		studentKurs
		studentMatrikelnummer
		studentEmail
		studentTelefon
		unternehmenName
		unternehmenAbteilung
		kursInfo
		BachelorarbeitThema
		BachelorarbeitThemenbereich
		BachelorarbeitThemenschwerpunkt
		wissenschaftlicherbetreuerNachname
		wissenschaftlicherbetreuerVorname
		wissenschaftlicherbetreuerEmail
		wissenschaftlicherbetreuerTelefon
		wissenschaftlicherbetreuerAdresse
		wissenschaftlicherbetreuerStrasse
		wissenschaftlicherbetreuerPostleitzahl
		wissenschaftlicherbetreuerOrt
		unternehmensbetreuerNachname
		unternehmensbetreuerVorname
		unternehmensbetreuerEmail
		unternehmensbetreuerTelefon
		unternehmensbetreuerFunktion
		BachelorarbeitInhalt
		sperrvermerkJaNein
		sperrvermerkBegruendung
		studentNachnameKurz
		studentVornameKurz
		studentKursKurz
		studentMatrikelnummerKurz
		unternehmensvertreterNachname
		unternehmensvertreterVorname
		unternehmensvertreterFunktion
	 */
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		logger.debug("Starting to fill document");
		
		String type = (String) execution.getVariable("applicationType");
		HashMap<String, String> values = (HashMap<String, String>) execution.getVariable("application_info");
		
		doc = PDDocument.load(gettemplateFileForType(type));
		
		values = addStudentInfo(values, (String) execution.getVariable("stid"));
		
		if (type!="PA_I") {
			values = addScientificSupervisorInfo(values, (String)execution.getVariable("supid"));
		}
		
		fillDocument(values);
		
		saveDocument();
		
		this.db.close();

	}
	
	
	//type should be either PA I, PA II or BA
	public File gettemplateFileForType(String type) throws URISyntaxException, IllegalAccessException {
		
		File template = null;
		URL ressourcepath = null;
		logger.debug(type);
		if (type=="PA_I") {
			ressourcepath = getClass().getClassLoader().getResource("anmeldung_pa_1.pdf");
		}else if (type=="PA_II") {
			ressourcepath = getClass().getClassLoader().getResource("anmeldung_pa_2.pdf");
		}else if (type=="BA") {
			ressourcepath = getClass().getClassLoader().getResource("anmeldung_ba.pdf");
		}else {
			logger.warn("The specified type could not be found");
			throw new IllegalAccessException(String.format("The type %s is not permitted", type));
		}
		template = new File(ressourcepath.toURI());
		
		return template;
	}
	
	public void fillDocument(HashMap values) throws IOException {
		PDDocumentCatalog catalog = doc.getDocumentCatalog();
		PDAcroForm form = catalog.getAcroForm();
		List<PDField> fields = form.getFields();
		
		Iterator<PDField> it = fields.iterator();
		
		String key = "";
		PDField field = null;
		while (it.hasNext()) {
			field = it.next();
			key = field.getFullyQualifiedName();
			
			if (!values.containsKey(key)) {
				logger.warn(String.format("The field %s could not be filled, no value present.", key));
			}else {
				// TODO find out which field is kursInfo and what values should be filled
				if (field.getFullyQualifiedName()!="kursInfo")
					try {
						field.setValue((String) values.get(key));
					} catch(IllegalArgumentException e) {
						e.printStackTrace();
					}
			}
		}
	}
	
	// TODO implement storage
	public void saveDocument() throws IOException, URISyntaxException {
		URI uri = getClass().getResource("/").toURI();
		//this.doc.save(uri.toString());
	}
	
	//studentNachname, studentVorname, studentKurs, studentMatrikelnummer, studentEmail, studentTelefon
	public HashMap<String, String> addStudentInfo(HashMap<String, String> values, String stid) throws ClassNotFoundException, SQLException {
		DataBase db = getDB();
		String query = "SELECT lastname, firstname, \"null\" as kurs, \"null\" as matrikelnr, email, phone1 FROM mdl_user WHERE id=?";
		PreparedStatement stmt = db.prepareQuery(query);
		
		stmt.setString(1, stid);
		
		ResultSet result = stmt.executeQuery();
		db.close();
		
		assert result.first();
		assert values!=null;
		
		values.put("studentNachname", result.getString(1));
		values.put("studentVorname", result.getString(2));
		values.put("studentKurs", result.getString(3));
		values.put("studentMatrikelnummer", result.getString(4));
		values.put("studentEmail", result.getString(5));
		values.put("studentTelefon", result.getString(6));
		values.put("studentNachnameKurz", result.getString(1));
		values.put("studentVornameKurz", result.getString(2));
		values.put("studentKursKurz", result.getString(3));
		values.put("studentMatrikelnummerKurz", result.getString(4));
		
		
		return values;
	}
	
	//wissenschaftlicherbetreuerNachname, wissenschaftlicherbetreuerVorname, wissenschaftlicherbetreuerEmail, wissenschaftlicherbetreuerTelefon
	//wissenschaftlicherbetreuerAdresse, wissenschaftlicherbetreuerStrasse, wissenschaftlicherbetreuerPostleitzahl, wissenschaftlicherbetreuerOrt
	public HashMap<String, String> addScientificSupervisorInfo(HashMap<String, String> values, String id) throws ClassNotFoundException, SQLException {
		DataBase db = getDB();
		String query = "SELECT lastname, firstname, email, phone1, address, \"?\" as strasse, \"?\" as plz, city FROM mdl_user WHERE id=?";
		PreparedStatement stmt = db.prepareQuery(query);
		stmt.setString(1, id);
		
		ResultSet result = stmt.executeQuery();
		db.close();
		
		values.put("wissenschaftlicherbetreuerNachname", result.getString(1));
		values.put("wissenschaftlicherbetreuerVorname", result.getString(2));
		values.put("wissenschaftlicherbetreuerEmail", result.getString(3));
		values.put("wissenschaftlicherbetreuerTelefon", result.getString(4));
		values.put("wissenschaftlicherbetreuerAdresse", result.getString(5));
		values.put("wissenschaftlicherbetreuerStrasse", result.getString(6));
		values.put("wissenschaftlicherbetreuerPostleitzahl", result.getString(7));
		values.put("wissenschaftlicherbetreuerOrt", result.getString(8));
		
		
		return values;
	}

}
