package com.td.dcts.eso.event.response.model;

import java.util.Map;

public class UpdateApplicationRequest {

	private String ipAddress;
	private String subApplicationId;
	private Map<String, Object> metaData;
	private Map<String, Object> appData;
	private ActionType actionType;
	
	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getSubApplicationId() {
		return subApplicationId;
	}

	public void setSubApplicationId(String subApplicationId) {
		this.subApplicationId = subApplicationId;
	}

	public Map<String, Object> getMetaData() {
		return metaData;
	}

	public void setMetaData(Map<String, Object> metaData) {
		this.metaData = metaData;
	}

	public Map<String, Object> getAppData() {
		return appData;
	}

	public void setAppData(Map<String, Object> appData) {
		this.appData = appData;
	}

	public ActionType getActionType() {
		return actionType;
	}

	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}

}
