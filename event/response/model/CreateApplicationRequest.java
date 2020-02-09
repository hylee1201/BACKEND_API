package com.td.dcts.eso.event.response.model;

import com.td.dcts.eso.experience.model.response.MetaData;

public class CreateApplicationRequest {
	
	private MetaData metaData;
	private Object data;
	
	public MetaData getMetaData() {
		return metaData;
	}
	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
}
