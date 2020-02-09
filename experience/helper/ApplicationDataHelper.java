package com.td.dcts.eso.experience.helper;

import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.environmentjs.facade.ReferenceDataEnum;
import com.td.dcts.eso.experience.environmentjs.facade.ReferenceDataHelper;
import com.td.dcts.eso.experience.environmentjs.model.JavaScriptEnumValues;
import com.td.dcts.eso.experience.model.response.*;
import com.td.eso.rest.response.model.LookupModel;
import com.td.eso.rest.response.model.LookupModelFinancialInstitutions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class ApplicationDataHelper {

  private ReferenceDataHelper referenceDataHelper;

  @Autowired
  public ApplicationDataHelper(ReferenceDataHelper referenceDataFacade) {
    this.referenceDataHelper = referenceDataFacade;
  }

  public void injectReferenceDataModel(WealthClientMasterInfo wealthClientMasterInfo) throws ApiException {
    JavaScriptEnumValues javaScriptEnumValues = new JavaScriptEnumValues();

    AboutYou aboutYou = wealthClientMasterInfo.getAboutyou();
    PersonalInfo personalInfo = aboutYou.getPersonalInfo();

    personalInfo.setTitleModel(lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.TITLE), personalInfo.getTitle()));
    personalInfo.setMaritalStatusModel(lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.MARITALSTATUS), personalInfo.getMaritalStatus()));
    personalInfo.setSuffixModel(lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.SUFFIX), personalInfo.getSuffix()));
    personalInfo.setCitizenshipModel(lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.COUNTRY), personalInfo.getCitizenship()));
    personalInfo.setCitizenship2Model(lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.COUNTRY), personalInfo.getCitizenship2()));

    if (personalInfo.getAdditionalTaxJurisdictions() != null && !personalInfo.getAdditionalTaxJurisdictions().isEmpty()){
      for (TaxJurisdiction taxJurisdiction : personalInfo.getAdditionalTaxJurisdictions()) {
        taxJurisdiction.setTaxJurisdictionModel(lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.COUNTRY), taxJurisdiction.getTaxJurisdiction()));
      }
    }

    if(personalInfo.getUsTaxJurisdiction() != null && personalInfo.getUsTaxJurisdiction().getTaxJurisdiction() != null) {
        personalInfo.getUsTaxJurisdiction().setTaxJurisdictionModel(lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.COUNTRY), personalInfo.getUsTaxJurisdiction().getTaxJurisdiction()));
    }

    ContactInfo contactInfo = wealthClientMasterInfo.getAboutyou().getContactInfo();
    Address employmentAddress = aboutYou.getEmploymentInfo().getAddress();
    Address legalAddress = contactInfo.getLegalAddress();
    Address mailingAddress = contactInfo.getMailingAddress();
    EmploymentInfo employmentInfo= aboutYou.getEmploymentInfo();
    contactInfo.setCountryCodeModel(lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.PHONECOUNTRYCODE), contactInfo.getCountryCode()));
    if (contactInfo.getOtherPhones() != null && contactInfo.getOtherPhones().size() > 0) {
      for (Phone phone : contactInfo.getOtherPhones()) {
        phone.setCountryCodeModel(lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.PHONECOUNTRYCODE), phone.getCountryCode()));
      }
    }

  if(employmentInfo.getEmployerPhoneCountryCode()!=null) {
    employmentInfo.setEmployerPhoneCountryCodeModel(lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.PHONECOUNTRYCODE), employmentInfo.getEmployerPhoneCountryCode()));
  }

    legalAddress.setCountryModel(lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.COUNTRY), legalAddress.getCountry()));
    if (javaScriptEnumValues.countryCodes.getData().get("canada").equalsIgnoreCase(legalAddress.getCountryModel() != null ? legalAddress.getCountryModel().getCode() : null)) {
      legalAddress.setProvinceStateModel(lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.CANADIANPROVINCES), legalAddress.getProvince()));
    } else if (javaScriptEnumValues.countryCodes.getData().get("usa").equalsIgnoreCase(legalAddress.getCountryModel() != null ? legalAddress.getCountryModel().getCode() : null)) {
      legalAddress.setProvinceStateModel(lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.USASTATES), legalAddress.getProvince()));
    }
    legalAddress.setUnitTypeModel(lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.UNITTYPES), legalAddress.getUnitType()));

    mailingAddress.setCountryModel(lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.COUNTRY), mailingAddress.getCountry()));
    if (javaScriptEnumValues.countryCodes.getData().get("canada").equalsIgnoreCase(legalAddress.getCountryModel() != null ? legalAddress.getCountryModel().getCode() : null)) {
      mailingAddress.setProvinceStateModel(lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.CANADIANPROVINCES), mailingAddress.getProvince()));
    } else if (javaScriptEnumValues.countryCodes.getData().get("usa").equalsIgnoreCase(legalAddress.getCountryModel() != null ? legalAddress.getCountryModel().getCode() : null)) {
      mailingAddress.setProvinceStateModel(lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.USASTATES), mailingAddress.getProvince()));
    }
    mailingAddress.setUnitTypeModel(lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.UNITTYPES), mailingAddress.getUnitType()));

    employmentAddress.setCountryModel(lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.COUNTRY), employmentAddress.getCountry()));
    if (javaScriptEnumValues.countryCodes.getData().get("canada").equalsIgnoreCase(legalAddress.getCountryModel() != null ? legalAddress.getCountryModel().getCode() : null)) {
      employmentAddress.setProvinceStateModel(lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.CANADIANPROVINCES), employmentAddress.getProvince()));
    } else if (javaScriptEnumValues.countryCodes.getData().get("usa").equalsIgnoreCase(legalAddress.getCountryModel() != null ? legalAddress.getCountryModel().getCode() : null)) {
      employmentAddress.setProvinceStateModel(lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.USASTATES), employmentAddress.getProvince()));
    }
    employmentAddress.setUnitTypeModel(lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.UNITTYPES), employmentAddress.getUnitType()));

    aboutYou.getEmploymentInfo().setEmploymentStatusModel((lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.EMPLOYMENTSTATUS), aboutYou.getEmploymentInfo().getStatusCd())));
    aboutYou.getEmploymentInfo().setIndustryModel((lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.INDUSTRY), aboutYou.getEmploymentInfo().getIndustry())));
    aboutYou.getEmploymentInfo().setOccupationModel((lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.OCCUPATIONS), aboutYou.getEmploymentInfo().getOccupation())));

    aboutYou.getPartnerInfo().getEmploymentInfo().setEmploymentStatusModel((lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.EMPLOYMENTSTATUS), aboutYou.getPartnerInfo().getEmploymentInfo().getStatusCd())));
    aboutYou.getPartnerInfo().getEmploymentInfo().setIndustryModel((lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.INDUSTRY), aboutYou.getPartnerInfo().getEmploymentInfo().getIndustry())));
    aboutYou.getPartnerInfo().getEmploymentInfo().setOccupationModel((lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.OCCUPATIONS), aboutYou.getPartnerInfo().getEmploymentInfo().getOccupation())));


    for (FinancialInstitution financialInstitution : aboutYou.getFinancialInfo().getFinancialInstitutions()) {
      financialInstitution.setFinancialInstitutionModel(lookUpReferenceDataFinancialInst(referenceDataHelper.getReferenceData(ReferenceDataEnum.FINANCIALINSTITUTIONS), financialInstitution.getFinancialInstitution()));
    }

    if (aboutYou.getAccountDetails().getPurpose() != null && !aboutYou.getAccountDetails().getPurpose().isEmpty()) {
      for (AccountPurpose accountPurpose : aboutYou.getAccountDetails().getPurpose()) {
        accountPurpose.setPrimaryPurposeModel(lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.ACCOUNTPURPOSES), accountPurpose.getPrimaryPurpose()));

        if(accountPurpose.getPaymentInstructionInfo()!=null){
          if(accountPurpose.getPaymentInstructionInfo().getFinancialInstitution()!=null)
          {
            accountPurpose.getPaymentInstructionInfo().getFinancialInstitution().setFinancialInstitutionModel(lookUpReferenceDataFinancialInst(referenceDataHelper.getReferenceData(ReferenceDataEnum.FINANCIALINSTITUTIONS), accountPurpose.getPaymentInstructionInfo().getFinancialInstitution().getFinancialInstitution()));
          }
        }

        if(accountPurpose.getSweepAccountInfo() != null) {
          if(accountPurpose.getSweepAccountInfo().getCanadianFinancialInstitution()!=null) {
            if (accountPurpose.getSweepAccountInfo().getCanadianFinancialInstitution().getFinancialInstitution() != null) {
              String cdnFinancialInstitution = accountPurpose.getSweepAccountInfo().getCanadianFinancialInstitution().getFinancialInstitution();
              accountPurpose.getSweepAccountInfo().getCanadianFinancialInstitution().setFinancialInstitutionModel(lookUpReferenceDataFinancialInst(referenceDataHelper.getReferenceData(ReferenceDataEnum.FINANCIALINSTITUTIONS), cdnFinancialInstitution));
            }
          }
          if(accountPurpose.getSweepAccountInfo().getUsFinancialInstitution()!=null) {
            if (accountPurpose.getSweepAccountInfo().getUsFinancialInstitution().getFinancialInstitution() != null) {
              String usFinancialInstitution = accountPurpose.getSweepAccountInfo().getUsFinancialInstitution().getFinancialInstitution();
              accountPurpose.getSweepAccountInfo().getUsFinancialInstitution().setFinancialInstitutionModel(lookUpReferenceDataFinancialInst(referenceDataHelper.getReferenceData(ReferenceDataEnum.FINANCIALINSTITUTIONS), usFinancialInstitution));
            }
          }
        }
      }
    }

    if (!CollectionUtils.isEmpty(wealthClientMasterInfo.getOtherEntities())) {
      for (Entity entity: wealthClientMasterInfo.getOtherEntities()) {
        if (entity.getRelationship() != null) {
          if (EntityType.PERSONAL.equals(entity.getEntityType())) {
            entity.setPersonalRelationshipModel(lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.PERSONALRELATIONSHIP), entity.getRelationship()));
          } else if (EntityType.BUSINESS.equals(entity.getEntityType())) {
            entity.setBusinessRelationshipModel(lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.BUSINESSRELATIONSHIP), entity.getRelationship()));
          }
        }
        entity.setTitleModel(lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.TITLE), entity.getTitle()));
        entity.setSuffixModel(lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.SUFFIX), entity.getSuffix()));
        Address address = entity.getAddress();
        if (address != null) {

          address.setCountryModel(lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.PHONECOUNTRYCODE), address.getCountry()));
          if (javaScriptEnumValues.countryCodes.getData().get("canada").equalsIgnoreCase(address.getCountryModel() != null ? address.getCountryModel().getCode() : null)) {
            address.setProvinceStateModel(lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.CANADIANPROVINCES), address.getProvince()));
          } else if (javaScriptEnumValues.countryCodes.getData().get("usa").equalsIgnoreCase(address.getCountryModel() != null ? address.getCountryModel().getCode() : null)) {
            address.setProvinceStateModel(lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.USASTATES), address.getProvince()));
          }
          address.setUnitTypeModel(lookUpReferenceData(referenceDataHelper.getReferenceData(ReferenceDataEnum.UNITTYPES), address.getUnitType()));
        }
      }
    }
  }

  private LookupModel lookUpReferenceData(List<? extends LookupModel> referenceData, String lookupCode) throws ApiException {
    for (LookupModel referenceDataItem : referenceData) {
      if (referenceDataItem.getCode().equalsIgnoreCase(lookupCode)) {
        return referenceDataItem;
      }
      ;
    }
    return null;
  }

  private LookupModelFinancialInstitutions lookUpReferenceDataFinancialInst(List<? extends LookupModel> referenceData, String lookupCode) throws ApiException {
    for (LookupModel referenceDataItem : referenceData) {
      if (referenceDataItem.getCode().equalsIgnoreCase(lookupCode)) {
        return (LookupModelFinancialInstitutions) referenceDataItem;
      }
      ;
    }
    return null;
  }
  public WealthClientMasterInfo deattachConsent(WealthClientMasterInfo wcm) {
      if (ExperienceConstants.CHANNEL_BRANCH.equals(wcm.getUserIdentity().getChannel()) && wcm.getAboutyou().getBranchTcConsentInfo() != null) {
//        sessionUtil.setToSession(request, "BranchTcConsentInfo", wcm.getAboutyou().getBranchTcConsentInfo(), true);

        wcm.getAboutyou().setBranchTcConsentInfo(null);
      }
      if (ExperienceConstants.CHANNEL_BRANCH.equals(wcm.getUserIdentity().getChannel()) && wcm.getAboutyou().getContractConsentInfo() != null) {
//        sessionUtil.setToSession(request, "ContractConsentInfo", wcm.getAboutyou().getContractConsentInfo(), true);
        wcm.getAboutyou().setContractConsentInfo(null);
      }
      if (ExperienceConstants.CHANNEL_BRANCH.equals(wcm.getUserIdentity().getChannel()) && wcm.getAboutyou().getCredentialConsentInfo() != null) {
        wcm.getAboutyou().setCredentialConsentInfo(null);
      }
      if (ExperienceConstants.CHANNEL_BRANCH.equals(wcm.getUserIdentity().getChannel()) && wcm.getAboutyou().getDigitalSignatureConsentInfo() != null) {
        wcm.getAboutyou().setDigitalSignatureConsentInfo(null);
      }
      if (ExperienceConstants.CHANNEL_BRANCH.equals(wcm.getUserIdentity().getChannel()) && wcm.getAboutyou().getElectronicAccountConsentInfo() != null) {
        wcm.getAboutyou().setElectronicAccountConsentInfo(null);
      }
      if (ExperienceConstants.CHANNEL_BRANCH.equals(wcm.getUserIdentity().getChannel()) && wcm.getAboutyou().getImportantConsentInfo() != null) {
        wcm.getAboutyou().setImportantConsentInfo(null);
      }
      if (ExperienceConstants.CHANNEL_BRANCH.equals(wcm.getUserIdentity().getChannel()) && wcm.getAboutyou().getLoginConsentInfo() != null) {
        wcm.getAboutyou().setLoginConsentInfo(null);
      }
      if (ExperienceConstants.CHANNEL_BRANCH.equals(wcm.getUserIdentity().getChannel()) && wcm.getAboutyou().getMarginConsentInfo() != null) {
        wcm.getAboutyou().setMarginConsentInfo(null);
      }
      if (ExperienceConstants.CHANNEL_BRANCH.equals(wcm.getUserIdentity().getChannel()) && wcm.getAboutyou().getTcConsentInfo() != null) {
        wcm.getAboutyou().setTcConsentInfo(null);
      }
      if (ExperienceConstants.CHANNEL_BRANCH.equals(wcm.getUserIdentity().getChannel()) && wcm.getAboutyou().getTransferTCConsentInfo() != null) {
        wcm.getAboutyou().setTransferTCConsentInfo(null);
      }
      if (ExperienceConstants.CHANNEL_BRANCH.equals(wcm.getUserIdentity().getChannel()) && wcm.getAboutyou().getTransferTypeConsentInfo() != null) {
        wcm.getAboutyou().setTransferTypeConsentInfo(null);
      }
    return wcm;
  }
}
