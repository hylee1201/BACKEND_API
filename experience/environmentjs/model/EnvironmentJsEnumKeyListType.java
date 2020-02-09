package com.td.dcts.eso.experience.environmentjs.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

import java.util.List;
import java.util.Map;

public class EnvironmentJsEnumKeyListType {

  private Map<String, List<String>> data;

  public EnvironmentJsEnumKeyListType(Map<String, List<String>> data) {
    this.data = data;
  }

  @JsonAnyGetter
  public Map<String, List<String>> getData() {
    return data;
  }
}
