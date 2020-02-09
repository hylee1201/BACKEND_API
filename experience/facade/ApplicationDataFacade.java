package com.td.dcts.eso.experience.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.events.createevent.request.EsoCreateEventRequest;
import com.td.dcts.eso.events.createevent.request.Event;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.dao.CustomerDAO;
import com.td.dcts.eso.experience.environmentjs.facade.ReferenceDataEnum;
import com.td.dcts.eso.experience.handler.AccountDetailsHandler;
import com.td.dcts.eso.experience.handler.ApplicationDataHandler;
import com.td.dcts.eso.experience.handler.ConsentHandler;
import com.td.dcts.eso.experience.handler.IdentityManagementHandler;
import com.td.dcts.eso.experience.helper.ApplicationDataHelper;
import com.td.dcts.eso.experience.helper.DisclosureHelper;
import com.td.dcts.eso.experience.helper.EventHelper;
import com.td.dcts.eso.experience.helper.LeftNavHelper;
import com.td.dcts.eso.experience.model.identity.AlternateKey;
import com.td.dcts.eso.experience.model.identity.Credential;
import com.td.dcts.eso.experience.model.identity.EmailAddress;
import com.td.dcts.eso.experience.model.identity.Identity;
import com.td.dcts.eso.experience.model.identity.Locator;
import com.td.dcts.eso.experience.model.identity.UserInfo;
import com.td.dcts.eso.experience.model.identity.UserNamePassword;
import com.td.dcts.eso.experience.model.response.*;
import com.td.dcts.eso.experience.util.CommonUtil;
import com.td.dcts.eso.experience.util.ExceptionUtil;
import com.td.dcts.eso.experience.util.SessionUtil;
import com.td.dcts.eso.experience.util.ValidationUtil;
import com.td.dcts.eso.session.model.EsoJsonData;
import com.td.eso.constants.LookupConstants;
import com.td.eso.constants.WealthConstants;
import com.td.eso.rest.response.model.LookupModel;
import com.td.eso.util.FeatureToggle;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.owasp.encoder.Encode;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.td.dcts.eso.experience.constants.ExperienceConstants.BRANCH_TC_CONSENT_STAGE;
import static com.td.dcts.eso.experience.constants.ExperienceConstants.CHANNEL_BRANCH;
import static com.td.dcts.eso.experience.constants.ExperienceConstants.CHANNEL_SELF_SERVE_WEB;
import static com.td.dcts.eso.experience.constants.ExperienceConstants.CREDENTIALS_SETUP_STAGE;
import static com.td.dcts.eso.experience.constants.ExperienceConstants.IMPORTANT_CONSENT_STAGE;
import static com.td.dcts.eso.experience.constants.ExperienceConstants.MARGIN_BRANCH_CHECK_STAGE;
import static com.td.dcts.eso.experience.constants.ExperienceConstants.OTHER;
import static com.td.dcts.eso.experience.constants.ExperienceConstants.PRODUCT_CASH;
import static com.td.dcts.eso.experience.constants.ExperienceConstants.PRODUCT_MARGIN;
import static com.td.dcts.eso.experience.util.CommonUtil.updateAllApplicationArray;
import static com.td.eso.constants.WealthConstants.INFORMAL_TRUST_PRODUCT;
import static com.td.eso.constants.WealthConstants.ITF_PRODUCT;
import static com.td.eso.constants.WealthConstants.USTP_PRODUCT;
import static org.apache.commons.lang.StringEscapeUtils.unescapeJava;

@Component
public class ApplicationDataFacade {

  @Autowired
  private DisclosureHelper disclosureHelper;

  @Autowired
  private ApplicationDataHelper applicationDataHelper;

  @Autowired
  private ApplicationDataHandler applicationDataHandler;

  @Autowired
  private AccountDetailsHandler accountDetailsHandler;

  @Autowired
  private IdentityManagementHandler identityManagementHandler;

  @Autowired
  private ConsentHandler consentHandler;



  @Autowired
  private CustomerDAO customerDAO;

  @Autowired
  private SessionUtil sessionUtil;

  @Value("${about_you_exclude_country_code}")
  private String[] excludeCountryCodeList;

  @Autowired
  private ValidationUtil validationUtil;

  @Autowired
  private PromoCodeFacade promoCodeFacade;

  @Autowired
  EventHelper eventHelper;


  @Value("${resturl.about.you.profile.update}")
  private String updateAboutYouURL;
  private final static String relSpouse = "SPOUSE";
  private final static String relSelf = "SELF";
  private final static String relOther = "OTHER";
  private static final String IDENTITY_MAP = "identityMap";
  private final static String TDBFG = "TDBFG";
  private final static String IDENTITY_SUCCESS = "Success";
  private final static String REVIEW_STATE = "review";
  private final static String FINANCIAL_INFO_STATE = "financialInfo";
  private final static String MARGIN_ELIGIBILITY_ERROR_CODE = "err002";
  private final static String ENTITY_TYPE_BUSINESS = "BUSINESS";
  private final static String ENTITY_TYPE_PERSONAL = "PERSONAL";
  private ObjectMapper objectMapper = new ObjectMapper();
  static final String SESSION_Id = "sessionId";

  private static final XLogger LOGGER = XLoggerFactory.getXLogger(AboutYouFacade.class);


  public WealthClientMasterInfo getAccountsAndWealthClientMasterInfo(HttpServletRequest httpServletRequest, String wealthJSONData, HttpHeaders httpHeaders, MetaData metaData) throws ApiException, IOException {
    WealthClientMasterInfo wealthCMInfo = null;
    try {
      wealthCMInfo = customerDAO.getWealthClientMasterInfo(wealthJSONData);
      wealthCMInfo.getAboutyou().setAccountDetails(addAccount(wealthCMInfo.getProducts(), wealthCMInfo.getAboutyou().getAccountDetails()));
      sessionUtil.setDraftClientProfile(httpServletRequest, wealthCMInfo);
    } catch(IOException e) {
      LOGGER.error("getWealthClientMasterInfo is failed", e);
      throw new ApiException(ExceptionUtil.buildServerErrorStatus());
    }
    return wealthCMInfo;
  }

