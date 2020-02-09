package com.td.dcts.eso.experience.model.request.signature;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.td.dcts.eso.experience.model.esignlive.ESignatureEvent;
import com.td.dcts.eso.experience.model.esignlive.ESignatureEventRequest;
import com.td.dcts.eso.experience.model.response.MetaData;

@JsonInclude(Include.NON_NULL)
public class ESignatureEventRequestWrapper {
	private MetaData metaData;
	private ESignatureEvent eSignatureEvent;

  public MetaData getMetaData() {
    return metaData;
  }

  public void setMetaData(MetaData metaData) {
    this.metaData = metaData;
  }

  public ESignatureEvent geteSignatureEvent() {
    return eSignatureEvent;
  }

  public void seteSignatureEvent(ESignatureEvent eSignatureEvent) {
    this.eSignatureEvent = eSignatureEvent;
  }
}
