package com.td.dcts.eso.experience.facade;


import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.events.createapplication.response.SubApplication;
import com.td.dcts.eso.experience.GetStartedController;
import com.td.dcts.eso.experience.environmentjs.handler.ReferenceDataHandler;
import com.td.dcts.eso.experience.handler.GetStartedHandler;
import com.td.dcts.eso.experience.model.associatesapi.AssociateOrganizations;
import com.td.dcts.eso.experience.model.associatesapi.Associates;
import com.td.dcts.eso.experience.model.response.AboutYou;
import com.td.dcts.eso.experience.model.response.ApplicationInfo;
import com.td.dcts.eso.experience.model.response.ContactInfo;
import com.td.dcts.eso.experience.model.response.EmploymentInfo;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.dcts.eso.experience.model.response.PartnerInfo;
import com.td.dcts.eso.experience.model.response.PersonalInfo;
import com.td.dcts.eso.experience.model.response.ProfileType;
import com.td.dcts.eso.experience.model.response.UserIdentity;
import com.td.dcts.eso.experience.model.response.WealthClientMasterInfo;
import com.td.dcts.eso.experience.util.ValidationUtil;
import com.td.dcts.eso.session.model.EsoJsonData;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.td.dcts.eso.experience.constants.ExperienceConstants.N2B;
import static com.td.dcts.eso.experience.constants.ExperienceConstants.STUDENT;


@Component
public class GetStartedFacade {

  private static final XLogger LOGGER = XLoggerFactory.getXLogger(GetStartedController.class);


  @Autowired
  private GetStartedHandler getStartedHandler;

  @Autowired
  ValidationUtil validationUtil;

  @Autowired
  ReferenceDataHandler referenceDataHandler;

  @Value("${resturl.getStarted.associates.retrieve}")
  private String getStartedAssociateRetrieveURL;

  public WealthClientMasterInfo getWealthClientMasterInfoResponse(MetaData metaData, HttpHeaders httpHeaders,
                                                                  String getStartedProfileRetrieveURL, String prefillType) throws ApiException {

    getStartedProfileRetrieveURL = getStartedProfileRetrieveURL + "?prefillType=" + prefillType;
    WealthClientMasterInfo wcm = getStartedHandler.getResponse(metaData, httpHeaders, getStartedProfileRetrieveURL);

    createPrimaryApplicant(wcm);


    //HACK: START -----------------------------------------
    // ToDo Code below is added only for temporary testing until we figure out how to get the real master client ID
    //Karthik is working on stories to find a permanent fix for the issue
    if(wcm.getUserIdentity().getCmMasterClientId()==null)
    {
      if(wcm.getAgreements()!= null && wcm.getAgreements().size()>0)
      {
        wcm.getUserIdentity().setCmMasterClientId(wcm.getAgreements().get(0).getAccount().getAccountNum().substring(0,6));
        wcm.setCmClientID(wcm.getUserIdentity().getCmMasterClientId());
      }
    }
    //HACK: END -----------------------------------------

    cleanUpClientProfile(wcm);
    UserIdentity userIdentityFromBackend = wcm.getUserIdentity();

    //Backend determines if the user is an existing Wealth User or New to Wealth(N2W).
    // If user has entered login credentials, metaData.getFlowId() will store "EW" but
    //after checking with Enrollment, backend will determine whether the user is EW or N2W.
    //If the backend determines that the UserType to N2W, then backend will change userIdentityFromBackend.getUserType to N2W.
    //If the backend changes userType then we also update it in session
    if(userIdentityFromBackend.getUserType() == null) {
      UserIdentity userIdentity;
      if(wcm.getUserIdentity()==null) {
        userIdentity = new UserIdentity();
      }
      else
      {
        userIdentity=wcm.getUserIdentity();
      }
      userIdentity.setConnectId(metaData.getConnectId());
      if(metaData.getFlowId() != null) {
        userIdentity.setUserType(metaData.getFlowId());
      }
      //Also setup the ConnectId in a new userIdentity object in AboutYou.userIdentity
      UserIdentity aboutYouUserIdentity = new UserIdentity();
      aboutYouUserIdentity.setConnectId(metaData.getConnectId());
      wcm.getAboutyou().setUserIdentity(aboutYouUserIdentity);
      wcm.setUserIdentity(userIdentity);
      wcm.setApplicationInfo(mapApplicationInfo(metaData));
    }

    return wcm;
  }