  public WealthClientMasterInfo runReviewValidations(MetaData metaData, WealthClientMasterInfo wcm, HttpHeaders httpHeaders) throws ApiException {
    try {
      LeftNavHelper leftNavHelper = new LeftNavHelper();
      leftNavHelper.updateLeftNav(wcm, REVIEW_STATE, true);

      ResponseEntity<String> responseEntity = applicationDataHandler.getApplicationResponse(metaData, wcm, httpHeaders, updateAboutYouURL + "/" + WealthConstants.REVIEW);
      if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
        if (responseEntity.getBody() != null) {
          try {
            EligibilityStatus eligibilityStatus = objectMapper.readValue((String) responseEntity.getBody().toString(), EligibilityStatus.class);
            if (eligibilityStatus.getSelectionEligible() == false) {
              leftNavHelper.updateLeftNav(wcm, REVIEW_STATE, false);
              for (String errCode: eligibilityStatus.getFailedEligibilityRules() ) {
                if(MARGIN_ELIGIBILITY_ERROR_CODE.equals(errCode))
                {
                  leftNavHelper.updateLeftNav(wcm, FINANCIAL_INFO_STATE, false);
                }
              }
            }
          } catch (Exception exp) {
            LOGGER.exit(responseEntity.getStatusCode());
            throw new ApiException(ExceptionUtil.buildErrorStatusFromApiErrorStatus(responseEntity));
          }
        }
      }
      wcm = leftNavHelper.updateForReview(wcm);
      return wcm;

    } catch (IOException e) {
      LOGGER.error("runReviewValidations failed", e);
      throw new ApiException(ExceptionUtil.buildServerErrorStatus());
    }

  }

  public WealthClientMasterInfo getWealthClientMasterInfo(String wealthJSONData) throws ApiException {
    WealthClientMasterInfo wealthCMInfo = null;
    try {
      wealthCMInfo = customerDAO.getWealthClientMasterInfo(wealthJSONData);
      //Code here to Encode Clarifications to Base64
//      encodeClarificationsToBase64(wealthCMInfo, "decode");

    } catch(IOException e) {
      LOGGER.error("getWealthClientMasterInfo is failed", e);
      throw new ApiException(ExceptionUtil.buildServerErrorStatus());
    }
    return wealthCMInfo;
  }

  public void recordUpdateApplicationEvent(MetaData metaData, WealthClientMasterInfo draftWcm, HttpHeaders httpHeaders) throws  ApiException{

    ResponseEntity<String> responseEntity = applicationDataHandler.getApplicationResponse(metaData, draftWcm, httpHeaders, updateAboutYouURL + "/" + WealthConstants.ABOUT_YOU_PERSONAL_INFO);

  }


  public void updateForUserIdentity(WealthClientMasterInfo wcm){
    if(wcm!=null && wcm.getAboutAllApplicantsAndParties()!=null) {
      for (AboutYou aboutYou :wcm.getAboutAllApplicantsAndParties()) {
        if(aboutYou.getUserIdentity()==null && aboutYou.getProfileType() != null && !WealthConstants.THIRD_PARTY.equals(aboutYou.getProfileType().toString().toLowerCase())) {
          UserIdentity userIdentity = new UserIdentity();
          aboutYou.setUserIdentity(userIdentity);
        }
      }
    }
  }


  public IdentityManagementResponse setupIdentity(HttpServletRequest httpServletRequest, HttpHeaders httpHeaders, MetaData metaData, Map<String, String> inputData) throws ApiException, IOException {
    LOGGER.debug("setupIdentity: called");

    WealthClientMasterInfo draftWcm = customerDAO.getWealthClientMasterInfo(sessionUtil.getDraftClientProfile(httpServletRequest));
    LOGGER.debug("setupIdentity: WealthModel Received");
    Identity identity = new Identity();
    AlternateKey alternateKey = new AlternateKey();
    Credential credential = new Credential();
    UserNamePassword userNamePassword = new UserNamePassword();
    UserInfo userInfo = new UserInfo();
    Locator locator = new Locator();
    EmailAddress emailAddress = new EmailAddress();
    List<Locator> locators = new ArrayList<>();

    String aliasName = (unescapeJava((inputData.get("aliasName"))));
    String email = (unescapeJava((inputData.get("email"))));
    String profileId = (unescapeJava((inputData.get("profileId"))));
    String pwd = (unescapeJava((inputData.get("pwd"))));
    AboutYou  aboutYou = CommonUtil.getProfile(draftWcm, profileId );
    draftWcm.setAboutyou(aboutYou);

    alternateKey.setAliasName(aliasName);
    alternateKey.setTypeCd(TDBFG);
    identity.setAlternateKey(alternateKey);
    userNamePassword.setPassword(pwd);
    userNamePassword.setExpireInd(false);
    userNamePassword.setStrongPasswordInd(true);
    credential.setUserNamePassword(userNamePassword);
    identity.setCredential(credential);
    if(aboutYou != null) {
          emailAddress.setText(email);
          locator.setEmailAddress(emailAddress);

          //Create Identity service only takes in maximum 19 characters for first name and last name
          String firstName = aboutYou.getPersonalInfo().getFirstName();
          String lastName = aboutYou.getPersonalInfo().getLastName();
          firstName = firstName.length() > ExperienceConstants.MAX_CREATE_IM_NAME_LENGTH ? firstName.substring(0, ExperienceConstants.MAX_CREATE_IM_NAME_LENGTH) : firstName;
          lastName = lastName.length() > ExperienceConstants.MAX_CREATE_IM_NAME_LENGTH ? lastName.substring(0, ExperienceConstants.MAX_CREATE_IM_NAME_LENGTH) : lastName;

          userInfo.setFirstName(firstName);
          userInfo.setLastName(lastName);
          locators.add(locator);
          userInfo.setLocator(locators);
          identity.setUserInfo(userInfo);
        }


    EsoJsonData esoJsonData = sessionUtil.getSessionData(httpServletRequest);
    String clientIPAddress = httpServletRequest.getRemoteAddr();
    String locale = (String) esoJsonData.get(SessionUtil.SESSION_KEY_LOCALE);
    String sessionId = (String) esoJsonData.get(SESSION_Id);
    httpHeaders.add(SESSION_Id, sessionId);
    httpHeaders.add(ExperienceConstants.HTTP_HEADER_CLIENT_IP, clientIPAddress);
    httpHeaders.add(ExperienceConstants.HTTP_HEADER_LOCALE, locale);


    IdentityManagementResponse connectId = identityManagementHandler.setupIdentity(httpHeaders, metaData, identity, draftWcm);
    LOGGER.debug("setupIdentity: Handler returned connectId");
    if(connectId.getResponseCode().equals(IDENTITY_SUCCESS)) {
      UserIdentity existingUserIdentity = null;
      if(WealthConstants.PRIMARY_APPLICANT.equals(draftWcm.getAboutyou().getProfileType().toString().toLowerCase()))
      {
        //Only update the main object
        existingUserIdentity = draftWcm.getUserIdentity();
        existingUserIdentity.setConnectId(connectId.getConnectId());
        existingUserIdentity.setAliasName(aliasName);
        draftWcm = connectId.getWcm();
        draftWcm.setUserIdentity(existingUserIdentity);
      }
      //--------------- New Code also puts the new ConnectID in wcm.aboutYou.userIdentity()

      UserIdentity userIdentityFromAboutYou;
      if (draftWcm.getAboutyou().getUserIdentity() == null) {
        userIdentityFromAboutYou = new UserIdentity();
      }
      else
      {
        userIdentityFromAboutYou = draftWcm.getAboutyou().getUserIdentity();
      }
      userIdentityFromAboutYou.setConnectId(connectId.getConnectId());
      userIdentityFromAboutYou.setAliasName(aliasName);
      draftWcm.getAboutyou().setUserIdentity(userIdentityFromAboutYou);

      //Finish setup identify, remove disclosure to session.
      sessionUtil.setToSession(httpServletRequest, CREDENTIALS_SETUP_STAGE, draftWcm.getAboutyou().getCredentialConsentInfo());
      draftWcm.getAboutyou().setCredentialConsentInfo(null);
      LOGGER.debug("setupIdentity: WealthModel saved back to session");
      sessionUtil.setDraftClientProfile(httpServletRequest, draftWcm);
      // Record Update_Application event
      ResponseEntity<String> responseEntity = applicationDataHandler.getApplicationResponse(metaData, draftWcm, httpHeaders, updateAboutYouURL + "/" + WealthConstants.ABOUT_YOU_PERSONAL_INFO);
      if(HttpStatus.OK.equals(responseEntity.getStatusCode())) {
        String s = responseEntity.getBody();
        LOGGER.debug("Update about you profile resonse={}", s);
      } else {
        LOGGER.exit(responseEntity.getStatusCode());
        throw new ApiException(ExceptionUtil.buildErrorStatusFromApiErrorStatus(responseEntity));
      }

      LOGGER.debug("setupIdentity: Update_Application event recorded");
    }
    // Save back to session


    return connectId;
  }

  private List<LookupModel> filterAndSort(List<LookupModel> lookupList, boolean needSorting, String... excludeLookupCodes) {

    List<LookupModel> filteredLookupList = new ArrayList<>();

    outter:
    for(LookupModel lookup : lookupList) {
      for(String excludeCode : excludeLookupCodes) {
        if(excludeCode.equals(lookup.getCode())) {
          LOGGER.debug("exclude Lookup code :" + excludeCode);
          continue outter;
        }
      }
      filteredLookupList.add(lookup);
    }

    if(needSorting) {
      Collections.sort(filteredLookupList, new Comparator<LookupModel>() {
        @Override
        public int compare(LookupModel o1, LookupModel o2) {
          return o1.getDescription().compareTo(o2.getDescription());
        }
      });
    }

    return filteredLookupList;
  }

  public void updateEligibilitySummary(WealthClientMasterInfo wcm, String eventId, Boolean validationSuccessfulFrontEnd) throws ApiException, IOException {
    List<Eligibility> eligibilitySummary = wcm.getEligibilitySummary();
    if(eligibilitySummary == null) {
      eligibilitySummary = new ArrayList<>();
      eligibilitySummary.add(addEligibility(wcm, validationSuccessfulFrontEnd, eventId));
      wcm.setEligibilitySummary(eligibilitySummary);
    }
    else
    {
      Boolean sectionFound = false;
      for (Eligibility eligibility:eligibilitySummary) {
        if(eventId.equals(eligibility.getSection()) && wcm.getAboutyou().getProfileId().equals(eligibility.getProfileId())) {
          eligibility.setEligible(validationSuccessfulFrontEnd && eligibility.isEligible());
          sectionFound = true;
          break;
        }
      }
      if(sectionFound == false){
        eligibilitySummary.add(addEligibility(wcm, validationSuccessfulFrontEnd, eventId));
      }
    }
  }

  private Eligibility addEligibility(WealthClientMasterInfo wcm, Boolean validationSuccessfulFrontEnd, String eventId){
    Eligibility eligibility= new Eligibility();
    eligibility.setProfileId(wcm.getAboutyou().getProfileId());
    eligibility.setSection(eventId);
    eligibility.setEligible(validationSuccessfulFrontEnd);
    return eligibility;
  }

  public Response updateProfile(HttpServletRequest httpServletRequest, WealthClientMasterInfo wcm, String eventId, HttpHeaders httpHeaders, MetaData metaData, Boolean validationSuccessfulFrontEnd, String productId) throws ApiException {

    applicationDataHelper.injectReferenceDataModel(wcm);
    LeftNavHelper leftNavHelper = new LeftNavHelper();

    // Validate updated data and merge with saved draft

    LOGGER.debug("Process event {}", eventId);
    WealthClientMasterInfo draftWcm = null;
    try {
      draftWcm = processProfileUpdateEvent(httpServletRequest, wcm, eventId, metaData, httpHeaders, productId);

      //Code here to Encode Clarifications to Base64
     // encodeClarificationsToBase64(draftWcm, "encode");


      // Update the Array
      updateAllApplicationArray(draftWcm);

      String passThisEventToLeftNavFunction;
      if(productId!=null && !productId.isEmpty()) {
        //If a product name (AKA Account: ex RRSP) then we use this name to set up Left Nav
        passThisEventToLeftNavFunction = productId;
      }
      else
      {
        //For all other pages, we use the EventID
        passThisEventToLeftNavFunction = eventId;
      }
      leftNavHelper.updateLeftNav(draftWcm, passThisEventToLeftNavFunction, validationSuccessfulFrontEnd);
      leftNavHelper.leftNavExceptionOverride(draftWcm, wcm, passThisEventToLeftNavFunction);

    } catch(IOException e) {
      LOGGER.error("updateProfile is failed", e);
      throw new ApiException(ExceptionUtil.buildServerErrorStatus());
    }
    LOGGER.debug("Update about you profile url={}/{}", updateAboutYouURL, eventId);

    // Even if Front End tells the backend that a particular page is valid, Backend may still override the Front End if Backend finds
    // that the eligibility has failed. Ideally, Front end should have blocked this situation otherwise FE would not be able to show any error
    ResponseEntity<String> responseEntity = applicationDataHandler.getApplicationResponse(metaData, draftWcm, httpHeaders, updateAboutYouURL + "/" + eventId);

    if(HttpStatus.OK.equals(responseEntity.getStatusCode())) {
      if (responseEntity.getBody() != null) {
        try {
          EligibilityStatus eligibilityStatus = objectMapper.readValue((String) responseEntity.getBody().toString(), EligibilityStatus.class);
          if(eligibilityStatus.getSelectionEligible()==false) {
            //Both Backend and Front end should have the Validation as True
            leftNavHelper.updateLeftNav(draftWcm, eventId, false);
          }

        }
        catch (Exception exp)
        {
          LOGGER.exit(responseEntity.getStatusCode());
          throw new ApiException(ExceptionUtil.buildErrorStatusFromApiErrorStatus(responseEntity));
        }
      }
      //Preserve the final wealthModel in session so that it could be retrieved by the Profile event later on.
      sessionUtil.setDraftClientProfile(httpServletRequest, draftWcm);


      return Response.ok(responseEntity.getBody(), MediaType.APPLICATION_JSON).build();
    } else {
      LOGGER.exit(responseEntity.getStatusCode());
      throw new ApiException(ExceptionUtil.buildErrorStatusFromApiErrorStatus(responseEntity));
    }


  }

  private WealthClientMasterInfo cleanupProductRelatedInfo(WealthClientMasterInfo wcm, String productId) {
    switch (productId) {
      case WealthConstants.ITF_PRODUCT:
      case WealthConstants.INFORMAL_TRUST_PRODUCT:
        if (wcm.getAboutyou()!=null
          && wcm.getAboutyou().getAccountDetails()!=null
          && wcm.getAboutyou().getAccountDetails().getPurpose()!=null
        )
        {
          List<AccountPurpose> accountPurposeList = wcm.getAboutyou().getAccountDetails().getPurpose();
          for (AccountPurpose accountPurpose :accountPurposeList) {
            if(INFORMAL_TRUST_PRODUCT.equals(accountPurpose.getAccountType().toLowerCase())
              || ITF_PRODUCT.equals(accountPurpose.getAccountType().toLowerCase())){
              if(accountPurpose.getItf()!=null
                && OTHER.equals(accountPurpose.getItf().getSourceOfFund())
                && accountPurpose.getItf().getBeneficiary()!=null){
                accountPurpose.getItf().getBeneficiary().setSIN(""); //Clear SIN if provided
              }
            }
          }
        }
        break;
        default:
          break;
    }
    return wcm;
  }

  private WealthClientMasterInfo processProfileUpdateEvent(HttpServletRequest httpServletRequest, WealthClientMasterInfo wcm, String eventId, MetaData metaData, HttpHeaders httpHeaders, String productId) throws ApiException, IOException {

    AboutYou aboutYou = wcm.getAboutyou();
    if(aboutYou == null) {
      throw new ApiException(ExceptionUtil.buildErrorStatus(400, "Invalid Data, missing About You"));
    }

    WealthClientMasterInfo latestWcmFromSession = customerDAO.getWealthClientMasterInfo(sessionUtil.getDraftClientProfile(httpServletRequest));
    if(!aboutYou.getProfileId().equals(latestWcmFromSession.getAboutyou().getProfileId())){
      //Front end is updating data for CoApplicant or a Party
      for (AboutYou aboutApplicantAndParty: latestWcmFromSession.getAboutAllApplicantsAndParties()) {
        if((aboutYou.getProfileId().equals(aboutApplicantAndParty.getProfileId()) == true )){
          latestWcmFromSession.setAboutyou(aboutApplicantAndParty);
          break;
        }
      }
    }

    AboutYou draftAboutYou = latestWcmFromSession.getAboutyou();
    List<Product> draftProducts = latestWcmFromSession.getProducts();
    UserIdentity draftUserId = latestWcmFromSession.getUserIdentity();

    //products can change for any event.
    latestWcmFromSession.setProducts(wcm.getProducts());
    AboutYou aboutYouLocal;

    //get locale
    String strLocale = httpServletRequest.getHeader(ExperienceConstants.HTTP_HEADER_LOCALE);
    if(strLocale==null  || strLocale.isEmpty()){
      EsoJsonData esoData = sessionUtil.getSessionData(httpServletRequest);
      strLocale = (String) esoData.get(SessionUtil.SESSION_KEY_LOCALE);
    }
    String channel = "";
    if(latestWcmFromSession.getUserIdentity()!=null) {
      channel = latestWcmFromSession.getUserIdentity().getChannel();
    }
    switch(eventId) {
      case WealthConstants.ABOUT_YOU_PERSONAL_INFO:
      case WealthConstants.CLIENT_PROFILE:

        processPersonalInfoEvent(draftAboutYou, aboutYou);
        processContactInfoEvent(draftAboutYou, aboutYou);
        //Code below is for Self-Serve only
        if ("N2W".equals(wcm.getUserIdentity().getUserType())  && (CHANNEL_SELF_SERVE_WEB.equals(channel)) ) {
          if (WealthConstants.ABOUT_YOU_PERSONAL_INFO_CARD_TAX_INFO.equals(wcm.getLastApplicationState().getCard())) {
            ResponseEntity<WealthClientMasterInfo> captureConsentResponse = consentHandler.makeCaptureConsentCall(httpHeaders, metaData, wcm, ExperienceConstants.CAPTURE_CONSENT_URL, false, false, WealthConstants.TC_CONSENT);
            WealthClientMasterInfo captureConsentWealthMode = captureConsentResponse.getBody();
            draftAboutYou.setCredentialConsentInfo(captureConsentWealthMode.getAboutyou().getCredentialConsentInfo());
            //Once we have recorded Consent, we freeze the PersonalDetails so that they cannot be changed
            draftAboutYou.setAboutYouFrozen(true);
          }
        }
        if (!CHANNEL_SELF_SERVE_WEB.equals(channel)) {
          processEmploymentInfoEvent(draftAboutYou, aboutYou);
          draftAboutYou.setFinancialInfo(aboutYou.getFinancialInfo());
          latestWcmFromSession.setIdCaptureInfo(wcm.getIdCaptureInfo());
          break;
        }

        break;
      case WealthConstants.ABOUT_YOU_CONTACT_INFO:
        processPersonalInfoEvent(draftAboutYou, aboutYou);
        processContactInfoEvent(draftAboutYou, aboutYou);
        break;

      case WealthConstants.ABOUT_YOU_EMPLOYMENT_INFO:
        processEmploymentInfoEvent(draftAboutYou, aboutYou);
        draftAboutYou.setFinancialInfo(aboutYou.getFinancialInfo());
        break;

      case WealthConstants.CHANGE_PRODUCTS:
        logSwitchAccountEvent(draftProducts, latestWcmFromSession.getProducts(), httpHeaders, metaData);
        aboutYouLocal = latestWcmFromSession.getAboutyou();
        if (CHANNEL_BRANCH.equals(wcm.getUserIdentity().getChannel())) {
          //For Branch channels, we preserve the AccountDetails as sent by Front end
          aboutYouLocal.setAccountDetails(wcm.getAboutyou().getAccountDetails());
        } else {
          aboutYouLocal.setAccountDetails(addAccount(wcm.getProducts(), latestWcmFromSession.getAboutyou().getAccountDetails(), true));
        }
        latestWcmFromSession.setAboutyou(aboutYouLocal);
        break;

      case WealthConstants.ID_CAPTURE_INFO:
//        latestWcmFromSession.setIdCaptureInfo(wcm.getIdCaptureInfo());
        if((latestWcmFromSession.getAboutyou()!=null) && (wcm.getAboutyou()!=null)){
          latestWcmFromSession.getAboutyou().setIdCaptureInfo(wcm.getAboutyou().getIdCaptureInfo());
        }
        break;
      case WealthConstants.INITIAL_STATE:
        processPersonalInfoEvent(draftAboutYou, aboutYou);
        processContactInfoEvent(draftAboutYou, aboutYou);
        UserIdentity userIdentity = new UserIdentity();
        userIdentity.setUserType(metaData.getFlowId());
        latestWcmFromSession.setUserIdentity(userIdentity);
        break;
      case WealthConstants.FINANCIAL_INFO:
        draftAboutYou.setFinancialInfo(aboutYou.getFinancialInfo());
        break;
      case WealthConstants.PRODUCT_SELECTOR:
        latestWcmFromSession.setProducts(wcm.getProducts());
        aboutYouLocal = latestWcmFromSession.getAboutyou();
        aboutYouLocal.setAccountDetails(addAccount(wcm.getProducts(), wcm.getAboutyou().getAccountDetails()));
        //ESODI-5439 Set mailing address as same as legal address by default
        aboutYouLocal.getContactInfo().setMailingAddressSameAsLegal(true);
        latestWcmFromSession.setAboutyou(aboutYouLocal);

        //set promo code if valid
        LookupModel promoCode = wcm.getApplicationInfo().getPromoCode();
        if (promoCode.getCode() != null) {
          if (!promoCode.getCode().isEmpty()) {
            PromoCodeStatus promoCodeStatus = promoCodeFacade.validatePromoCode(promoCode);
            if (promoCodeStatus.getCodeValid()) {
              latestWcmFromSession.getApplicationInfo().setPromoCode(promoCode);
            }
          } else if (promoCode.getCode().isEmpty()) { //UI send empty string for update
            latestWcmFromSession.getApplicationInfo().setPromoCode(promoCode);
          }
        }
        disclosureHelper.loadDisclosureConfig(metaData, latestWcmFromSession);
        if (CHANNEL_SELF_SERVE_WEB.equals(latestWcmFromSession.getUserIdentity().getChannel())) {
          disclosureHelper.populateDisclosureDetails(metaData, httpHeaders, latestWcmFromSession, strLocale);
        }
        break;
      case WealthConstants.MARGIN_CHECK_UPDATE:
      if(FeatureToggle.CONSENT_CAPTURE_ENABLE()) {
        ResponseEntity<WealthClientMasterInfo> captureConsentResponse = consentHandler.makeCaptureConsentCall(httpHeaders, metaData, wcm, ExperienceConstants.CAPTURE_CONSENT_URL, true, false, WealthConstants.MARGIN_CHECK_UPDATE);
        WealthClientMasterInfo captureConsentWealthModel = captureConsentResponse.getBody();
        //no need to keep the document list anymore, set to session for backup.
        sessionUtil.setToSession(httpServletRequest, MARGIN_BRANCH_CHECK_STAGE, captureConsentWealthModel.getAboutyou().getMarginConsentInfo().getDisclosureDocumentInfoList());
        //      Commented out temporarily to fix Sprint bug
        //        captureConsentWealthModel.getAboutyou().getMarginConsentInfo().setDisclosureDocumentInfoList(null);
        disclosureHelper.loadDisclosureConfig(metaData, captureConsentWealthModel);
        draftAboutYou.setMarginConsentInfo(captureConsentWealthModel.getAboutyou().getMarginConsentInfo());
      }
        break;

      case WealthConstants.ELECTRONIC_ACCOUNT_CONSENT:
        if(FeatureToggle.CONSENT_CAPTURE_ENABLE()) {
          ResponseEntity<WealthClientMasterInfo> captureEAConsentResponse = consentHandler.makeCaptureConsentCall(httpHeaders, metaData, wcm, ExperienceConstants.CAPTURE_CONSENT_URL, true, false, WealthConstants.ELECTRONIC_ACCOUNT_CONSENT);
          WealthClientMasterInfo captureEAConsentWealthModel = captureEAConsentResponse.getBody();
          draftAboutYou.setElectronicAccountConsentInfo(captureEAConsentWealthModel.getAboutyou().getElectronicAccountConsentInfo());
        }
        break;

      case WealthConstants.DIGITAL_SIGNATURE_CONSENT:
        if(FeatureToggle.CONSENT_CAPTURE_ENABLE()) {
          ResponseEntity<WealthClientMasterInfo> captureDSConsentResponse = consentHandler.makeCaptureConsentCall(httpHeaders, metaData, wcm, ExperienceConstants.CAPTURE_CONSENT_URL, true, false, WealthConstants.DIGITAL_SIGNATURE_CONSENT);
          WealthClientMasterInfo captureDSConsentWealthModel = captureDSConsentResponse.getBody();
          draftAboutYou.setDigitalSignatureConsentInfo(captureDSConsentWealthModel.getAboutyou().getDigitalSignatureConsentInfo());
        }
        break;

      case WealthConstants.ACK_AGREE_CONTRACT_CONSENT:
        if(FeatureToggle.CONSENT_CAPTURE_ENABLE()) {
          ResponseEntity<WealthClientMasterInfo> captureAckAgreeConsentResponse = consentHandler.makeCaptureConsentCall(httpHeaders, metaData, wcm, ExperienceConstants.CAPTURE_CONSENT_URL, true, false, WealthConstants.ACK_AGREE_CONTRACT_CONSENT);
          WealthClientMasterInfo captureAckAgreeConsentWealthModel = captureAckAgreeConsentResponse.getBody();
          draftAboutYou.setContractConsentInfo(captureAckAgreeConsentWealthModel.getAboutyou().getContractConsentInfo());
        }
        break;

      case WealthConstants.IMPORTANT_CONSENT:
        if(FeatureToggle.CONSENT_CAPTURE_ENABLE()) {
          ResponseEntity<WealthClientMasterInfo> captureImportantConsentResponse = consentHandler.makeCaptureConsentCall(httpHeaders, metaData, wcm, ExperienceConstants.CAPTURE_CONSENT_URL, true, false, WealthConstants.IMPORTANT_CONSENT);
          WealthClientMasterInfo captureImportantConsentWealthModel = captureImportantConsentResponse.getBody();
          sessionUtil.setToSession(httpServletRequest, IMPORTANT_CONSENT_STAGE, captureImportantConsentWealthModel.getAboutyou().getImportantConsentInfo().getDisclosureDocumentInfoList());
//      Commented out temporarily to fix Sprint bug
//         captureImportantConsentWealthModel.getAboutyou().getImportantConsentInfo().setDisclosureDocumentInfoList(null);
          disclosureHelper.loadDisclosureConfig(metaData, captureImportantConsentWealthModel);
          draftAboutYou.setImportantConsentInfo(captureImportantConsentWealthModel.getAboutyou().getImportantConsentInfo());
        }
        break;

      case WealthConstants.BRANCH_TC_CONSENT:
        if(FeatureToggle.CONSENT_CAPTURE_ENABLE()) {
          ResponseEntity<WealthClientMasterInfo> captureBranchTcConsentResponse = consentHandler.makeCaptureConsentCall(httpHeaders, metaData, wcm, ExperienceConstants.CAPTURE_CONSENT_URL, true, false, WealthConstants.BRANCH_TC_CONSENT);
          WealthClientMasterInfo captureBranchTcConsentWealthModel = captureBranchTcConsentResponse.getBody();
          sessionUtil.setToSession(httpServletRequest, BRANCH_TC_CONSENT_STAGE, captureBranchTcConsentWealthModel.getAboutyou().getBranchTcConsentInfo().getDisclosureDocumentInfoList());
//      Commented out temporarily to fix Sprint bug
//          captureBranchTcConsentWealthModel.getAboutyou().getBranchTcConsentInfo().setDisclosureDocumentInfoList(null);
          disclosureHelper.loadDisclosureConfig(metaData, captureBranchTcConsentWealthModel);
          draftAboutYou.setBranchTcConsentInfo(captureBranchTcConsentWealthModel.getAboutyou().getBranchTcConsentInfo());
        }
        break;

      case ExperienceConstants.EVENT_BUSINESS_OUTCOME_AGENT_TO_CUST_HANDOFF:
        logDeviceEvent(httpHeaders, metaData, ExperienceConstants.EVENT_TYPE_DEVICE_HANDOFF, ExperienceConstants.EVENT_BUSINESS_OUTCOME_AGENT_TO_CUST_HANDOFF);
        break;

      case ExperienceConstants.EVENT_BUSINESS_OUTCOME_CUST_TO_AGENT_HANDOFF:
        logDeviceEvent(httpHeaders, metaData, ExperienceConstants.EVENT_TYPE_DEVICE_HANDOFF, ExperienceConstants.EVENT_BUSINESS_OUTCOME_CUST_TO_AGENT_HANDOFF);
        break;

      case ExperienceConstants.EVENT_BUSINESS_OUTCOME_CUST_ACCEPT:
        logDeviceEvent(httpHeaders, metaData, ExperienceConstants.EVENT_TYPE_DEVICE_ACCEPT, ExperienceConstants.EVENT_BUSINESS_OUTCOME_CUST_ACCEPT);
        break;

      case ExperienceConstants.EVENT_BUSINESS_OUTCOME_AGENT_ACCEPT:
        logDeviceEvent(httpHeaders, metaData, ExperienceConstants.EVENT_TYPE_DEVICE_ACCEPT, ExperienceConstants.EVENT_BUSINESS_OUTCOME_AGENT_ACCEPT);
        break;
      case WealthConstants.INVESTMENT_INFO:
      case WealthConstants.ACCOUNT_DETAILS: //No break, continue to default process for remaining details
        //Duplicate Statements and Other Entities are set for ACCOUNT_DETAILS
        latestWcmFromSession.setDuplicateStatementsList(wcm.getDuplicateStatementsList());
        latestWcmFromSession.setOtherEntities(processOtherEntities(wcm.getOtherEntities()));
        wcm = cleanupProductRelatedInfo(wcm, productId);
      default:
        if (!CHANNEL_SELF_SERVE_WEB.equals(channel))
        {
          latestWcmFromSession.setApplicationInfo(wcm.getApplicationInfo());
          processDefaultEvent(draftAboutYou, aboutYou, draftUserId);
          break;
        }
        processDefaultEvent(draftAboutYou, aboutYou, draftUserId);

    }
    latestWcmFromSession.setEligibilitySummary(wcm.getEligibilitySummary());
    latestWcmFromSession.setLastApplicationState(wcm.getLastApplicationState());
    return latestWcmFromSession;
  }

  private String encodeBase64(String inputStr){
    String s = Encode.forHtml(StringEscapeUtils.unescapeJava(inputStr));
    String outputStr = new String(Base64.getEncoder().encode(s.getBytes()));
    return outputStr;
}

  private void encodeClarificationsToBase64(WealthClientMasterInfo latestWcmFromSession, String encodeDecodeFlag) {
    AboutYou aboutYou = latestWcmFromSession.getAboutyou();
    if (aboutYou != null) {
      if (aboutYou.getFinancialInfo() != null && aboutYou.getFinancialInfo().getAdditionalInfo() != null) {
        aboutYou.getFinancialInfo().setAdditionalInfo(processAdditionalInfo(encodeDecodeFlag, aboutYou.getFinancialInfo().getAdditionalInfo()));
      }

      if (aboutYou.getEmploymentInfo() != null && aboutYou.getEmploymentInfo().getClarificationForEmploymentStatus() != null)
      {
        aboutYou.getEmploymentInfo().setClarificationForEmploymentStatus(processAdditionalInfo(encodeDecodeFlag, aboutYou.getEmploymentInfo().getClarificationForEmploymentStatus()));
      }

      if (aboutYou.getEmploymentInfo() != null && aboutYou.getEmploymentInfo().getAddress() !=null &&
        aboutYou.getEmploymentInfo().getAddress().getClarificationForEmploymentAddress() != null)
      {
        aboutYou.getEmploymentInfo().getAddress().setClarificationForEmploymentAddress(processAdditionalInfo(encodeDecodeFlag, aboutYou.getEmploymentInfo().getAddress().getClarificationForEmploymentAddress()));
      }

      //.getContactInfo().getMailingAddress().getClarificationMailingAddressProvince()
      if (aboutYou.getContactInfo() != null && aboutYou.getContactInfo().getMailingAddress() !=null &&
        aboutYou.getContactInfo().getMailingAddress().getClarificationMailingAddressProvince() != null)
      {
        aboutYou.getContactInfo().getMailingAddress().setClarificationMailingAddressProvince(processAdditionalInfo(encodeDecodeFlag, aboutYou.getContactInfo().getMailingAddress().getClarificationMailingAddressProvince()));
      }

      //SCHOOL PENDING:   EMPLOYMENT ADDRESS PENDING
      //this.getAboutyou().getEmploymentInfo().getAddress().getClarificationForEmploymentAddress()
      if (aboutYou.getEmploymentInfo() != null && aboutYou.getEmploymentInfo().getAddress() !=null &&
        aboutYou.getEmploymentInfo().getAddress().getClarificationForEmploymentAddress() != null)
      {
        aboutYou.getEmploymentInfo().getAddress().setClarificationForEmploymentAddress(processAdditionalInfo(encodeDecodeFlag, aboutYou.getEmploymentInfo().getAddress().getClarificationForEmploymentAddress()));
      }

//      this.getAboutyou().getFinancialInfo().getClarificationLiquidAssets()
      if (aboutYou.getFinancialInfo() != null && aboutYou.getFinancialInfo().getClarificationLiquidAssets() !=null &&
        aboutYou.getFinancialInfo().getClarificationLiquidAssets() != null)
      {
        aboutYou.getFinancialInfo().setClarificationLiquidAssets(processAdditionalInfo(encodeDecodeFlag, aboutYou.getFinancialInfo().getClarificationLiquidAssets()));
      }
//this.getAboutyou().getPartnerInfo().getEmploymentInfo().getClarificationForEmploymentStatus()
      if (aboutYou.getPartnerInfo() != null && aboutYou.getPartnerInfo().getEmploymentInfo() != null &&
        aboutYou.getPartnerInfo().getEmploymentInfo().getClarificationForEmploymentStatus() != null)
      {
        aboutYou.getPartnerInfo().getEmploymentInfo().setClarificationForEmploymentStatus(processAdditionalInfo(encodeDecodeFlag, aboutYou.getPartnerInfo().getEmploymentInfo().getClarificationForEmploymentStatus()));
      }

      //this.getAboutyou().getContactInfo().getClarificationContactPhoneNumber()
      if (aboutYou.getContactInfo() != null && aboutYou.getContactInfo().getClarificationContactPhoneNumber() !=null
        )
      {
        aboutYou.getContactInfo().setClarificationContactPhoneNumber(processAdditionalInfo(encodeDecodeFlag, aboutYou.getContactInfo().getClarificationContactPhoneNumber()));
      }

      //getIdCaptureInfo().getClarificationPhotoId()
      if (latestWcmFromSession.getIdCaptureInfo() != null && latestWcmFromSession.getIdCaptureInfo().getClarificationPhotoId() !=null
        )
      {
        latestWcmFromSession.getIdCaptureInfo().setClarificationPhotoId(processAdditionalInfo(encodeDecodeFlag, latestWcmFromSession.getIdCaptureInfo().getClarificationPhotoId()));
      }

    }
  }

