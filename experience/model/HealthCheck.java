package com.td.dcts.eso.experience.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HealthCheck {

  @JsonProperty("endpointName")
  private String endpointName;
  @JsonProperty("URL")
  private String URL;
  @JsonProperty("EnvironmentKey")
  private String EnvironmentKey;
  @JsonProperty("enableCheck")
  private String enableCheck;
  @JsonProperty("method")
  private String method;
  @JsonProperty("requestEntity")
  private String requestEntity;
  @JsonProperty("loginStatus")
  private String loginStatus;
  @JsonProperty("lastRunTime")
  private String lastRunTime;
  @JsonProperty("successFlag")
  private String successFlag;
  @JsonProperty("response")
  private String response;

  public String getEndpointName() {
    return endpointName;
  }

  public void setEndpointName(String endpointName) {
    this.endpointName = endpointName;
  }

  public String getURL() {
    return URL;
  }

  public void setURL(String URL) {
    this.URL = URL;
  }

  public String getEnvironmentKey() {
    return EnvironmentKey;
  }

  public void setEnvironmentKey(String environmentKey) {
    EnvironmentKey = environmentKey;
  }

  public String getEnableCheck() {
    return enableCheck;
  }

  public void setEnableCheck(String enableCheck) {
    this.enableCheck = enableCheck;
  }

  public String getRequestEntity() {
    return requestEntity;
  }

  public void setRequestEntity(String requestEntity) {
    this.requestEntity = requestEntity;
  }

  public String getLoginStatus() {
    return loginStatus;
  }

  public void setLoginStatus(String loginStatus) {
    this.loginStatus = loginStatus;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String getLastRunTime() {
    return lastRunTime;
  }

  public void setLastRunTime(String lastRunTime) {
    this.lastRunTime = lastRunTime;
  }

  public String getSuccessFlag() {
    return successFlag;
  }

  public void setSuccessFlag(String successFlag) {
    this.successFlag = successFlag;
  }

  public String getResponse() {
    return response;
  }

  public void setResponse(String response) {
    this.response = response;
  }
}
