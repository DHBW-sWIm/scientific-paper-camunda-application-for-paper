package de.swimdhbw.scientificpaper;

public class SignatureStep {
	private int step;
	private Signee[] signees;

	public SignatureStep(int step, Signee[] signees) {
		this.step=step;
		this.signees=signees;
	}
	
	public SignatureStep(String step, Signee[] signees) {
		this.step=Integer.valueOf(step);
		this.signees=signees;
	}

	public int getStep() {
		return step;
	}

	public Signee[] getSignees() {
		return signees;
	}
	
	public String toString() {
		return String.format("Step %d contains %d steps", this.step, this.signees.length);
	}
	
	public void execute() {
		for (int i=0; i<this.signees.length; i++) {
			this.signees[i].sendMail();
		}
	}

}
