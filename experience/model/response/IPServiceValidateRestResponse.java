package com.td.dcts.eso.experience.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "eid", "status" })
@JsonInclude(Include.NON_NULL)
public class IPServiceValidateRestResponse {
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private static final long serialVersionUID = 1L;
	private String eid;
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEid() {
		return eid;
	}

	public void setEid(String eid) {
		this.eid = eid;
	}
}