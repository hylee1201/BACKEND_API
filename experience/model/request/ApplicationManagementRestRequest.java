package com.td.dcts.eso.experience.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.td.dcts.eso.experience.model.response.MetaData;

@JsonInclude(Include.NON_NULL)
public class ApplicationManagementRestRequest {
	private MetaData metaData;
	private Object data;
	private Object answersData;
	private Object consentData;

	public Object getData() {
		return data;
	}

	public MetaData getMetaData() {
		return metaData;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}

	public Object getAnswersData() {
		return answersData;
	}

	public void setAnswersData(Object answersData) {
		this.answersData = answersData;
	}

	public Object getConsentData() {
		return consentData;
	}

	public void setConsentData(Object consentData) {
		this.consentData = consentData;
	}
}
