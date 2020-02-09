package com.td.dcts.eso.experience.environmentjs.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class EnvironmentJsEnumKeyValueTypeBuilder {
  private Map<String, String> data = new LinkedHashMap();

  public EnvironmentJsEnumKeyValueTypeBuilder setDescAndCode(String desc, String code) {
    this.data.put(desc, code);
    return this;
  }

  public EnvironmentJsEnumKeyValueType createEnvironmentJsEnumSimpleType() {
    return new EnvironmentJsEnumKeyValueType(this.data);
  }
}
