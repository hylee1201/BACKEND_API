package com.td.dcts.eso.experience.environmentjs.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.td.dcts.eso.experience.model.response.ProfileRoleType;
import com.td.dcts.eso.experience.model.response.ProfileType;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JavaScriptEnumValues {

  public final EnvironmentJsEnumKeyValueType proTypes = new EnvironmentJsEnumKeyValueTypeBuilder()
    .setDescAndCode("self", "SELF").setDescAndCode( "spouse", "SPOUSE").setDescAndCode( "other", "OTHER")
    .setDescAndCode("pro","PRO").createEnvironmentJsEnumSimpleType();
  public final EnvironmentJsEnumKeyValueType proRelationshipTypes = new EnvironmentJsEnumKeyValueTypeBuilder().setDescAndCode("parent", "PARENT").setDescAndCode("child", "CHILD").setDescAndCode("sibling", "SIBLING").createEnvironmentJsEnumSimpleType();
  public final EnvironmentJsEnumKeyValueType maritalStatus = new EnvironmentJsEnumKeyValueTypeBuilder().setDescAndCode("married", "M").setDescAndCode("separated", "P").setDescAndCode("commonLaw", "C").setDescAndCode("single", "S").setDescAndCode("widowed", "W").createEnvironmentJsEnumSimpleType();
  public final EnvironmentJsEnumKeyValueType yesNo = new EnvironmentJsEnumKeyValueTypeBuilder().setDescAndCode("yes", "Y").setDescAndCode("no", "N").createEnvironmentJsEnumSimpleType();
  public final EnvironmentJsEnumKeyValueType phoneTypes = new EnvironmentJsEnumKeyValueTypeBuilder().setDescAndCode("home", "HOME_PHONE").setDescAndCode("mobile", "MOBILE").createEnvironmentJsEnumSimpleType();
  public final EnvironmentJsEnumKeyValueType languages = new EnvironmentJsEnumKeyValueTypeBuilder().setDescAndCode("english" ,"en").setDescAndCode("french", "fr").createEnvironmentJsEnumSimpleType();
  public final EnvironmentJsEnumKeyValueType countryCodes = new EnvironmentJsEnumKeyValueTypeBuilder().setDescAndCode("canada", "CA").setDescAndCode("usa", "US").createEnvironmentJsEnumSimpleType();
  public final EnvironmentJsEnumKeyValueType employmentStatus = new EnvironmentJsEnumKeyValueTypeBuilder().setDescAndCode("selfEmployed", "SELF_EMPLOYED")
    .setDescAndCode("retired","RETIRED").setDescAndCode("student", "STUDENT").setDescAndCode("homeMaker", "HOMEMAKER").setDescAndCode("unemployed", "UNEMPLOYED")
    .setDescAndCode("employed", "EMPLOYED").setDescAndCode("caregiver", "CAREGIVER").setDescAndCode("atHomeParent", "AT_HOME_PARENT").createEnvironmentJsEnumSimpleType();
  public final EnvironmentJsEnumKeyValueType shareholderCommunicationCodes = new EnvironmentJsEnumKeyValueTypeBuilder().setDescAndCode("all", "RECEIVE_ALL").setDescAndCode("proxy", "RECEIVE_PROXY").setDescAndCode("decline", "DECLINE").createEnvironmentJsEnumSimpleType();
  public final EnvironmentJsEnumKeyValueType accountTypes = new EnvironmentJsEnumKeyValueTypeBuilder().setDescAndCode("tfsa", "TFSA").
    setDescAndCode("margin", "MARGIN").setDescAndCode("cash", "CASH").
    setDescAndCode("rrsp", "RRSP").setDescAndCode("marginShort", "MARGIN_SHORT").
    setDescAndCode("lrsp", "LRSP").setDescAndCode("rlsp", "RLSP").
    setDescAndCode("srsp", "SRSP").setDescAndCode("lira", "LIRA").
    setDescAndCode("rrif", "RRIF").setDescAndCode("srif", "SRIF").
    setDescAndCode("lrif", "LRIF").setDescAndCode("lif", "LIF").
    setDescAndCode("prif", "PRIF").setDescAndCode("rlif", "RLIF").
    setDescAndCode("itf", "ITF").setDescAndCode("ustp", "USTP").
    setDescAndCode("dsp", "RDSP").setDescAndCode("esp", "RESP").createEnvironmentJsEnumSimpleType();
  public final EnvironmentJsEnumKeyValueType financialInstitutions = new EnvironmentJsEnumKeyValueTypeBuilder().setDescAndCode("td", "TD").setDescAndCode("bmo", "BMO")
    .setDescAndCode("sb", "SB").setDescAndCode("rbc", "RBC").setDescAndCode("cibc", "CIBC").setDescAndCode("nb", "NB").setDescAndCode("lb", "LB")
    .setDescAndCode("hsbc", "HSBC").setDescAndCode("tb", "TB").setDescAndCode("atb", "ATB").setDescAndCode("as", "AS").setDescAndCode("ccd", "CCD")
    .setDescAndCode("fo", "FO").setDescAndCode("cua", "CUA").setDescAndCode("cum", "CUM").setDescAndCode("cus", "CUS").setDescAndCode("ccub", "CCUB").
      setDescAndCode("ccuo", "CCUO").setDescAndCode("mcu", "MCU").setDescAndCode("pc", "PC").setDescAndCode("other", "OTHER").createEnvironmentJsEnumSimpleType();
  public final EnvironmentJsEnumKeyValueType usTaxIdTypes = new EnvironmentJsEnumKeyValueTypeBuilder().setDescAndCode("ssn", "SSN").setDescAndCode("itin", "ITIN").setDescAndCode("atin", "ATIN").createEnvironmentJsEnumSimpleType();
  public final EnvironmentJsEnumKeyValueType userTypes = new EnvironmentJsEnumKeyValueTypeBuilder().setDescAndCode("newToBank", "N2B").setDescAndCode("newToWealth", "N2W").setDescAndCode("existing", "EW").createEnvironmentJsEnumSimpleType();
  public final EnvironmentJsEnumKeyValueType disclosureDisplayTypes = new EnvironmentJsEnumKeyValueTypeBuilder().setDescAndCode("staticLink", "SL").setDescAndCode("dynamicLink", "DL").setDescAndCode("inline", "INLINE").createEnvironmentJsEnumSimpleType();
  public final EnvironmentJsEnumKeyValueType clarificationTypes = new EnvironmentJsEnumKeyValueTypeBuilder().setDescAndCode("rush", "RUSH").setDescAndCode("phone", "PHONE")
    .setDescAndCode("highRiskOccupation", "HIGH_RISK_OCCUPATION").setDescAndCode("sanctionedcountry", "SANCTIONED_COUNTRY").setDescAndCode("address", "ADDRESS")
    .setDescAndCode("publicEmpoyment", "PUBLIC_EMPLOYMENT").setDescAndCode("crs", "CRS").setDescAndCode("idAddress", "ID_ADDRESS").createEnvironmentJsEnumSimpleType();
  public final EnvironmentJsEnumKeyValueType eligibilityRules = new EnvironmentJsEnumKeyValueTypeBuilder().setDescAndCode("err001", "MARGIN_STUDENT_UNEMPLOYED_NOT_ELIGIBLE")
    .setDescAndCode("err002","MARGIN_SCORE_CHECK").setDescAndCode("err003", "RRSP_OLDER_THAN_71_NOT_ELIGIBLE").setDescAndCode("err004", "DOB_EQUAL_18_PROVINCE_NOT_ELIGIBLE")
    .setDescAndCode("err005", "DOB_LESS_THAN_18").setDescAndCode("err006", "DOB_MISSING").setDescAndCode("err007", "LEGAL_FIRST_LAST_NAME_MISSING")
    .setDescAndCode("err008", "SIN_NUMBER_VIOLATION").setDescAndCode("err009", "LEGAL_ADDRESS_COUNTRY_NOT_CANADA").setDescAndCode("err010", "NO_LEGAL_ADDRESS_AND_MAILING_ADDRESS_COUNTRY_NOT_CANADA")
    .setDescAndCode("err011", "N2W_MCOM_ALERTS_NOT_ELIGIBLE").setDescAndCode("err012", "RR_CODE_AND_HALT_TRADE_NOT_ELIGIBLE").setDescAndCode("err013", "NON_EDITABLE_FIELDS_NOT_ELIGIBLE")
    .setDescAndCode("err014", "ID_PROOFING_COMPLIANCE_NOT_ELIGIBLE").setDescAndCode("err015", "ID_CAPTURE_COMPLIANCE_NOT_ELIGIBLE").setDescAndCode("err016", "CREDENTIAL_SET_UP_NOT_ELIGIBLE").createEnvironmentJsEnumSimpleType();
  public final EnvironmentJsEnumKeyValueType clarifyEmploymentIndustries = new EnvironmentJsEnumKeyValueTypeBuilder().setDescAndCode("govermentDiplomat", "140").setDescAndCode("projectManagement", "240").setDescAndCode("businessManagement", "050").createEnvironmentJsEnumSimpleType();
  public final EnvironmentJsEnumKeyValueType clarifyEmploymentOccupations = new EnvironmentJsEnumKeyValueTypeBuilder().setDescAndCode("civilServant", "140013").setDescAndCode("projectManagement", "240002").setDescAndCode("businessManagement", "050009").createEnvironmentJsEnumSimpleType();

  public final EnvironmentJsEnumKeyValueType investmentTransferType = new EnvironmentJsEnumKeyValueTypeBuilder().setDescAndCode("allInKind", "ALL_IN_KIND").setDescAndCode("allInCash", "ALL_IN_CASH").createEnvironmentJsEnumSimpleType();
  public final EnvironmentJsEnumKeyValueType transferType = new EnvironmentJsEnumKeyValueTypeBuilder().setDescAndCode("investment", "INVESTMENT").setDescAndCode("cash", "CASH").createEnvironmentJsEnumSimpleType();
  public final EnvironmentJsEnumKeyValueType addressTypes = new EnvironmentJsEnumKeyValueTypeBuilder().setDescAndCode("mailing", "MAILING").setDescAndCode("legal", "LEGAL").setDescAndCode("employment", "EMPLOYMENT").createEnvironmentJsEnumSimpleType();
  public final EnvironmentJsEnumKeyValueType channels = new EnvironmentJsEnumKeyValueTypeBuilder().setDescAndCode("branch", "Branch").createEnvironmentJsEnumSimpleType();
  public final EnvironmentJsEnumKeyListType productJurisdictionMap = new EnvironmentJsEnumKeyListTypeBuilder()
    .setDescAndCode("LIF", "AB").setDescAndCode("LIF", "BC").setDescAndCode("LIF", "MB")
    .setDescAndCode("LIF", "NB").setDescAndCode("LIF", "NL").setDescAndCode("LIF", "NS")
    .setDescAndCode("LIF", "ON").setDescAndCode("LIF", "QC").setDescAndCode("LIF", "Federal")
    .setDescAndCode("LRIF", "NL")
    .setDescAndCode("RLIF", "Federal")
    .setDescAndCode("RLSP", "Federal")
    .setDescAndCode("PRIF", "MB").setDescAndCode("PRIF", "SK")
    .setDescAndCode("LIRA", "AB").setDescAndCode("LIRA", "BC").setDescAndCode("LIRA", "MB")
    .setDescAndCode("LIRA", "NB").setDescAndCode("LIRA", "NL").setDescAndCode("LIRA", "NS")
    .setDescAndCode("LIRA", "ON").setDescAndCode("LIRA", "QC").setDescAndCode("LIRA", "SK")
    .createEnvironmentJsEnumSimpleType();

  public final EnvironmentJsEnumKeyValueType profileTypes = new EnvironmentJsEnumKeyValueTypeBuilder()
    .setDescAndCode("primaryApplicant", ProfileType.PRIMARY_APPLICANT.toString())
    .setDescAndCode("coApplicant", ProfileType.CO_APPLICANT.toString())
    .setDescAndCode("relatedParty",ProfileType.THIRD_PARTY.toString())
    .createEnvironmentJsEnumSimpleType();

  public final EnvironmentJsEnumKeyValueType profileRoleTypes = new EnvironmentJsEnumKeyValueTypeBuilder()
    .setDescAndCode("POA", ProfileRoleType.POA.toString())
    .setDescAndCode("GPA", ProfileRoleType.GPA.toString())
    .setDescAndCode("TA",ProfileRoleType.TA.toString())
    .createEnvironmentJsEnumSimpleType();

  public final EnvironmentJsEnumKeyValueType annualPaymentOptions = new EnvironmentJsEnumKeyValueTypeBuilder()
    .setDescAndCode("minimum", "MINIMUM")
    .setDescAndCode("greaterThanMinimum", "GREATERTHANMINIMUM")
    .setDescAndCode("maximum","MAXIMUM")
    .createEnvironmentJsEnumSimpleType();

  public final EnvironmentJsEnumKeyValueType grossOrNetOptions = new EnvironmentJsEnumKeyValueTypeBuilder()
    .setDescAndCode("gross", "GROSS")
    .setDescAndCode("net", "NET")
    .createEnvironmentJsEnumSimpleType();

  public final EnvironmentJsEnumKeyValueType gender = new EnvironmentJsEnumKeyValueTypeBuilder()
    .setDescAndCode("male", "M")
    .setDescAndCode("female", "F")
    .createEnvironmentJsEnumSimpleType();

  public final EnvironmentJsEnumKeyValueType relationshipToBeneficiary = new EnvironmentJsEnumKeyValueTypeBuilder()
    .setDescAndCode("beneficiary", "BENEFICIARY")
    .setDescAndCode("parent", "PARENT").setDescAndCode("family","FAMILY").setDescAndCode("none","NONE")
    .createEnvironmentJsEnumSimpleType();

  public final EnvironmentJsEnumKeyValueType relationshipDetail = new EnvironmentJsEnumKeyValueTypeBuilder()
    .setDescAndCode("guardian", "GUARDIAN")
    .setDescAndCode("tutor", "TUTOR").setDescAndCode("curator","CURATOR").setDescAndCode("other","OTHER")
    .createEnvironmentJsEnumSimpleType();

  public final EnvironmentJsEnumKeyValueType sourceApplicationTypes = new EnvironmentJsEnumKeyValueTypeBuilder()
    .setDescAndCode("WD", "WD").setDescAndCode("C3", "C3")
    .createEnvironmentJsEnumSimpleType();

  public final EnvironmentJsEnumKeyValueType productGroupName = new EnvironmentJsEnumKeyValueTypeBuilder()
    .setDescAndCode("rif", "RIF").setDescAndCode("rsp", "RSP")
    .createEnvironmentJsEnumSimpleType();

  public final EnvironmentJsEnumKeyValueType primaryCaregiverTypes = new EnvironmentJsEnumKeyValueTypeBuilder()
    .setDescAndCode("individual", "INDIVIDUAL")
    .setDescAndCode("agency","AGENCY").setDescAndCode("coapplicant","CO_APPLICANT")
    .setDescAndCode("primaryApplicant","PRIMARY_APPLICANT")
    .createEnvironmentJsEnumSimpleType();

  public final EnvironmentJsEnumKeyValueType espBeneficiaryRelationship = new EnvironmentJsEnumKeyValueTypeBuilder()
    .setDescAndCode("child", "CHILD").setDescAndCode("grandchild","GRANDCHILD")
    .createEnvironmentJsEnumSimpleType();

  public final EnvironmentJsEnumKeyValueType espProvinces = new EnvironmentJsEnumKeyValueTypeBuilder()
    .setDescAndCode("quebec", "QC").setDescAndCode("britishColumbia","BC")
    .setDescAndCode("saskatchewan","SK").createEnvironmentJsEnumSimpleType();

  public final EnvironmentJsEnumKeyValueType espAddressSameAs = new EnvironmentJsEnumKeyValueTypeBuilder()
    .setDescAndCode("subscriber", "PRIMARY_APPLICANT").setDescAndCode("coapplicant","CO_APPLICANT")
    .setDescAndCode("parent","LEGAL_PARENT").setDescAndCode("other","OTHER")
    .createEnvironmentJsEnumSimpleType();

  public final EnvironmentJsEnumKeyValueType sourceOfFundTypes = new EnvironmentJsEnumKeyValueTypeBuilder()
    .setDescAndCode("inheritance", "INHERITANCE").setDescAndCode("childTaxCredit","CHILD_TAX_CREDIT")
    .setDescAndCode("childEmploymentSavings", "CHILD_EMPLOYMENT_SAVINGS").setDescAndCode("formalTrust","FORMAL_TRUST")
    .setDescAndCode("other","OTHER").createEnvironmentJsEnumSimpleType();

  public final EnvironmentJsEnumKeyValueType relatedPartyRelationship = new EnvironmentJsEnumKeyValueTypeBuilder()
    .setDescAndCode("parent", "PARENT")
    .setDescAndCode("spouse", "SPOUSE")
    .setDescAndCode("child", "CHILD")
    .setDescAndCode("grandchild", "GRANDCHILD")
    .setDescAndCode("sibling", "SIBLING")
    .setDescAndCode("other", "OTHER").createEnvironmentJsEnumSimpleType();

}
