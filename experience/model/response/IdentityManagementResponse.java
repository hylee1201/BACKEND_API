package com.td.dcts.eso.experience.model.response;

public class IdentityManagementResponse {
  private String connectId;
  private String responseCode;
  private WealthClientMasterInfo wcm;

  public String getConnectId() {
    return connectId;
  }

  public void setConnectId(String connectId) {
    this.connectId = connectId;
  }

  public String getResponseCode() {
    return responseCode;
  }

  public void setResponseCode(String responseCode) {
    this.responseCode = responseCode;
  }


  public WealthClientMasterInfo getWcm() {
    return wcm;
  }

  public void setWcm(WealthClientMasterInfo wcm) {
    this.wcm = wcm;
  }


}
