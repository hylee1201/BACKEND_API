package com.td.dcts.eso.experience.model;

public class LoginInfo {

  private String systemName;
  private DevicePrintVal deviceInfo;
  private String loginId;
  private String loginStatus;

  private String eventName;


  public String getSystemName() {
    return systemName;
  }

  public void setSystemName(String systemName) {
    this.systemName = systemName;
  }

  public DevicePrintVal getDeviceInfo() {
    return deviceInfo;
  }

  public void setDeviceInfo(DevicePrintVal deviceInfo) {
    this.deviceInfo = deviceInfo;
  }

  public String getLoginId() {
    return loginId;
  }

  public void setLoginId(String loginId) {
    this.loginId = loginId;
  }

  public String getLoginStatus() {
    return loginStatus;
  }

  public void setLoginStatus(String loginStatus) {
    this.loginStatus = loginStatus;
  }

  public String getEventName() {
    return eventName;
  }

  public void setEventName(String eventName) {
    this.eventName = eventName;
  }

}
