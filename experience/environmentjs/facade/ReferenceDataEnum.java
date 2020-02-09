package com.td.dcts.eso.experience.environmentjs.facade;

public enum ReferenceDataEnum {
  TITLE("title"), COUNTRY("country"), MARITALSTATUS("maritalStatus"), SUFFIX("suffix"), INDUSTRY("industry"), OCCUPATIONS("occupations"),
  EMPLOYMENTSTATUS("employmentStatus"), CANADIANPROVINCES("canadianProvinces"), USASTATES("usaStates"), PHONETYPES("phoneTypes"),
  UNITTYPES("unitTypes"), PHONECOUNTRYCODE("phoneCountryCode"), FINANCIALINSTITUTIONS("financialInstitutions"), ACCOUNTPURPOSES("accountPurposes"),
  CURRENCY("currency"), MAINTENANCEFEESFROM("maintenanceFeesFrom"), SHAREHOLDERCOMMUNICATIONCODES("shareholderCommunicationCodes"), ACCOUNTPURPOSESTFSARRSP("accountPurposesTfsaRrsp"),
  SOURCEOFINCOME("sourceOfIncome"), PERSONALRELATIONSHIP("personalRelationship"), BUSINESSRELATIONSHIP("businessRelationship");

  private String referenceDataName;

  ReferenceDataEnum(String referenceDataName) {
    this.referenceDataName = referenceDataName;
  }

  public String getReferenceDataName() {
    return referenceDataName;
  }
}
