package com.td.dcts.eso.experience.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.td.coreapi.common.security.AccessTokenClaims;
import com.td.coreapi.common.security.JwtHelper;
import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.event.response.model.SubApplicationInfo;
import com.td.dcts.eso.events.createapplication.response.EsoCreateApplicationResponse;
import com.td.dcts.eso.events.createapplication.response.SubApplication;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.facade.ContentFacade;
import com.td.dcts.eso.experience.model.ExternalState;
import com.td.dcts.eso.experience.model.response.*;
import com.td.dcts.eso.session.model.EsoJsonData;
import com.td.dcts.eso.session.model.EsoOAuthData;
import com.td.dcts.eso.session.model.EsoSession;
import com.td.dcts.eso.session.service.IESOSessionService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SessionUtil {

	static final XLogger logger = XLoggerFactory.getXLogger(SessionUtil.class);

	public static final String SESSION_KEY_LOCALE = "locale";
  public static final String DI_EXTERNAL_REF_NUMBER = "diExternalRefNumber";
  public static final String DI_INBOUND_SAML_CODE = "diInboundSamlCode";
  public static final String VALET_KEY = "valetKeyFromC3";
  public static final String SESSION_KEY_DEVICE_PRINT_VAL = "devicePrintVal";
  public static final String TC_CONSENT_CAPTURED = "tcConsentCaptures";
  public static final String SESSION_KEY_OVERRIDE_VALUES = "overrideValues";
  public static final String SESSION_KEY_APPLICATION_ID = "applicationId";
	public static final String SESSION_KEY_SUBAPPLICATION_IDS = "subApplicationIds";
	public static final String SESSION_KEY_PRODUCT_ID = "productId";
	public static final String SESSION_KEY_PRODUCTS = "products";
	public static final String SESSION_KEY_IDCAPTURE_VERIFICATION = "idCaptureVerificationKey";
	//public static final String SESSION_KEY_CREDIT_REFRENCE_NUMBER = "creditRefNumber";
	public static final String SESSION_KEY_PARTY_ID = "partyId";
	public static final String SESSION_KEY_FLOW_ID = "flowId";
	public static final String SESSION_KEY_SESSION_ID = "sessionId";
	public static final String SESSION_KEY_FIRSTNAME = "firstname";
	public static final String SESSION_KEY_LASTNAME = "lastname";
	public static final String SESSION_KEY_CHANNEL = "channel";
	public static final String SESSION_KEY_CONNECT_ID = "connectId";
	public static final String SESSION_KEY_ORIGINAL_CLIENT_PROFILE = "clientProfile";
	public static final String SESSION_KEY_DRAFT_CLIENT_PROFILE = "draftClientProfile";
  public static final String SESSION_KEY_CURRENT_WOT_APP_STATE = "currentWotAppState";
	public static final String SESSION_KEY_IDP_QUESTIONS = "idpQuestions";
	public static final String SESSION_KEY_IDP_QUESTION_SENT_TIME = "idpQuestionSentTime";
	public static final String SESSION_KEY_DEFAULT_CHANNEL_ID = "web";
  public static final String SESSION_KEY_CHANNEL_ID = "channelId";
	public static final String FLOW_ID_EW = "EW";
  public static final String FLOW_ID_N2B = "N2B";
	public static final String OAUTH_TOKEN_KEY = "OAUTH_TOKEN_KEY";
  public static final int ERR_INVALID_JWT_TOKEN = 9702;
	public static final String ERR_INVALID_JWT_TOKEN_MSG = "Unexpected error when trying to inspect the contents of the JWT token";
  public static final String COST_CENTER = "CostCenter";


  @Autowired
	private IESOSessionService iEsoSessionService;

	@Autowired
  private ContentFacade contentFacade;

	private ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	public EsoSession createSession(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			String locale, EsoCreateApplicationResponse esoCreateApplicationResponse, MetaData metaData, Boolean... hasSession) throws ApiException {
    EsoSession esoSession = null;
    //To work around the second time add cookie value not overwrite the existing one issue just for happy path testing.
    boolean needNewSession = ! (hasSession != null && hasSession.length ==1 && hasSession[0]);
	  if (!needNewSession) {
      esoSession = iEsoSessionService.getSession(httpServletRequest);
    } else {
      esoSession = iEsoSessionService.createSession();
    }

		EsoJsonData esoJsonData = createEsoJsonData(locale, metaData, esoCreateApplicationResponse, esoSession);

    // create override map and set in eso json data object.
    contentFacade.createDefaultOverrideMap(esoJsonData, esoJsonData.get(SESSION_KEY_CHANNEL).toString());

		iEsoSessionService.setJsonDataAttribute(esoSession.getSessionId(), esoJsonData);

    if (needNewSession) {
      iEsoSessionService.addCookieToResponse(httpServletRequest, httpServletResponse, esoSession);
    }

		logger.debug("Session created for application ID [{}], product ID [{}]: {}",
				esoJsonData.get(SESSION_KEY_APPLICATION_ID), esoJsonData.get(SESSION_KEY_PRODUCT_ID), esoSession.getSessionId());
		return esoSession;
	}

	private EsoJsonData createEsoJsonData(String locale, MetaData metaData, EsoCreateApplicationResponse esoCreateApplicationResponse, EsoSession session) {
		EsoJsonData esoJsonData = new EsoJsonData();
		esoJsonData.put(SESSION_KEY_LOCALE, locale);
		if (esoCreateApplicationResponse != null && esoCreateApplicationResponse.getApplication() != null) {
      esoJsonData.put(DI_EXTERNAL_REF_NUMBER, esoCreateApplicationResponse.getApplication().getReferenceNumber());
    }
    esoJsonData.put(DI_INBOUND_SAML_CODE, metaData.getInboundSamlCode());
    esoJsonData.put(SESSION_KEY_PARTY_ID, metaData.getPrimaryPartyId());
    esoJsonData.put(VALET_KEY, metaData.getValetKey());

		if (metaData != null) {
			esoJsonData.put(SESSION_KEY_CHANNEL, metaData.getChannel());
			List<String> products = new ArrayList<String>();
			for (SubApplicationInfo subApp: metaData.getSubApplicationList()) {
				products.add(subApp.getProductId());
			}
			esoJsonData.put(SESSION_KEY_PRODUCTS, products); // store multi-products. Only basic Java Type should be stored in EsoJsonData or else it will cause ClassNotFoundException when EsoJsonData is deserialized in core-login-app
		}
		esoJsonData.put(SESSION_KEY_SESSION_ID, session.getSessionId());
		if (esoCreateApplicationResponse != null) {
			esoJsonData.put(SESSION_KEY_APPLICATION_ID, esoCreateApplicationResponse.getApplication().getApplicationId());
			List<Long> subAppIds = new ArrayList<Long>();
			for (SubApplication subApplication : esoCreateApplicationResponse.getApplication().getSubApplications()) { // as of Aug 23, 2017, it always return 1 sub application
				esoJsonData.put(SESSION_KEY_PRODUCT_ID, subApplication.getProductId());
				subAppIds.add(Long.parseLong(subApplication.getSubApplicationId()));
			}
			esoJsonData.put(SESSION_KEY_SUBAPPLICATION_IDS, subAppIds);
		} else {
			// it should not be null normally
		}
		return esoJsonData;
	}

  public void authenticateEWSession(String oauthToken, HttpServletRequest httpServletRequest) throws ApiException {
    EsoSession esoSession = getSession(httpServletRequest);

    // get the connectId from the jwt token
    EsoOAuthData EsoOAuthData = getEsoOAuthData(oauthToken, httpServletRequest);

    // add oauth data to session
    iEsoSessionService.setOAuthDataAttribute(esoSession.getSessionId(), EsoOAuthData);

    // add flow Id to session
    EsoJsonData esoJsonData = iEsoSessionService.getJsonDataAttribute(esoSession.getSessionId());
    esoJsonData.put(SessionUtil.SESSION_KEY_FLOW_ID, SessionUtil.FLOW_ID_EW);
    iEsoSessionService.setJsonDataAttribute(esoSession.getSessionId(), esoJsonData);
  }
/*
	public void createEwSession(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			String locale, String channel, CreateApplicationResponse application, String oauthToken) throws ApiException {

		EsoOAuthData oAuthData = getEsoOAuthData(oauthToken, httpServletRequest);
		EsoSession session = iEsoSessionService.createSession(oAuthData);

		EsoJsonData esoJsonData = createEsoJsonData(locale, metaData, application, session);
		esoJsonData.put(SESSION_KEY_FLOW_ID, FLOW_ID_EW);
		iEsoSessionService.setJsonDataAttribute(session.getSessionId(), esoJsonData);

		iEsoSessionService.addCookieToResponse(httpServletRequest, httpServletResponse, session);
	}
*/
	private EsoOAuthData getEsoOAuthData(String oauthToken, HttpServletRequest httpServletRequest) throws ApiException {
		EsoOAuthData esoOAuthData = new EsoOAuthData();
		AccessTokenClaims jwtClaims = null;
		try {
			jwtClaims = JwtHelper.verifyJwt(oauthToken);
			String userId = jwtClaims.getUserId();
			if (jwtClaims.getCc() != null) {
        httpServletRequest.setAttribute(COST_CENTER, jwtClaims.getCc());
      }
			esoOAuthData.setConnectId(userId);
			esoOAuthData.setToken(oauthToken);
			esoOAuthData.setExpiry((long) jwtClaims.getExp());
		} catch (Exception e) {
			throw new ApiException(ExceptionUtil.buildErrorStatus(ERR_INVALID_JWT_TOKEN, ERR_INVALID_JWT_TOKEN_MSG));
		}
		return esoOAuthData;
	}

	public void addEwUserNameToSession(EsoJsonData esoJsonData, CustomerProfileResponse response) throws ApiException {
		esoJsonData.put(SessionUtil.SESSION_KEY_FIRSTNAME, response.getFirstName());
		esoJsonData.put(SessionUtil.SESSION_KEY_LASTNAME, response.getLastName());
		esoJsonData.put(SessionUtil.SESSION_KEY_PARTY_ID, response.getPartyID());
		iEsoSessionService.setJsonDataAttribute((String) esoJsonData.get(SESSION_KEY_SESSION_ID), esoJsonData);
	}
	/*
	public void addPartyIdAndCreditRefNumToSession(EsoJsonData esoJsonData, ApplicationManagementRestResponse response) throws ApiException {
		esoJsonData.put(SessionUtil.SESSION_KEY_CREDIT_REFRENCE_NUMBER, response.getCreditReferenceNumber());
		esoJsonData.put(SessionUtil.SESSION_KEY_PARTY_ID, response.getPartyID());
		iEsoSessionService.setJsonDataAttribute((String) esoJsonData.get(SESSION_KEY_SESSION_ID), esoJsonData);
	}
	*/
	public EsoJsonData setToSession(HttpServletRequest httpServletRequest, String sessionKey, Object value, EsoSession... esoSession) throws ApiException {
		EsoJsonData esoJsonData = getSessionData(httpServletRequest, esoSession);
		esoJsonData.put(sessionKey,value);
		return esoJsonData;
	}

	public EsoJsonData setToSession(HttpServletRequest httpServletRequest, String sessionKey, Object value, boolean persist, EsoSession... newEsoSession) throws ApiException {
		EsoJsonData esoJsonData = setToSession(httpServletRequest, sessionKey, value, newEsoSession);
		if (persist) {
			iEsoSessionService.setJsonDataAttribute((String) esoJsonData.get(SESSION_KEY_SESSION_ID), esoJsonData);
		}
		return esoJsonData;
	}

	public Object getFromSession(HttpServletRequest httpServletRequest, String sessionKey, EsoSession... newEsoSession) throws ApiException {
		EsoJsonData esoJsonData = getSessionData(httpServletRequest, newEsoSession);
		Object sessionData = esoJsonData.get(sessionKey);
		return sessionData;
	}

	public EsoJsonData getSessionData(HttpServletRequest httpServletRequest, EsoSession... newEsoSession) throws ApiException {
		logger.entry("getSessionData http started");
		String esoSessionId = iEsoSessionService.getSessionId(httpServletRequest);
		if (esoSessionId == null && newEsoSession != null && newEsoSession.length == 1 && newEsoSession[0] != null) {
		  esoSessionId = newEsoSession[0].getSessionId();
    }
		EsoJsonData esoJsonData = iEsoSessionService.getJsonDataAttribute(esoSessionId);
		logger.exit("getSessionData http finished");
		if (esoJsonData == null) {
			throw new ApiException(ExceptionUtil.buildErrorStatus(403, "Session expired"));
		}
		return esoJsonData;
	}

	public void destroySession(HttpServletRequest httpServletRequest) throws ApiException {
		String esoSessionId = iEsoSessionService.getSessionId(httpServletRequest);
		iEsoSessionService.destroySession(esoSessionId);

		logger.debug("Session destroyed through logout: {}", esoSessionId);
	}

	public EsoSession getSession(HttpServletRequest httpServletRequest, EsoSession... newEsoSession) throws ApiException {
		logger.entry("getSession(httpSR) started");
		EsoSession esoSession = iEsoSessionService.getSession(httpServletRequest);
		if (esoSession == null) {
		  if (newEsoSession != null && newEsoSession.length == 1 && newEsoSession[0] != null) {
		    esoSession = newEsoSession[0];
      } else {
        throw new ApiException(ExceptionUtil.buildErrorStatus(403, "Session expired"));
      }
		}
    logger.exit("getSession(httpSR) finished");
		return esoSession;
	}

	public void authenticateN2BSession(String oauthToken, HttpServletRequest httpServletRequest, EsoSession... firstTimeEsoSession) throws ApiException {
    EsoSession esoSession = getSession(httpServletRequest, firstTimeEsoSession);
    EsoOAuthData EsoOAuthData = getEsoOAuthData(oauthToken, httpServletRequest);
    // add oauth data to session
    iEsoSessionService.setOAuthDataAttribute(esoSession.getSessionId(), EsoOAuthData);

    // add flow Id to session
    EsoJsonData esoJsonData = iEsoSessionService.getJsonDataAttribute(esoSession.getSessionId());
    esoJsonData.put(SessionUtil.SESSION_KEY_FLOW_ID, SessionUtil.FLOW_ID_N2B);
    iEsoSessionService.setJsonDataAttribute(esoSession.getSessionId(), esoJsonData);
	}

	public String getConnectIdFromSession(HttpServletRequest httpServletRequest, EsoSession... newEsoSession) throws ApiException {
		logger.entry("getConnectIdFromSession(http) started") ;
		try {
			EsoSession esoSession = getSession(httpServletRequest, newEsoSession);
			logger.entry("getOAuthDataAttribute(http) started", esoSession.getSessionId()) ;
			EsoOAuthData esoOAuthData = iEsoSessionService.getOAuthDataAttribute(esoSession.getSessionId());
			return esoOAuthData.getConnectId();
		} catch(NullPointerException npe) {
			return null;
		} finally {
			logger.exit("getConnectIdFromSession(http) finished") ;
		}
	}

	public void setPartyIdToSession(Map<String, Object> map, HttpServletRequest httpServletRequest) {
		String sessionId = iEsoSessionService.getSessionId(httpServletRequest);
		EsoJsonData esoJsonData = iEsoSessionService.getJsonDataAttribute(sessionId);
		esoJsonData.put(ExperienceConstants.PARTY_ID_KEY, map.get("partyId"));
		iEsoSessionService.setJsonDataAttribute(sessionId, esoJsonData);
	}
	public EsoJsonData getJSONSessionData(HttpServletRequest httpServletRequest, EsoSession... newEsoSession) throws ApiException {
		EsoJsonData esoJsonData = null;
		String sessionId = iEsoSessionService.getSessionId(httpServletRequest);
		if (sessionId == null && newEsoSession.length > 0) {
		  sessionId = newEsoSession[0].getSessionId();
    }
		if (sessionId != null) {
			esoJsonData = iEsoSessionService.getJsonDataAttribute(sessionId);
			esoJsonData.put(ExperienceConstants.SESSION_ID_KEY, sessionId);
		}
		if (esoJsonData == null) {
			throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.FORBIDDEN.value(), "Session expired"), "Session expired");
		}
		return esoJsonData;
	}
	public void setCreditRefNumToSession(ResponseEntity<ApplicationManagementRestResponse> responseEntity, HttpServletRequest httpServletRequest) throws ApiException {
		String sessionId = iEsoSessionService.getSessionId(httpServletRequest);
		EsoJsonData esoJsonData = iEsoSessionService.getJsonDataAttribute(sessionId);
		ApplicationManagementRestResponse applicationManagementRestResponse = (ApplicationManagementRestResponse) responseEntity.getBody();
		esoJsonData.put(ExperienceConstants.CREDIT_REFRENCE_NUMBER_KEY, applicationManagementRestResponse.getCreditReferenceNumber());
		iEsoSessionService.setJsonDataAttribute(sessionId, esoJsonData);
	}

	public void addClientProfile(HttpServletRequest httpServletRequest, String profileData) throws ApiException {
		String sessionId = iEsoSessionService.getSessionId(httpServletRequest);
		EsoJsonData sessionData = getSessionData(httpServletRequest);
		sessionData.put(SESSION_KEY_ORIGINAL_CLIENT_PROFILE, profileData);
		iEsoSessionService.setJsonDataAttribute(sessionId, sessionData);
	}

	public void setDraftClientProfile(HttpServletRequest httpServletRequest, String profileData, EsoSession... newEsoSession) throws ApiException {
		logger.entry("setDraftClientProfile started");
		String sessionId = iEsoSessionService.getSessionId(httpServletRequest);
		if (sessionId == null && newEsoSession != null && newEsoSession.length == 1 && newEsoSession[0] != null) {
		  sessionId = newEsoSession[0].getSessionId();
    }
		EsoJsonData sessionData = getSessionData(httpServletRequest, newEsoSession);
		sessionData.put(SESSION_KEY_DRAFT_CLIENT_PROFILE, profileData);
		iEsoSessionService.setJsonDataAttribute(sessionId, sessionData);
		logger.exit("setDraftClientProfile finished");
	}

  public String getCurrentAppState(HttpServletRequest httpServletRequest) throws ApiException, JsonProcessingException {
    logger.entry("getCurrentState started");
    EsoJsonData sessionData = getSessionData(httpServletRequest);
    Object o = sessionData.get(SESSION_KEY_CURRENT_WOT_APP_STATE);
    logger.exit("getCurrentState finished");
    if (o != null && o instanceof String) {
      return (String)o;
    } else {
      ExternalState externalState = new ExternalState();
      return new ObjectMapper().writeValueAsString(externalState);
    }
  }


  public String getDraftClientProfile(HttpServletRequest httpServletRequest) throws ApiException {
		logger.entry("getDraftClientProfile started");
		EsoJsonData sessionData = getSessionData(httpServletRequest);
		Object o = sessionData.get(SESSION_KEY_DRAFT_CLIENT_PROFILE);
		logger.exit("getDraftClientProfile finished");
		if (o != null && o instanceof String) {
			return (String)o;
		} else {
			throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.INTERNAL_SERVER_ERROR.value(), "No draft client profile data found"), "No draft client profile data found");
		}
	}

	public String getOriginalClientProfile(HttpServletRequest httpServletRequest) throws ApiException {
		EsoJsonData sessionData = getSessionData(httpServletRequest);
		Object o = sessionData.get(SESSION_KEY_ORIGINAL_CLIENT_PROFILE);
		if (o != null && o instanceof String) {
			return (String)o;
		} else {
			throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.INTERNAL_SERVER_ERROR.value(), "No client profile data found"), "No client profile data found");
		}
	}

	public void setDraftClientProfile(HttpServletRequest httpServletRequest, WealthClientMasterInfo profileData, EsoSession... newEsoSession) throws ApiException {
		try {
      setDraftClientProfile(httpServletRequest, objectMapper.writeValueAsString(profileData), newEsoSession);
      String sessionId = iEsoSessionService.getSessionId(httpServletRequest);
      if (sessionId == null && newEsoSession != null && newEsoSession.length == 1 && newEsoSession[0] != null) {
        sessionId = newEsoSession[0].getSessionId();
      }
      EsoJsonData sessionData = getSessionData(httpServletRequest, newEsoSession);
      ExternalState externalState = new ExternalState();
      if (profileData.getLastApplicationState() != null) {
        externalState.setLastCard(profileData.getLastApplicationState().getCard());
        externalState.setLastState(profileData.getLastApplicationState().getState());
      }
      if ((profileData.getUserIdentity() != null)) {
        externalState.setUserType(profileData.getUserIdentity().getUserType());
      }
      sessionData.put(SESSION_KEY_CURRENT_WOT_APP_STATE, objectMapper.writeValueAsString(externalState));
      iEsoSessionService.setJsonDataAttribute(sessionId, sessionData);
    } catch (JsonProcessingException e) {
			logger.error("Error converting WealthClientMasterInfo to JSON String", e);
			throw new ApiException(ExceptionUtil.buildServerErrorStatus());
		}
	}

	public boolean isSessionValid(HttpServletRequest httpServletRequest){
    return iEsoSessionService.isSessionValid(httpServletRequest);
  }


  public boolean isSessionAlive(HttpServletRequest httpServletRequest){
    return iEsoSessionService.isSessionAlive(httpServletRequest);
  }

  public void storeInSession(HttpServletRequest httpServletRequest, String data,String key) throws ApiException {

	  logger.entry("Storing In Session started for key "+key);


    String sessionId = iEsoSessionService.getSessionId(httpServletRequest);
    EsoJsonData sessionData = getSessionData(httpServletRequest);
    sessionData.put(key, data);
    iEsoSessionService.setJsonDataAttribute(sessionId, sessionData);

    logger.exit("Storing In Session started for key "+key);
  }

  public String retrieveFromSession(HttpServletRequest httpServletRequest,String key) throws ApiException {

	  logger.entry("retrieveFromSession started");

	  EsoJsonData sessionData = getSessionData(httpServletRequest);
    Object o = sessionData.get(key);

    logger.exit("retrieveFromSession finished");

    if (o != null && o instanceof String) {
      return (String)o;
    } else {
      throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.INTERNAL_SERVER_ERROR.value(), "No draft client profile data found"), "No draft client profile data found");
    }
  }
}
