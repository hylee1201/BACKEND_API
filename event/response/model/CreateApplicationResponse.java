package com.td.dcts.eso.event.response.model;

import com.td.coreapi.common.status.Status;

public class CreateApplicationResponse {

	private ApplicationInfo application;
	private Status status;

	public ApplicationInfo getApplication() {
		return application;
	}

	public void setApplication(ApplicationInfo application) {
		this.application = application;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
}
