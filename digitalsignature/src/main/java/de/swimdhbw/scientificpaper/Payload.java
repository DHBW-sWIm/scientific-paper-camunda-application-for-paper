package de.swimdhbw.scientificpaper;

public class Payload {
	private String businessKey;
	private Document document;
	private SignatureStep[] signees;
	private Callback callback;
	

	public Payload(String businessKey, Document document, SignatureStep[] signees, Callback callback) {
		this.setBusinessKey(businessKey);
		this.setDocument(document);
		this.setSignees(signees);
		this.setCallback(callback);
	}


	public String getBusinessKey() {
		return businessKey;
	}


	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}


	public Document getDocument() {
		return document;
	}


	public void setDocument(Document document) {
		this.document = document;
	}


	public SignatureStep[] getSignees() {
		return signees;
	}


	public void setSignees(SignatureStep[] signees) {
		this.signees = signees;
	}


	public Callback getCallback() {
		return callback;
	}


	public void setCallback(Callback callback) {
		this.callback = callback;
	}

}