private AdditionalInfo  processAdditionalInfo(String encodeDecodeFlag, AdditionalInfo additionalInfo){
if(additionalInfo!=null) {
  if (
    additionalInfo.getCommentTxt() != null &&
    !additionalInfo.getCommentTxt().isEmpty()
    ) {
if("encode".equalsIgnoreCase(encodeDecodeFlag)) {
  String strLocal = Encode.forHtml(StringEscapeUtils.unescapeJava(additionalInfo.getCommentTxt()));
  additionalInfo.setCommentTxt(encodeBase64(strLocal));
  additionalInfo.setEncoded(true);
}
else
{
    String decodedStr = new String(Base64.getDecoder().decode(additionalInfo.getCommentTxt()));
    additionalInfo.setCommentTxt(decodedStr);
}
  }
}
  return additionalInfo;
}

  private AccountDetails addAccount(List<Product> products, AccountDetails existingAccountDetail, boolean... changeProduct) {
    AccountPurpose accountPurpose;
    List<AccountPurpose> existingPurposes = new ArrayList<>();
    AccountDetails accountDetails = new AccountDetails();
    if(existingAccountDetail != null) {
      accountDetails.setAccountNumber(existingAccountDetail.getAccountNumber());
      accountDetails.setMaintenanceFee(existingAccountDetail.getMaintenanceFee());
      accountDetails.setShareholderCommunication(existingAccountDetail.getShareholderCommunication());
      accountDetails.setShareInfoSecurityHolder(existingAccountDetail.getShareInfoSecurityHolder());

      if(existingAccountDetail.getPurpose() != null) {
        existingPurposes = existingAccountDetail.getPurpose();
      }
    }
    List<AccountPurpose> accountPurposes = new ArrayList<AccountPurpose>();
    for(Product product : products) {
      accountPurpose = new AccountPurpose();
      accountPurpose.setAccountType(product.getProductId());
      if(existingPurposes != null) {
        for(AccountPurpose existing : existingPurposes) {
          if (PRODUCT_CASH.equals(product.getProductId())) {
            if (! ArrayUtils.isEmpty(changeProduct) && changeProduct[0]) {
              if (PRODUCT_MARGIN.equals(existing.getAccountType()) || USTP_PRODUCT.equalsIgnoreCase(existing.getAccountType())) {
                accountPurpose = existing;
                accountPurpose.setAccountType(product.getProductId());
              }
            }
          }
          if(existing.getAccountType()!=null) {
            if (existing.getAccountType().equals(product.getProductId())) {
              accountPurpose = existing;
              break;
            }
          }
        }
      }
      accountPurposes.add(accountPurpose);
    }
    accountDetails.setPurpose(accountPurposes);
    return accountDetails;
  }

  private void processDefaultEvent(AboutYou draftAboutYou, AboutYou aboutYou, UserIdentity draftUserId) throws ApiException {

    processBeneficiaryInfo(aboutYou);
    draftAboutYou.setAccountDetails(aboutYou.getAccountDetails());
    draftAboutYou.setFinancialInfo(aboutYou.getFinancialInfo());
    draftAboutYou.setInvestmentInfo(aboutYou.getInvestmentInfo());

    //set the client type when invest info is present
    if (aboutYou.getInvestmentInfo() != null) {
      String clientType = CommonUtil.getCMClientType(aboutYou.getInvestmentInfo());
      draftUserId.setClientTyp(clientType);
    }
    if(aboutYou.getPartnerInfo()!=null) {
      draftAboutYou.getPartnerInfo().setSIN(aboutYou.getPartnerInfo().getSIN());
      draftAboutYou.getPartnerInfo().setDOB(aboutYou.getPartnerInfo().getDOB());
    }
  }


  private void processBeneficiaryInfo(AboutYou aboutYou) {
    if(aboutYou.getAccountDetails().getPurpose() != null) {
      int[] purposeIndex = {0};
      aboutYou.getAccountDetails().getPurpose().forEach(purpose -> {
        if(purpose.getBeneficiary() != null && purpose.getBeneficiary()) {
          int[] beneficiaryIndex = {0};
          purpose.getBeneficiaryInfoList().forEach(beneficiaryInfo -> {
            if (beneficiaryInfo.getType() != null && beneficiaryInfo.getType().equalsIgnoreCase(ExperienceConstants.ESTATE)
              && (beneficiaryInfo.getFirstName() != null || beneficiaryInfo.getLastName() != null)) {
              beneficiaryInfo.setFirstName(null);
              beneficiaryInfo.setLastName(null);
              beneficiaryInfo.setRelationship(null);
              beneficiaryInfo.setRelationshipDescription(null);
              purpose.getBeneficiaryInfoList().set(beneficiaryIndex[0], beneficiaryInfo);
              beneficiaryIndex[0]++;
            }
            if(beneficiaryInfo.getType() != null && beneficiaryInfo.getType().equalsIgnoreCase(ExperienceConstants.INDIVIDUAL)
              && !beneficiaryInfo.getRelationship().equalsIgnoreCase(ExperienceConstants.OTHER)) {
                if(beneficiaryInfo.getRelationshipDescription() != null) {
                  beneficiaryInfo.setRelationshipDescription(null);
                }
              purpose.getBeneficiaryInfoList().set(beneficiaryIndex[0], beneficiaryInfo);
            }
            beneficiaryIndex[0]++;
          });
          aboutYou.getAccountDetails().getPurpose().set(purposeIndex[0], purpose);
        }
        purposeIndex[0]++;
      });
    }
  }


  private void processPersonalInfoEvent(AboutYou draftAboutYou, AboutYou aboutYou) throws ApiException {
    // Process personalInfo
    PersonalInfo pi = aboutYou.getPersonalInfo();
    if(pi == null) {
      throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), "Personal Info is missing"));
    }

    validationUtil.validateDataAgainstDropdown(pi, false);
    updateCMIds(pi, draftAboutYou.getPersonalInfo());
    draftAboutYou.setPersonalInfo(pi);
    draftAboutYou.setIdCaptureInfo(aboutYou.getIdCaptureInfo());
    draftAboutYou.setHasPhotoId(aboutYou.isHasPhotoId());
    draftAboutYou.setWBCredentialsRequired(aboutYou.getWBCredentialsRequired());
    // Process partnerInfo when applicant is married or common law
    if(pi.getMaritalStatus() != null) {
      if(pi.getMaritalStatus().equals(LookupConstants.MARITAL_STATUS_MARRIED) || pi.getMaritalStatus().equals(LookupConstants.MARITAL_STATUS_COMMON_LAW)) {
        PartnerInfo pti = aboutYou.getPartnerInfo();
        if(pti == null) {
          throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), "Partner Info is missing"));
        }
        if(draftAboutYou.getPartnerInfo() == null) {
          draftAboutYou.setPartnerInfo(new PartnerInfo());
        }
        // Only update Spouse name for personalInfo event, don't update
        // Spouse Employment here
        draftAboutYou.getPartnerInfo().setFirstName(pti.getFirstName());
        draftAboutYou.getPartnerInfo().setMiddleName(pti.getMiddleName());
        draftAboutYou.getPartnerInfo().setLastName(pti.getLastName());
        draftAboutYou.getPartnerInfo().setSIN(pti.getSIN());
        draftAboutYou.getPartnerInfo().setDOB(pti.getDOB());
      }
    }
  }

  private void updateCMIds(PersonalInfo targetPI, PersonalInfo comparePI) {
    if(targetPI != null && comparePI != null) {
      if(targetPI.getPreferredName() != null && comparePI.getPreferredName() != null) {
        if(!targetPI.getPreferredName().equals(comparePI.getPreferredName())) {
          targetPI.setPreferredNameId("");
        }
      }
      if(targetPI.getCitizenship() != null && comparePI.getCitizenship() != null) {
        if(!targetPI.getCitizenship().equals(comparePI.getCitizenship())) {
          targetPI.setCitizenshipId("");
        }
      }
      if(targetPI.getCitizenship2() != null && comparePI.getCitizenship2() != null) {
        if(!targetPI.getCitizenship2().equals(comparePI.getCitizenship2())) {
          targetPI.setCitizenshipId2("");
        }
      }
    }
  }

  private void processEmploymentInfoEvent(AboutYou draftAboutYou, AboutYou aboutYou) throws ApiException {
    // Process Applicant Employment Info
    EmploymentInfo applicantEI = aboutYou.getEmploymentInfo();
    if(applicantEI == null) {
      throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), "Employment Info is missing"));
    }
    if(applicantEI.getStatusCd() != null) {
      if(applicantEI.getStatusCd().equals(LookupConstants.EMPLOYMENT_STATUS_STUDENT)) {
        // Student doesn't have Industry and Occupation
        applicantEI.setIndustry(null);
        applicantEI.setOccupation(null);
      }

      if(applicantEI.getStatusCd().equals(LookupConstants.EMPLOYMENT_STATUS_STUDENT) || applicantEI.getStatusCd().equals(LookupConstants.EMPLOYMENT_STATUS_EMPLOYED)
        || applicantEI.getStatusCd().equals(LookupConstants.EMPLOYMENT_STATUS_SELF_EMPLOYED)) {
        // Applicant must provide address for student, employed or self
        // employed
        Address employerAddress = applicantEI.getAddress();
        if(employerAddress == null) {
          throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), "Employer Address is missing"));
        } else {
          String employerCountry = employerAddress.getCountry();
          if(employerCountry != null) {
            if(!StringUtils.isEmpty(employerCountry)) {
              if(!employerCountry.equals(LookupConstants.COUNTRY_CODE_CA) && !employerCountry.equals(LookupConstants.COUNTRY_CODE_US)) {
                employerAddress.setProvince(null);
              }
            }
          }

        }
      } else {
        applicantEI.setAddress(null);
      }

      //clean up employerPhoneCountryCode, employerPhoneExtension and employerPhoneNumber if the following condition meets
      //add EMPLOYMENT_STATUS_CAREGIVER here when care giver becomes available in Client Master
      if(LookupConstants.EMPLOYMENT_STATUS_RETIRED.equals(applicantEI.getStatusCd()) ||
        LookupConstants.EMPLOYMENT_STATUS_UNEMPLOYED.equals(applicantEI.getStatusCd()) ||
        LookupConstants.EMPLOYMENT_STATUS_STUDENT.equals(applicantEI.getStatusCd()) ||
        LookupConstants.EMPLOYMENT_STATUS_HOMEMAKER.equals(applicantEI.getStatusCd()) ||
        LookupConstants.EMPLOYMENT_STATUS_AT_HOME_PARENT.equals(applicantEI.getStatusCd())) {

        applicantEI.setEmployerPhoneCountryCode(null);
        applicantEI.setEmployerPhoneExtension(null);
        applicantEI.setEmployerPhoneNumber(null);
        applicantEI.setEmployerPhoneCountryCodeModel(null);

      }
    }

    //Edit Employment Info on Review page should clear out financial info's additionalInfo object when condition meets
    if(aboutYou.getFinancialInfo().getAdditionalInfo() == null) {
      draftAboutYou.getFinancialInfo().setAdditionalInfo(null);
    }

    validationUtil.validateDataAgainstDropdown(applicantEI, false);
    draftAboutYou.setEmploymentInfo(applicantEI);

    // Process Spouse Employment Info when client is married or common law
    String marritalStatus = draftAboutYou.getPersonalInfo().getMaritalStatus();
    if (marritalStatus !=null) {
      if (marritalStatus.equals(LookupConstants.MARITAL_STATUS_COMMON_LAW) || marritalStatus.equals(LookupConstants.MARITAL_STATUS_MARRIED)) {
        // Married or Common Law must have spouse EI
        PartnerInfo pai = aboutYou.getPartnerInfo();
        if (pai == null) {
          throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), "Partner Info is missing"));
        }
        EmploymentInfo spouseEI = pai.getEmploymentInfo();
        if (spouseEI == null) {
          throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), "Partner Employment Info is missing"));
        }
        // Spouse will never have Employer address regardless of employment
        // status
        spouseEI.setAddress(null);
        if (spouseEI.getStatusCd() != null) {
          if (spouseEI.getStatusCd().equals(LookupConstants.EMPLOYMENT_STATUS_STUDENT)) {
            // Student doesn't have Industry and Occupation
            spouseEI.setIndustry(null);
            spouseEI.setOccupation(null);
          }
        }
        draftAboutYou.getPartnerInfo().setEmploymentInfo(spouseEI);
      }
    }
  }

  private void processContactInfoEvent(AboutYou draftAboutYou, AboutYou aboutYou) throws ApiException {
    // Process Applicant Employment Info
    ContactInfo ci = aboutYou.getContactInfo();
    if(ci == null) {
      throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), "Contact Info is missing"));
    }

    if(isEmpty(ci.getPhoneType()))
    {
      //ci.setPhoneType(ExperienceConstants.MOBILE); //No predefined phone type for now.
    }

    if (ci.getMailingAddressSameAsLegal() != null && ci.getMailingAddressSameAsLegal()) {
      ci.setMailingAddress(ci.getLegalAddress());

    }

    if(!isEmpty(ci.getPhoneType()) && !validationUtil.lookupModelListContainsCode(ci.getPhoneType(), ReferenceDataEnum.PHONETYPES)) {
      throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), "Invalid Phone Type"));
    }
    if (ci.getOtherPhones() != null && ci.getOtherPhones().size() > 0) {
      for (Phone phone : ci.getOtherPhones()) {

        if(!isEmpty(phone.getPhoneType()) && !validationUtil.lookupModelListContainsCode(phone.getPhoneType(), ReferenceDataEnum.PHONETYPES)) {
          throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), "Invalid Phone Type"));
        }

      }
    }
    validationUtil.validateDataAgainstDropdown(ci.getLegalAddress(), false);
    draftAboutYou.setContactInfo(ci);
  }

  private List<Entity> processOtherEntities(List<Entity> otherEntities) {

    List<Entity> processedOtherEntities = new ArrayList<>();

    for(Entity entity : otherEntities) {
      //If switching entity type, clean up old values
      if(entity.getEntityType() != null && entity.getEntityType().name().equalsIgnoreCase(ENTITY_TYPE_PERSONAL)) {
        entity.setBusinessRelationshipModel(null);
        if(entity.getRelationship() != null && !entity.getRelationship().equalsIgnoreCase("OTHER")) {
          entity.setOtherRelationshipDesc(null);
        }
      }
      if(entity.getEntityType() != null && entity.getEntityType().name().equalsIgnoreCase(ENTITY_TYPE_BUSINESS)) {
        //Front end will be responsible to clean the data for business entity, or sometimes they are retained.
//        entity.setTitle(null);
//        entity.setTitleModel(null);
//        entity.setSuffixModel(null);
//        entity.setSuffix(null);
//        entity.setFirstName(null);
//        entity.setMiddleName(null);
//        entity.setLastName(null);
        entity.setPersonalRelationshipModel(null);
        if(entity.getRelationship() != null && !entity.getRelationship().equalsIgnoreCase("OTHER")) {
          entity.setOtherRelationshipDesc(null);
        }
      }
      processedOtherEntities.add(entity);
    }

    return processedOtherEntities;

  }


  private boolean isEmpty(String value) {
    return value == null || value.trim().isEmpty();
  }

  private void logSwitchAccountEvent(List<Product> fromProducts, List<Product> toProducts, HttpHeaders httpHeaders, MetaData metaData) throws JsonProcessingException, ApiException {
    String fromAccount = null;
    String toAccount = null;

    List<String> fromAccountList = new ArrayList<>();
    List<String> toAccountList = new ArrayList<>();

    for(Product fromProduct : fromProducts) {
      fromAccountList.add(fromProduct.getProductId());
    }
    for(Product toProduct : toProducts) {
      toAccountList.add(toProduct.getProductId());
    }
    for(String account : fromAccountList) {
      if(!toAccountList.contains(account)) {
        fromAccount = account;
        break;
      }
    }
    for(String account : toAccountList) {
      if(!fromAccountList.contains(account)) {
        toAccount = account;
        break;
      }
    }

    if(fromAccount != null && toAccount != null) {
      Map<String, List<String>> productsMap = new HashMap<>();
      productsMap.put("FromAccountList", fromAccountList);
      productsMap.put("ToAccountList", toAccountList);

      EsoCreateEventRequest esoCreateEventRequest = new EsoCreateEventRequest();


      Event event = new Event();
      event.setApplicationId(String.valueOf(metaData.getApplicationId()));
      event.setSubApplicationId(String.valueOf(metaData.getSubApplicationList().get(0).getSubApplicationId()));
      event.setProductId(metaData.getProductId());
      event.setEventTypeCD(ExperienceConstants.EVENT_TYPE_SWITCH_ACCOUNT);
      event.setEventStatus(ExperienceConstants.EVENT_STATUS_SUCCESS);
      event.setBusinessOutcomeCD(ExperienceConstants.DEFAULT_BUSINESS_OUTCOME);
      event.setEventMetaDataJSON(new ObjectMapper().writeValueAsString(productsMap));
      esoCreateEventRequest.setEvent(event);

      eventHelper.logEvent(esoCreateEventRequest, httpHeaders, metaData);
    }
  }

  private void logDeviceEvent(HttpHeaders httpHeaders, MetaData metaData, String eventType, String businessOutcomeCd) throws ApiException {
    EsoCreateEventRequest esoCreateEventRequest = new EsoCreateEventRequest();

    Event event = new Event();
    event.setApplicationId(String.valueOf(metaData.getApplicationId()));
    event.setSubApplicationId(String.valueOf(metaData.getSubApplicationList().get(0).getSubApplicationId()));
    event.setProductId(metaData.getProductId());
    event.setEventTypeCD(eventType);
    event.setEventStatus(ExperienceConstants.EVENT_STATUS_SUCCESS);
    event.setBusinessOutcomeCD(businessOutcomeCd);
    event.setEventMetaDataJSON("");
    esoCreateEventRequest.setEvent(event);

    eventHelper.logEvent(esoCreateEventRequest, httpHeaders, metaData);
  }


}


