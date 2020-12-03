package de.swimdhbw.scientificpaper;

public class Document {
	private String documentID;
	private String documentLink;

	public Document(String documentID, String documentLink) {
		this.documentID=documentID;
		this.documentLink=documentLink;
	}
	

	public String getDocumentID() {
		return documentID;
	}

	public String getDocumentLink() {
		return documentLink;
	}

}
