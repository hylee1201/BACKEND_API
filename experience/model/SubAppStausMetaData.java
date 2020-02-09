package com.td.dcts.eso.experience.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "subApplicationStatus"
})
public class SubAppStausMetaData {

  @JsonProperty("subApplicationStatus")
  private String subApplicationStatus;

  public String getSubApplicationStatus() {
    return subApplicationStatus;
  }

  public void setSubApplicationStatus(String subApplicationStatus) {
    this.subApplicationStatus = subApplicationStatus;
  }
}
