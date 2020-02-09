package com.td.dcts.eso.experience.model.request.idp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "ipAnswers"
})
public class IPAnswers {

  @JsonProperty("ipAnswers")
  private List<IpAnswer> ipAnswers = null;

  @JsonProperty("ipAnswers")
  public List<IpAnswer> getIpAnswers() {
    return ipAnswers;
  }

  @JsonProperty("ipAnswers")
  public void setIpAnswers(List<IpAnswer> ipAnswers) {
    this.ipAnswers = ipAnswers;
  }


}
