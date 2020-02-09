package com.td.dcts.eso.experience.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.td.coreapi.common.config.ApiConfig;
import com.td.coreapi.common.oauthsdk.model.*;
import com.td.coreapi.common.oauthsdk.service.OAuthSDKService;
import com.td.coreapi.common.status.ApiConfigException;
import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.event.response.model.SubApplicationInfo;
import com.td.dcts.eso.events.createevent.request.EsoCreateEventRequest;
import com.td.dcts.eso.events.createevent.request.Event;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.helper.EventHelper;
import com.td.dcts.eso.experience.model.LoginInfo;
import com.td.dcts.eso.experience.model.response.MetaData;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.*;

import static com.td.dcts.eso.experience.constants.ExperienceConstants.FR_CA;


@Component
public class UAPFacade {

  @Autowired
  private EventHelper eventHelper;

  public HashMap<String, Object> getConfig(String locale) throws ApiException {

    HashMap<String, Object> configResponse = new HashMap<String, Object>();

    Map<String, Object> mapJSONConfig = ApiConfig.getInstance().getJsonMapConfig(OAuthConstants.CONFIG_FILE_NAME);
    Properties envProperties = ApiConfig.getInstance().getPropertiesConfig(ExperienceConstants.ENV_FILE_NAME);

    Map<String, Object> oauthRequest = (Map<String, Object>) mapJSONConfig.get("oauth_request_1");
    Map<String, Object> clientCredGrant = (Map<String, Object>) oauthRequest.get(OAuthConstants.JSON_CLIENT_CREDENTIALS_GRANT_NAME);
    Map<String, Object> authCredGrant =     ((Map<String, Object>) oauthRequest.get(OAuthConstants.JSON_AUTHORIZATION_CODE_GRANT_NAME));

    //pingDomain should come from Configs as the address would be different for Internal Token Generation and External Redirect
    String pingDomain = (String) envProperties.getProperty(ExperienceConstants.UAP_DOMAIN); //(String) oauthRequest.get(OAuthConstants.AUTHENTICATION_SERVER_URL); //"https://authentication.dev.td.com";//
    String clientId = (String) clientCredGrant.get(OAuthConstants.CLIENT_ID);//"33d3c0d0-e34f-4260-9755-1bc95fd527c6";
    ArrayList scopes = (ArrayList) clientCredGrant.get(OAuthConstants.SCOPE);
//    String redirectURI = (String) authCredGrant.get(ExperienceConstants.REDIRECT_URI);

//    String scopes = "idm.cad.uid.w idm.cad.uid.r enr.tdw.prdc.r enr.tdw.prdc.w docg.hpeews.docg.w docm.fn.repo.w prts.wca.prts.r prts.cif.prts.r cb.cbs.acr.r cb.cbs.acr.w docm.fn.repo.r fapp.eoae.fapp.w fapp.eoae.fapp.r prts.wca.prts.w inva.wca.inva.r";

    //String consumerID = isResume;// ? envProperties.getProperty(ExperienceConstants.CONSUMER_ID_RESUME) : envProperties.getProperty(ExperienceConstants.CONSUMER_ID);

    configResponse.put("pingDomain", pingDomain);
    configResponse.put("clientId", clientId);
    configResponse.put("scopes",  StringUtils.join(scopes.toArray(), " "));
    configResponse.put("responseType", (String) envProperties.getProperty(ExperienceConstants.RESPONSE_TYPE));
    configResponse.put("idpAdapter", envProperties.getProperty(ExperienceConstants.IDP_ADAPTER));
    configResponse.put("tsnConsumerAppId", envProperties.getProperty(ExperienceConstants.CONSUMER_APP_ID));
if (FR_CA.equals(locale)) {
  configResponse.put("redirect_uri", envProperties.getProperty(ExperienceConstants.REDIRECT_URI_FR));
}
else
{
  configResponse.put("redirect_uri", envProperties.getProperty(ExperienceConstants.REDIRECT_URI));
}

    configResponse.put("uapDomain", envProperties.getProperty(ExperienceConstants.INITIAL_UAP_COOKIE_DOMAIN));
   //INITIAL_UAP_COOKIE_DOMAIN goes to uapDomain which is used to setup Cookie
    configResponse.put("consumerId", envProperties.getProperty(ExperienceConstants.CONSUMER_ID));
    configResponse.put("nonce", UUID.randomUUID().toString());
    configResponse.put("lang", locale);

    return configResponse;
  }

  public void createEvent(MultiValueMap<String, String> httpHeaders, MetaData metaData, String eventStatus, LoginInfo loginInfo, String... organizationEvent) throws ApiException, JsonProcessingException {

    EsoCreateEventRequest esoCreateEventRequest = new EsoCreateEventRequest();

    String subAppId = null;
    List<SubApplicationInfo> subAppIDs = metaData.getSubApplicationList();
    if(subAppIDs != null) {
      if(!subAppIDs.isEmpty()) {
        subAppId = subAppIDs.get(0).getSubApplicationId();
      }
    }

    Event event = new Event();
    event.setApplicationId(metaData.getApplicationId().toString());
    event.setSubApplicationId(subAppId);
    event.setProductId(metaData.getProductId());
    if (loginInfo != null) {
      if (loginInfo.getEventName() != null) {
        event.setEventTypeCD(loginInfo.getEventName());
      } else {
        event.setEventTypeCD(ExperienceConstants.EVENT_TYPE_LOGIN);
      }
    } else if (organizationEvent != null && organizationEvent.length == 1) {
      event.setEventTypeCD(organizationEvent[0]);
    }
    event.setEventStatus(eventStatus);
    event.setBusinessOutcomeCD(ExperienceConstants.DEFAULT_BUSINESS_OUTCOME);
    if (loginInfo != null) {
      event.setEventMetaDataJSON(new ObjectMapper().writeValueAsString(loginInfo).toString());
    }
    esoCreateEventRequest.setEvent(event);
    eventHelper.logEvent(esoCreateEventRequest,httpHeaders,metaData);

  }

  public OAuthResponse getOAuthToken(String code, String redirectUri) throws ApiException, IOException, ApiConfigException {
  OAuthRequest request;
  request = new OAuthRequestBuilder().withAuthorizationGrantRequest().endAuthorizationGrantRequest().build();
    request.getAuthorizationGrantRequest().setRedirectUri(redirectUri);
  OAuthAuthorizationGrantRequest grantRequest = request.getAuthorizationGrantRequest();
  grantRequest.setAuthCode(code);
  request.setAuthorizationGrantRequest(grantRequest);
  OAuthResponse authResponse = new OAuthSDKService().getToken(request);
  return authResponse;
}


}
