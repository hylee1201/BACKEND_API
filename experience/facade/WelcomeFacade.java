package com.td.dcts.eso.experience.facade;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.td.coreapi.common.status.*;
import com.td.dcts.eso.events.createapplication.response.EsoCreateApplicationResponse;
import com.td.dcts.eso.experience.GetStartedController;
import com.td.dcts.eso.experience.WelcomeController;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.environmentjs.controller.EnvironmentJSController;
import com.td.dcts.eso.experience.helper.ApplicationEventRestHandler;
import com.td.dcts.eso.experience.helper.LeftNavHelper;
import com.td.dcts.eso.experience.model.DevicePrintVal;
import com.td.dcts.eso.experience.model.LoginInfo;
import com.td.dcts.eso.experience.model.SamlRequestData;
import com.td.dcts.eso.experience.model.associatesapi.Associate;
import com.td.dcts.eso.experience.model.associatesapi.AssociateOrganizations;
import com.td.dcts.eso.experience.model.associatesapi.Associates;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.dcts.eso.experience.model.response.ReferringAgent;
import com.td.dcts.eso.experience.model.response.WealthClientMasterInfo;
import com.td.dcts.eso.experience.util.*;
import com.td.dcts.eso.session.model.EsoJsonData;
import com.td.dcts.eso.session.model.EsoSession;
import com.td.eso.constants.WealthConstants;
import com.td.eso.util.FeatureToggle;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.td.dcts.eso.experience.constants.ExperienceConstants.*;

@Component
public class WelcomeFacade {

  static final XLogger logger = XLoggerFactory.getXLogger(WelcomeController.class);

  @Value("${application.url.appRootUrlEn:}")
  private String appRootEn;

  @Value("${application.url.appRootUrlFr:}")
  private String appRootFr;

  @Value("${application.url.n2b:#/td-eso-core-login-app/n2bLoginForm}")
  private String n2bUrl;

  @Value("${application.url.existing.client:#/td-eso-core-login-app/loginForm}")
  private String existingClientUrl;

  @Value("${application.url.sso.branch:#/start}")
  private String ssoBranchLaunchURL;

  @Value("${resturl.getStarted.associates.retrieve}")
  private String organizationRetrieveURL;

  @Value("${eo.investing.productId:DIR_INVEST}")
  private String syntheticProductId;

  private final String N2B = "n2b";

  @Value("${TD_ESO_COOKIE_NAME:TDESOSESSIONID}")
  private String appCookieName;

  @Value("${TD_ASSOCIATE_TEST_USER_ID:#{null}}")
  private String associateTestingUserId;

  @Value("${SSO_HAS_SESSION:false}")
  private boolean hasSession;

  @Autowired
  private GetStartedFacade getStartedFacade;

  @Autowired
  private ApplicationEventRestHandler applicationEventRestHandler;

  @Autowired
  private SessionUtil sessionUtil;

  @Autowired
  private MetaDataUtil metaDataUtil;

  @Autowired
  private ValidationUtil validationUtil;

  @Autowired
  private GetStartedController getStartedController;

  @Autowired
  private UAPFacade uAPFacade;

  @Autowired
  private EnvironmentJSController environmentJSController;

