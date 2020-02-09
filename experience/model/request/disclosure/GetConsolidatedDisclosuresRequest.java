package com.td.dcts.eso.experience.model.request.disclosure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.td.dcts.eso.disclosureadapter.model.DisclosureBo;
import com.td.dcts.eso.disclosureadapter.model.RestEventChannelBo;

import java.util.List;

@JsonInclude(Include.NON_NULL)
public class GetConsolidatedDisclosuresRequest extends BaseRequestContent {
	private List<DisclosureBo> disclosureList;
	private RestEventChannelBo eventChannel;
	public RestEventChannelBo getEventChannel() {
        return eventChannel;
    }

    public void setEventChannel(RestEventChannelBo eventChannel) {
        this.eventChannel = eventChannel;
    }

    public List<DisclosureBo> getDisclosureList() {
		return disclosureList;
	}

	public void setDisclosureList(List<DisclosureBo> disclosureList) {
		this.disclosureList = disclosureList;
	}
}
