package com.td.dcts.eso.experience.environmentjs.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

import java.util.Map;

public class EnvironmentJsEnumKeyValueType {

  private Map<String, String> data;

  public EnvironmentJsEnumKeyValueType(Map<String, String> data) {
    this.data = data;
  }

  @JsonAnyGetter
  public Map<String, String> getData() {
    return data;
  }
}
