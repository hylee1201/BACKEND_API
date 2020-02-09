package com.td.dcts.eso.experience.model.response;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "codeValid"
})
public class PromoCodeStatus {

  @JsonProperty("codeValid")
  private boolean codeValid;

  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  @JsonProperty("codeValid")
  public boolean getCodeValid() {
    return codeValid;
  }

  @JsonProperty("codeValid")
  public void setCodeValid(boolean codeValid) {
    this.codeValid = codeValid;
  }

  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }

}
