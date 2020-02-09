package com.td.dcts.eso.event.response.model;

public class SaveIPRequest {
	
	private String ipAddress;
	private String json;
	private String businessOutcomeCode;
	private String subApplicationId;
	
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	public String getBusinessOutcomeCode() {
		return businessOutcomeCode;
	}
	public void setBusinessOutcomeCode(String businessOutcomeCode) {
		this.businessOutcomeCode = businessOutcomeCode;
	}
	public String getSubApplicationId() {
		return subApplicationId;
	}
	public void setSubApplicationId(String subApplicationId) {
		this.subApplicationId = subApplicationId;
	}
	

}
