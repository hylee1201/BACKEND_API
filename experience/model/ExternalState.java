package com.td.dcts.eso.experience.model;

public class ExternalState {

private String userType;
private String lastCard;
private String lastState;

  public String getUserType() {
    return userType;
  }

  public void setUserType(String userType) {
    this.userType = userType;
  }

  public String getLastCard() {
    return lastCard;
  }

  public void setLastCard(String lastCard) {
    this.lastCard = lastCard;
  }

  public String getLastState() {
    return lastState;
  }

  public void setLastState(String lastState) {
    this.lastState = lastState;
  }
}
