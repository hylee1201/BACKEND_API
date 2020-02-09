package com.td.dcts.eso.event.response.model;

import java.util.List;

public class ApplicationInfo extends ProductInfo {

	private Long applicationId;
	private List<SubApplicationInfo> subApplications;

	public Long getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
	}

	public List<SubApplicationInfo> getSubApplications() {
		return subApplications;
	}

	public void setSubApplications(List<SubApplicationInfo> subApplications) {
		this.subApplications = subApplications;
	}
}
