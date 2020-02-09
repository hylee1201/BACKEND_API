package com.td.dcts.eso.experience.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.td.dcts.eso.disclosureadapter.model.DisclosureBo;
import com.td.dcts.eso.disclosureadapter.model.RestEventChannelBo;
import com.td.dcts.eso.experience.model.response.Status;

import java.util.List;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "status", "disclosuresList", "eventChannel"})
public class GetDisclosureListResponse extends BaseResponseContent {
	 	/**
	 *
	 */
	private static final long serialVersionUID = -6711001193632235626L;
		private Status status;
	    private List<DisclosureBo> disclosuresList;
	    private RestEventChannelBo eventChannel;
		@Override
		public Status getStatus() {
			// TODO Auto-generated method stub
			return status;
		}
		@Override
		public void setStatus(Status status) {
			this.status=status;

		}
		public List<DisclosureBo> getDisclosuresList() {
			return disclosuresList;
		}
		public void setDisclosuresList(List<DisclosureBo> disclosuresList) {
			this.disclosuresList = disclosuresList;
		}
		public RestEventChannelBo getEventChannel() {
			return eventChannel;
		}
		public void setEventChannel(RestEventChannelBo eventChannel) {
			this.eventChannel = eventChannel;
		}
}
