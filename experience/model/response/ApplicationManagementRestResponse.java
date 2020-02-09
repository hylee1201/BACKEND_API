package com.td.dcts.eso.experience.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ApplicationManagementRestResponse {
	
	private String applicationId;
	private String subApplicationId;
	private String creditReferenceNumber;
	private ApplicationStatus applicationStatus;
	private String partyID;
	private Double maxCreditLimit;
	private Double creditLimitApplied;
	private Integer pollDuration;
	private Integer pollInterval;
	private Object consentData;
	private String pickupBranchAddress;
	private String pickupCustomerAddress;
	
	public String getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
	public String getSubApplicationId() {
		return subApplicationId;
	}
	public void setSubApplicationId(String subApplicationId) {
		this.subApplicationId = subApplicationId;
	}
	public String getCreditReferenceNumber() {
		return creditReferenceNumber;
	}
	public void setCreditReferenceNumber(String creditReferenceNumber) {
		this.creditReferenceNumber = creditReferenceNumber;
	}
	public ApplicationStatus getApplicationStatus() {
		return applicationStatus;
	}
	public void setApplicationStatus(ApplicationStatus applicationStatus) {
		this.applicationStatus = applicationStatus;
	}
	public String getPartyID() {
		return partyID;
	}
	public void setPartyID(String partyID) {
		this.partyID = partyID;
	}
	public Double getMaxCreditLimit() {
		return maxCreditLimit;
	}
	public void setMaxCreditLimit(Double maxCreditLimit) {
		this.maxCreditLimit = maxCreditLimit;
	}
	public Double getCreditLimitApplied() {
		return creditLimitApplied;
	}
	public void setCreditLimitApplied(Double creditLimitApplied) {
		this.creditLimitApplied = creditLimitApplied;
	}
	public Integer getPollDuration() {
		return pollDuration;
	}
	public void setPollDuration(Integer pollDuration) {
		this.pollDuration = pollDuration;
	}
	public Integer getPollInterval() {
		return pollInterval;
	}
	public void setPollInterval(Integer pollInterval) {
		this.pollInterval = pollInterval;
	}
	public Object getConsentData() {
		return consentData;
	}
	public void setConsentData(Object consentData) {
		this.consentData = consentData;
	}
	public String getPickupBranchAddress() {
		return pickupBranchAddress;
	}
	public void setPickupBranchAddress(String pickupBranchAddress) {
		this.pickupBranchAddress = pickupBranchAddress;
	}
	public String getPickupCustomerAddress() {
		return pickupCustomerAddress;
	}
	public void setPickupCustomerAddress(String pickupCustomerAddress) {
		this.pickupCustomerAddress = pickupCustomerAddress;
	}
}