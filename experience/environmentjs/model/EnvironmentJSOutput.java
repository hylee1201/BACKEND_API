package com.td.dcts.eso.experience.environmentjs.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.td.eso.rest.response.model.LookupModel;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnvironmentJSOutput {

  private String uploadIdUrl;

  private JavaScriptEnumValues javaScriptEnumValues;
  private Map<String, List<? extends LookupModel>> referenceData;

  private boolean bootstrapDone;
  private String otherValueCode = "OTHER";

private String  otherAccountPurpose = "OTHER";

  private String channel = "Branch";


  private String wcmContentLocation;

  private String credentialsUsernamePattern;

  private String language;

  private String applicationId;

  private String canadaPhoneCode;

  private List<String> highRiskOccupationCodes = Arrays.asList("140003",
    "140004",
    "140005",
    "140006",
    "140007",
    "140008",
    "140009",
    "140010",
    "140011",
    "140999",
    "180014",
    "230004",
    "270008",
    "270011"
  );
  private List<String> nexusCountries = Arrays.asList("CA","US");


  @JsonUnwrapped
  public JavaScriptEnumValues getJavaScriptEnumValues() {
    return javaScriptEnumValues;
  }

  public void setJavaScriptEnumValues(JavaScriptEnumValues javaScriptEnumValues) {
    this.javaScriptEnumValues = javaScriptEnumValues;
  }

  @JsonProperty(value = "dropdowns")
  public Map<String, List<? extends LookupModel>> getReferenceData() {
    return referenceData;
  }

  public void setReferenceDataHandlerOutput(Map<String, List<? extends LookupModel>> referenceData) {
    this.referenceData = referenceData;
  }

  @JsonProperty(value = "uploadIDUrl")
  public String getUploadIdUrl() {
    return uploadIdUrl;
  }

  public void setUploadIdUrl(String uploadIdUrl) {
    this.uploadIdUrl = uploadIdUrl;
  }

  public boolean isBootstrapDone() {
    return bootstrapDone;
  }

  public void setBootstrapDone(boolean bootstrapDone) {
    this.bootstrapDone = bootstrapDone;
  }

  @JsonProperty("otherValueCode")
  public String getOtherValueCode() {
    return otherValueCode;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getWcmContentLocation() {
    return wcmContentLocation;
  }

  public void setWcmContentLocation(String wcmContentLocation) {
    this.wcmContentLocation = wcmContentLocation;
  }

  public String getCredentialsUsernamePattern() {
    return credentialsUsernamePattern;
  }

  public void setCredentialsUsernamePattern(String credentialsUsernamePattern) {
    this.credentialsUsernamePattern = credentialsUsernamePattern;
  }

  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  public String getCanadaPhoneCode() {
    return canadaPhoneCode;
  }

  public void setCanadaPhoneCode(String canadaPhoneCode) {
    this.canadaPhoneCode = canadaPhoneCode;
  }

  public List<String> getHighRiskOccupationCodes() {
    return highRiskOccupationCodes;
  }

  public void setHighRiskOccupationCodes(List<String> highRiskOccupationCodes) {
    this.highRiskOccupationCodes = highRiskOccupationCodes;
  }

  public List<String> getNexusCountries() {
    return nexusCountries;
  }

  public void setNexusCountries(List<String> nexusCountries) {
    this.nexusCountries = nexusCountries;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  @JsonProperty("otherAccountPurpose")
  public String getOtherAccountPurpose() {
    return otherAccountPurpose;
  }

  public void setOtherAccountPurpose(String otherAccountPurpose) {
    this.otherAccountPurpose = otherAccountPurpose;
  }
}