  public Response processWelcome(
    HttpServletRequest httpServletRequest,
    HttpServletResponse httpServletResponse,
    String lang,
    String marketingCode,
    String sourceCode,
    String sourceUrl,
    List<String> productIds,
    String n2b,
    String targetURL,
    String channelCode
  ) throws ApiException {
    //String locale = CommonUtil.getLocale(httpServletRequest);

    logger.debug("welcome({}, {}, {}), mkt: {}, src: {}, url: {}", lang, syntheticProductId, productIds, marketingCode, sourceCode, sourceUrl);
    // Default appURLRoot ../ would work if no Root URL provided in Environment.Properties
    String appURLRoot = "../";
    try {
      if(channelCode==null){
        channelCode= ExperienceConstants.CHANNEL_SELF_SERVE_WEB;
      }
      Map<String, Object> parameters = validationUtil.validateWelcomeParameters(lang, syntheticProductId, productIds, marketingCode, sourceCode, sourceUrl, n2b, httpServletRequest,channelCode);
      MetaData metaData = metaDataUtil.populateMetaDataBeforeSession(parameters);
      HttpHeaders httpHeaders = RestUtil.buildRequestHeaders(lang, httpServletRequest.getRemoteAddr());

      EsoCreateApplicationResponse esoCreateApplicationResponse = applicationEventRestHandler.makeCreateApplicationEventCall(httpHeaders, parameters, metaData);

      sessionUtil.createSession(httpServletRequest, httpServletResponse, lang, esoCreateApplicationResponse, metaData);

      String eoInvesting = httpServletRequest.getContextPath() + "/";

      if(ExperienceConstants.FR_CA.equals(lang)) {
        if(!appRootFr.isEmpty()) {
          appURLRoot = appRootFr  + eoInvesting;
        }
      }
      else
      {
        if(!appRootEn.isEmpty()) {
          appURLRoot = appRootEn + eoInvesting;
        }
      }

      if((targetURL == null)) {
        if(ExperienceConstants.CHANNEL_SELF_SERVE_WEB.equalsIgnoreCase(channelCode)) {
          if (n2b != null) {
            return Response.seeOther(URI.create(appURLRoot + n2bUrl)).build();
          }
          return Response.seeOther(URI.create(appURLRoot + existingClientUrl)).build();
        }
        else //Branch Flow will come here.  When Phone Channel is available then Phone Channel specific logic would be added.
        {

//            String oAuthToken = ClientCredentialsUtil.getClientCredentials();
//            sessionUtil.authenticateN2BSession(oAuthToken, httpServletRequest);
//            Response rsEnvironmentJs = environmentJSController.dropdown(httpServletRequest,lang);
//            Response rs = getStartedController.retrieveCustomerProfile(httpServletRequest);
          return Response.seeOther(URI.create(appURLRoot.replace("v1/","") + "v1/uap/callback?sso=true")).build();
//          return Response.seeOther(URI.create(appURLRoot + ssoBranchLaunchURL)).build();
        }

      } else {
        return Response.seeOther(URI.create(appURLRoot + targetURL)).build();
      }


    } catch(ApiException e) {
      logger.error("Create application event call throws API exception: {}", e.getStatus());

      sessionUtil.createSession(httpServletRequest, httpServletResponse, lang, null, null);

      return Response.seeOther(URI.create(appURLRoot + "#/error/" + ExceptionUtil.getErrorAdditionalStatusCode(e))).build();

    } catch(Exception e) {
      logger.error("Create application event call throws exception.", e);

      return Response.seeOther(URI.create(appURLRoot + "#/error/9200")).build();
    }
  }

