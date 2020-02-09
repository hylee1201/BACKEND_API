package com.td.dcts.eso.experience.model.response;

import com.td.dcts.eso.disclosureadapter.model.ConsentAgreementBo;
import com.td.dcts.eso.disclosureadapter.model.RestEventChannelBo;


import java.util.List;

public class CaptureConsentHandlerOutput {

	private List<ConsentAgreementBo> agreement;
	private RestEventChannelBo eventChannel;
	private boolean error;
	private String errorMessage;

	public List<ConsentAgreementBo> getAgreement() {
		return agreement;
	}

	public void setAgreement(List<ConsentAgreementBo> agreement) {
		this.agreement = agreement;
	}

	public RestEventChannelBo getEventChannel() {
		return eventChannel;
	}

	public void setEventChannel(RestEventChannelBo eventChannel) {
		this.eventChannel = eventChannel;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}



}
