package com.td.dcts.eso.experience.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.td.dcts.eso.event.response.model.SubApplicationInfo;

import java.io.Serializable;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public class MetaData implements Serializable {
  private static final long serialVersionUID = 7582407085803999958L;

	private String sessionId;
  private Integer applicationId;
  private String stage;
  private String channel;
  private String connectId;
  private String flowId;
  private String productId; // this is actually a synthetic productId
  private List<SubApplicationInfo> subApplicationList;
  private String accessCard;
  private String inboundSamlCode;
  private String primaryPartyId;
  private String masterClientID;
  private String cifID;
  private String aliasName;
  private String agentUserId;
  private String systemName;
  private String valetKey;

	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public Integer getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(Integer applicationId) {
		this.applicationId = applicationId;
	}
	public String getStage() {
		return stage;
	}
	public void setStage(String stage) {
		this.stage = stage;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getConnectId() {
		return connectId;
	}
	public void setConnectId(String connectId) {
		this.connectId = connectId;
	}
	public String getFlowId() {
		return flowId;
	}
	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}
	public List<SubApplicationInfo> getSubApplicationList() {
		return subApplicationList;
	}
	public void setSubApplicationList(List<SubApplicationInfo> subApplicationList) {
		this.subApplicationList = subApplicationList;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String syntheticProductId) {
		this.productId = syntheticProductId;
	}

  public String getAccessCard() {
    return accessCard;
  }

  public void setAccessCard(String accessCard) {
    this.accessCard = accessCard;
  }

  public String getInboundSamlCode() {
    return inboundSamlCode;
  }

  public void setInboundSamlCode(String inboundSamlCode) {
    this.inboundSamlCode = inboundSamlCode;
  }

  public String getPrimaryPartyId() {
    return primaryPartyId;
  }

  public void setPrimaryPartyId(String primaryPartyId) {
    this.primaryPartyId = primaryPartyId;
  }

  public String getMasterClientID() {
    return masterClientID;
  }

  public void setMasterClientID(String masterClientID) {
    this.masterClientID = masterClientID;
  }

  public String getCifID() {
    return cifID;
  }

  public void setCifID(String cifID) {
    this.cifID = cifID;
  }

  public String getAliasName() {
    return aliasName;
  }

  public void setAliasName(String aliasName) {
    this.aliasName = aliasName;
  }

  public String getAgentUserId() {
    return agentUserId;
  }

  public void setAgentUserId(String agentUserId) {
    this.agentUserId = agentUserId;
  }

  public String getSystemName() {
    return systemName;
  }

  public void setSystemName(String systemName) {
    this.systemName = systemName;
  }

  public String getValetKey() {
    return valetKey;
  }

  public void setValetKey(String valetKey) {
    this.valetKey = valetKey;
  }
}
