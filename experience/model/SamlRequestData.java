package com.td.dcts.eso.experience.model;

public class SamlRequestData {

  private String samlToken;
  private String lang;
  private String valetKey;

  public String getSamlToken() {
    return samlToken;
  }

  public void setSamlToken(String samlToken) {
    this.samlToken = samlToken;
  }

  public String getLang() {
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }

  public String getValetKey() {
    return valetKey;
  }

  public void setValetKey(String valetKey) {
    this.valetKey = valetKey;
  }
}