  public Response processWelcomeFromSAML(HttpServletRequest httpServletRequest,
                                         HttpServletResponse httpServletResponse,
                                         String lang,
                                         String marketingCode,
                                         String sourceCode,
                                         String channelCode,
                                         String sourceUrl,
                                         List<String> productIds,
                                         String n2b,
                                         String targetURL) throws ApiException {
    if(channelCode==null){
      channelCode= ExperienceConstants.CHANNEL_SELF_SERVE_WEB;
    }
    String appURLRoot = "../";

    logger.debug("welcome({}, {}, {}), mkt: {}, src: {}, url: {}, samlassertion: {} ", lang, marketingCode, sourceCode, sourceUrl, productIds, n2b, targetURL, channelCode);
    LoginInfo loginInfo = null;
    lang = ExperienceConstants.FR_CA.equals(lang) ? ExperienceConstants.FR_CA : ExperienceConstants.EN_CA;

    try {
      SamlRequestData samlData = getSAMLData(httpServletRequest);
      //lang = samlData.getLang();
      String oAuthToken = ClientCredentialsUtil.getCredentialTokenFromSAML(samlData.getSamlToken());

      Map<String, Object> parameters = validationUtil.validateWelcomeParameters(lang, syntheticProductId, productIds, marketingCode, sourceCode, sourceUrl, n2b, httpServletRequest,channelCode);
      MetaData metaData = metaDataUtil.populateMetaDataBeforeSession(parameters);
      metaData.setInboundSamlCode(samlData.getSamlToken());
      metaData.setValetKey(samlData.getValetKey());
      HttpHeaders httpHeaders = RestUtil.buildRequestHeaders(lang, httpServletRequest.getRemoteAddr());
      httpHeaders.add(SAML_OAUTH_TOKEN, oAuthToken);
      httpServletRequest.setAttribute(SAML_OAUTH_TOKEN, oAuthToken);
      EsoCreateApplicationResponse esoCreateApplicationResponse = applicationEventRestHandler.makeCreateApplicationEventCall(httpHeaders, parameters, metaData);
      EsoSession newSession = sessionUtil.createSession(httpServletRequest, httpServletResponse, lang, esoCreateApplicationResponse, metaData, hasSession);
      refreshSessionIdByUpdateCookie(httpServletRequest, httpServletResponse, newSession);
      sessionUtil.authenticateN2BSession(oAuthToken, httpServletRequest, newSession);
      //create event
      EsoJsonData esoJsonData = sessionUtil.getSessionData(httpServletRequest, newSession);
      String clientIPAddress = httpServletRequest.getRemoteAddr();
      String acf2Id = sessionUtil.getConnectIdFromSession(httpServletRequest, newSession);
      metaData.setAgentUserId(acf2Id);
      String costCenter = (String) httpServletRequest.getAttribute(SessionUtil.COST_CENTER);
      loginInfo = getLoginInfo(acf2Id, "Remote IP: " + clientIPAddress);
      if(metaData.getValetKey()!=null && !metaData.getValetKey().isEmpty())
      {
        loginInfo.setSystemName(SYSTEM_TYPE_C3);
      }
      else
      {
          loginInfo.setSystemName(SYSTEM_TYPE_WD);
      }
      setC3MessageCookie(httpServletResponse, lang);
      metaData.setSystemName(loginInfo.getSystemName());
      metaData = metaDataUtil.populateMetaData(null, esoJsonData, SSO_LOGIN_STAGE);
      uAPFacade.createEvent(httpHeaders, metaData, ExperienceConstants.EVENT_STATUS_SUCCESS, loginInfo);
      ReferringAgent ra = new ReferringAgent();
      ra.setAcf2id(acf2Id);
      if (costCenter != null) {
        ra.setTransitNumber(costCenter);
      }
      if (FeatureToggle.ASSOCIATE_ORGANIZATION_ENABLE()) {
        if (StringUtils.isNotBlank(associateTestingUserId)) {
          ra.setAcf2id(associateTestingUserId);
        }
        ra = completeReferringAgent(metaData, httpHeaders, ra);
      }
      if (ra != null) {
        httpServletRequest.setAttribute(WealthConstants.REFERRING_AGENT, new ObjectMapper().writeValueAsString(ra));
      }
      //Need to initialize the reference data first before retrieve customer profile call.
      httpServletRequest.setAttribute(SAML_DROP_DOWN_CALL_ONCE, newSession);
      Response rsEnvironmentJs = environmentJSController.dropdown(httpServletRequest, lang);
      Response rs = getStartedController.retrieveCustomerProfile(httpServletRequest, newSession);
      WealthClientMasterInfo wcm = (WealthClientMasterInfo) rs.getEntity();
      LeftNavHelper leftNavHelper = new LeftNavHelper();
      leftNavHelper.updateLeftNav(wcm,  WealthConstants.GET_STARTED, null);

      if (StringUtils.isBlank(wcm.getApplicationInfo().getReferringAgent().getAcf2id())) {
        wcm.getApplicationInfo().setReferringAgent(ra);
      }
      wcm.getUserIdentity().setSourceApplication(loginInfo.getSystemName());
      sessionUtil.setDraftClientProfile(httpServletRequest, wcm, newSession);

    } catch(ApiException e) {
      logger.error("Create application event call throws API exception: {}", e.getMessage() + ", " + e.getStatus());

      sessionUtil.createSession(httpServletRequest, httpServletResponse, lang , null, null);

      return Response.seeOther(URI.create(appURLRoot + "#/error/" + ExceptionUtil.getErrorAdditionalStatusCode(e))).build();

    } catch(Exception e) {
      logger.error("Create application event call throws exception.", e);
      return Response.seeOther(URI.create(appURLRoot + "#/error/9200")).build();
    }
    return Response.seeOther(URI.create(appURLRoot + "#/start")).build();
  }

  public ReferringAgent completeReferringAgent(MetaData metaData, HttpHeaders httpHeaders, ReferringAgent referringAgent) throws ApiException {
    if (referringAgent.getTransitNumber() == null) {
      AssociateOrganizations organizations = getStartedFacade.getAssociateOrganizationsResponse(metaData, httpHeaders, WealthConstants.DEFAULT_USER_TYPE, referringAgent.getAcf2id());
      if (organizations == null || CollectionUtils.isEmpty(organizations.getRelatedInternalOrganizations())) {
        Status status = new Status();
        status.setSeverity(Severity.Error);
        status.setServerStatusCode("500");
        AdditionalStatus as = new AdditionalStatus();
        as.setServerStatusCode("500");
        as.setSeverity(AdditionalSeverity.Error);
        as.setStatusCode(0);
        as.setStatusDesc( "acf2id: " + referringAgent.getAcf2id() + " has no organization, this should neven happen.");
        AdditionalStatus[] ases = {as};
        status.setAdditionalStatus(ases);
        //uAPFacade.createEvent(httpHeaders, metaData, ExperienceConstants.EVENT_STATUS_FAIL, loginInfo);
        throw new ApiException(status);
      }
      referringAgent.setTransitNumber(organizations.getRelatedInternalOrganizations().get(0).getInternalOrganization().getOrganizationId());
    }
    Associates associates = getStartedFacade.getAssociateResponse(metaData, httpHeaders, WealthConstants.DEFAULT_USER_TYPE, referringAgent.getAcf2id());
    Associate associate = associates.getAssociate();
    if(associate != null ) {
      if (associate.getName() != null) {
        if (associate.getName().size() > 0) {
          referringAgent.setFirstName(associate.getName().get(0).getGivenName());
          referringAgent.setLastName(associate.getName().get(0).getFamilyName());
            }
          }
      }
    return referringAgent;
  }

