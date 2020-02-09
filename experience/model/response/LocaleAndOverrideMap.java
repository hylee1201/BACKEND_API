package com.td.dcts.eso.experience.model.response;

import java.util.Locale;
import java.util.Map;

public class LocaleAndOverrideMap {

  private Locale locale;
  private Map<String, String> overrideMap;

  public LocaleAndOverrideMap() {
  }

  public LocaleAndOverrideMap(Locale locale, Map<String, String> overrideMap) {
    this.locale = locale;
    this.overrideMap = overrideMap;
  }

  public Locale getLocale() {
    return locale;
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  public Map<String, String> getOverrideMap() {
    return overrideMap;
  }

  public void setOverrideMap(Map<String, String> overrideMap) {
    this.overrideMap = overrideMap;
  }

  @Override
  public String toString() {
    return "LocaleAndOverrideMap{" +
      "locale=" + locale +
      ", overrideMap=" + overrideMap +
      '}';
  }
}
