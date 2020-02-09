package com.td.dcts.eso.experience.model;


//ToDo This class is added temporarily and it should be removed

public class ApplicationMetaData {

  private String sessionId;
  private String appId;
  private String subAppId;
  private String locale;
  private String authToken;
  private String clientIP;

  public ApplicationMetaData() {
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public String getSubAppId() {
    return subAppId;
  }

  public void setSubAppId(String subAppId) {
    this.subAppId = subAppId;
  }

  public String getLocale() {
    return locale;
  }

  public void setLocale(String locale) {
    this.locale = locale;
  }

  public String getAuthToken() {
    return authToken;
  }

  public void setAuthToken(String authToken) {
    this.authToken = authToken;
  }

  public String getClientIP() {
    return clientIP;
  }

  public void setClientIP(String clientIP) {
    this.clientIP = clientIP;
  }
}
