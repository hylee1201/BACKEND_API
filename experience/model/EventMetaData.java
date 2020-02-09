package com.td.dcts.eso.experience.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "request",
  "response",
  "systemName"
})
public class EventMetaData {

  @JsonProperty("request")
  private Object request;

  @JsonProperty("response")
  private Object response;

  @JsonProperty("systemName")
  private String systemName;

  public Object getRequest() {
    return request;
  }

  public void setRequest(Object request) {
    this.request = request;
  }

  public Object getResponse() {
    return response;
  }

  public void setResponse(Object response) {
    this.response = response;
  }

  public String getSystemName() {
    return systemName;
  }

  public void setSystemName(String systemName) {
    this.systemName = systemName;
  }
}
