package com.td.dcts.eso.experience.model.response;

public class CustomerProfileResponse {

	private boolean isCompliant;
	private boolean alertCheck;
	private String firstName;
	private String lastName;
	private String partyID;

	public boolean isCompliant() {
		return isCompliant;
	}

	public void setCompliant(boolean isCompliant) {
		this.isCompliant = isCompliant;
	}

	public boolean isAlertCheck() {
		return alertCheck;
	}

	public void setAlertCheck(boolean alertCheck) {
		this.alertCheck = alertCheck;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPartyID() {
		return partyID;
	}

	public void setPartyID(String partyID) {
		this.partyID = partyID;
	}
}