  public void createPrimaryApplicant(WealthClientMasterInfo wcm){
    //Backend will send only one Primary Applicant Profile.  This profile has to be added to the list of profiles too.
    List<AboutYou> aboutAllApplicantsAndParties = new ArrayList<>();
    wcm.getAboutyou().setProfileId("0"); //Primary applicant
    wcm.getAboutyou().setProfileType(ProfileType.PRIMARY_APPLICANT); //Primary applicant
    aboutAllApplicantsAndParties.add(wcm.getAboutyou());
    wcm.setAboutAllApplicantsAndParties(aboutAllApplicantsAndParties);

  }

  public AssociateOrganizations getAssociateOrganizationsResponse(MetaData metaData, HttpHeaders httpHeaders,
                                                                  String idTypeCd, String id) throws ApiException {

    String getStartedAssociateRetrieveURLToGo = getStartedAssociateRetrieveURL + idTypeCd + "/" + id + "/internalorganizations";
    AssociateOrganizations organizations = getStartedHandler.getRelatedInternalOrganizationsResponse(metaData, httpHeaders, getStartedAssociateRetrieveURLToGo);

    return organizations;
  }

  public Associates getAssociateResponse(MetaData metaData, HttpHeaders httpHeaders,
                                         String idTypeCd, String id) throws ApiException {

    String getStartedAssociateRetrieveURLToGo = getStartedAssociateRetrieveURL + idTypeCd + "/" + id;
    Associates associates = getStartedHandler.getAssociateResponse(metaData, httpHeaders, getStartedAssociateRetrieveURLToGo);

    return associates;
  }
  private ApplicationInfo mapApplicationInfo(MetaData metaData) {
    ApplicationInfo applicationInfo = new ApplicationInfo();
    applicationInfo.setApplicationId(metaData.getApplicationId().toString());
    applicationInfo.setApplicationStartDate(new Date().toString());

    SubApplication subApplicationInfo = new SubApplication();
    List<SubApplication> subApplications = new ArrayList<>();
    if(metaData.getSubApplicationList().size() > 0) {
      subApplicationInfo.setSubApplicationId(metaData.getSubApplicationList().get(0).getSubApplicationId());
    }
    subApplications.add(subApplicationInfo);
    applicationInfo.setSubApplicationList(subApplications);
    return applicationInfo;
  }




  public WealthClientMasterInfo getN2BData(MetaData metaData, EsoJsonData esoSession, HttpHeaders httpHeaders) throws ApiException, IOException {

    WealthClientMasterInfo wcm = new WealthClientMasterInfo();

    wcm.getAboutyou().getPersonalInfo().setFirstName((String) esoSession.get("firstName"));
    wcm.getAboutyou().getPersonalInfo().setLastName((String) esoSession.get("lastName"));
    wcm.getAboutyou().getContactInfo().setEmail((String) esoSession.get("email"));
    wcm.getAboutyou().getContactInfo().setPhoneNumber((String) esoSession.get("phone"));

    //populte user type (EW, N2B, N2W, etc)
    UserIdentity userIdentity = new UserIdentity();
    userIdentity.setUserType(N2B);
    wcm.setUserIdentity(userIdentity);
    wcm.setApplicationInfo(mapApplicationInfo(metaData));
    return wcm;
  }


  public void cleanUpClientProfile(WealthClientMasterInfo wcm) {

    AboutYou aboutYou = wcm.getAboutyou();
    if(aboutYou == null) {
      return;
    }

    PersonalInfo pi = aboutYou.getPersonalInfo();
    if(!pi.isEmpty()) {
      try {
        validationUtil.validateDataAgainstDropdown(pi, true);
      } catch(ApiException e) {
        LOGGER.warn("Error Cleanup PersonalInfo", e);
      }
    }

    ContactInfo ci = aboutYou.getContactInfo();
    if(!ci.isEmpty()) {
      try {
        validationUtil.validateDataAgainstDropdown(ci, true);
      } catch(ApiException e) {
        LOGGER.warn("Error cleanUp ContactInfo", e);
      }
    }

    EmploymentInfo ei = aboutYou.getEmploymentInfo();
    if(!ei.isEmpty()) {
      try {
        if(ei.getStatusCd()!=null) {
          if (STUDENT.equalsIgnoreCase(ei.getStatusCd())) {
            ei.setIndustry("");
            ei.setOccupation("");
          }
        }
        validationUtil.validateDataAgainstDropdown(ei, true);
      } catch(ApiException e) {
        LOGGER.warn("Error cleanUp EmploymentInfo ", e);
      }
    }

    PartnerInfo pti = aboutYou.getPartnerInfo();
    if(!pti.isEmpty()) {
      EmploymentInfo pei = pti.getEmploymentInfo();
      if(!pei.isEmpty()) {
        try {
          validationUtil.validateDataAgainstDropdown(pei, true);
        } catch(ApiException e) {
          LOGGER.warn("Error cleanUp Partner EmploymentInfo", e);
        }
      }
    }
  }
}