  public LoginInfo getLoginInfo(String acf2Id, String deviceInfo) {
    LoginInfo loginInfo;
    loginInfo = new LoginInfo();
    loginInfo.setSystemName(SYSTEM_TYPE_WD);
    DevicePrintVal devicePrint = new DevicePrintVal();
    loginInfo.setDeviceInfo(devicePrint);
    loginInfo.setLoginId(acf2Id);
    loginInfo.setLoginStatus(EVENT_STATUS_SUCCESS);
    loginInfo.setEventName(ExperienceConstants.EVENT_TYPE_EMPLOYEE_SSO_LOGIN);
    DevicePrintVal dpv = new DevicePrintVal();
    dpv.setDevicePrintVal(deviceInfo);
    loginInfo.setDeviceInfo(dpv);
    return loginInfo;
  }

  public void refreshSessionIdByUpdateCookie(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, EsoSession newSession) {
    Cookie[] cookies = httpServletRequest.getCookies();
    if (cookies != null) {
      Optional<Cookie> appCookieMaybe = Arrays.stream(cookies).filter(cookie -> appCookieName.equals(cookie.getName())).findFirst();
      if (appCookieMaybe.isPresent()) {
        Cookie appCookie = appCookieMaybe.get();
        //Update the existing cookie.
        appCookie.setPath("/");
        appCookie.setValue(newSession.getSessionId());
        httpServletResponse.addCookie(appCookie);
      }
    }
  }

  public void setC3MessageCookie(HttpServletResponse httpServletResponse, String lang) {
      try {
        String externalMessage = "";
        if(ExperienceConstants.EN_CA.equalsIgnoreCase(lang)){
          externalMessage = ExperienceConstants.C3_EXTERNAL_APP_MESSAGE_EN;
        }
        if(ExperienceConstants.FR_CA.equalsIgnoreCase(lang)) {
          externalMessage = ExperienceConstants.C3_EXTERNAL_APP_MESSAGE_FR;
        }
        Cookie c3Cookie = new Cookie(ExperienceConstants.C3_EXTERNAL_APP_COOKIE, URLEncoder.encode(externalMessage, "UTF-8").replaceAll("\\+", "%20"));
        c3Cookie.setPath("/");
        c3Cookie.setDomain(".tdgroup.com");
        httpServletResponse.addCookie(c3Cookie);
      } catch (Exception e){
        logger.error("Error setting C3 cookie : {}", e.getMessage());
      }
  }

  public SamlRequestData getSAMLData(HttpServletRequest httpServletRequest) throws ApiException {
    SamlRequestData samlData = new SamlRequestData();
    try {
      String samlToken = "";
      if(httpServletRequest.getParameter(SAML_RESPONSE_TAG_NAME) != null) {
        samlToken = httpServletRequest.getParameter(SAML_RESPONSE_TAG_NAME);
        logger.info("WelcomeFacade :: getSAMLData :: Setting SAML token ");
      }
      samlData.setSamlToken(samlToken);

      String payload = "";
      if(httpServletRequest.getParameter(PAYLOAD_TAG_NAME) != null) {
        payload = httpServletRequest.getParameter(PAYLOAD_TAG_NAME);
        logger.info("WelcomeFacade :: getSAMLData :: Setting valet key payload");
      }
      samlData.setValetKey(payload);

    }catch(Exception e) {
      Status status = new Status();
      status.setSeverity(Severity.Error);
      status.setServerStatusCode(SERVER_ERROR_STATUS_CODE);
      AdditionalStatus as = new AdditionalStatus();
      as.setStatusDesc(e.getMessage());
      as.setSeverity(AdditionalSeverity.Error);
      logger.error("Error setting SAML token : ", e.getMessage());
      throw new ApiException(status);
    }
    return samlData;
  }
}
