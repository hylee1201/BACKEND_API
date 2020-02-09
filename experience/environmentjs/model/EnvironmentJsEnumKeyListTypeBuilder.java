package com.td.dcts.eso.experience.environmentjs.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EnvironmentJsEnumKeyListTypeBuilder {
  private Map<String, List<String>> data = new LinkedHashMap();

  public EnvironmentJsEnumKeyListTypeBuilder setDescAndCode(String desc, String code) {
    if (data.get(desc) != null) {
      List<String> list = data.get(desc);
      list.add(code);
    } else {
      List<String> list = new ArrayList<>();
      list.add(code);
      data.put(desc, list);
    }
    return this;
  }

  public EnvironmentJsEnumKeyListType createEnvironmentJsEnumSimpleType() {
    return new EnvironmentJsEnumKeyListType(this.data);
  }
}
