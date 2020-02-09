package com.td.dcts.eso.experience;

import com.td.coreapi.common.config.ApiConfig;
import com.td.coreapi.common.oauthsdk.model.OAuthResponse;
import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.environmentjs.controller.EnvironmentJSController;
import com.td.dcts.eso.experience.facade.UAPFacade;
import com.td.dcts.eso.experience.model.DevicePrintVal;
import com.td.dcts.eso.experience.model.LoginInfo;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.dcts.eso.experience.util.*;
import com.td.dcts.eso.session.model.EsoJsonData;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Properties;

import static com.td.dcts.eso.experience.constants.ExperienceConstants.*;

@Controller
@Path("/uap")
public class UapController {

	static final XLogger logger = XLoggerFactory.getXLogger(UapController.class);

	@Autowired
	private SessionUtil sessionUtil;


  @Autowired
  private GetStartedController getStartedController;

  @Autowired
  private EnvironmentJSController environmentJSController;

  @Autowired
  private UAPFacade uAPFacade;

  @Autowired
  private MetaDataUtil metaDataUtil;

  @Value("${application.url.appRootUrlEn:}")
  private String appRootEn;

  @Value("${application.url.appRootUrlFr:}")
  private String appRootFr;

  @POST
	@Path("/config")
	@Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Response getConfig(@Context HttpServletRequest httpServletRequest, @RequestBody DevicePrintVal devicePrint) throws ApiException {

		String locale = httpServletRequest.getHeader(ExperienceConstants.HTTP_HEADER_LOCALE);
if(locale==null   || locale.isEmpty()) {
  EsoJsonData esoData = sessionUtil.getSessionData(httpServletRequest);
  locale = (String) esoData.get(SessionUtil.SESSION_KEY_LOCALE);
}
    HashMap<String, Object> configResponse = new HashMap<String, Object>();
    configResponse = uAPFacade.getConfig(locale);
    //sessionUtil.setToSession(httpServletRequest, SessionUtil.SESSION_KEY_DEVICE_PRINT_VAL, devicePrint.getDevicePrintVal().toString(), true);
		return Response.ok(configResponse, MediaType.APPLICATION_JSON).build();
	}


	@GET
	@Path("/callback")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response callbackRedirection(@Context HttpServletRequest httpServletRequest,
			@QueryParam("code") String code,
      @QueryParam("ext") String ext) throws ApiException, IOException {
    EsoJsonData esoJsonData = sessionUtil.getSessionData(httpServletRequest);
    String locale = (String) esoJsonData.get(SessionUtil.SESSION_KEY_LOCALE);


		try {
      String eoInvesting = httpServletRequest.getContextPath() + "/";
      String appRoot = "../";
      String redirectURI;
      Properties envProperties = ApiConfig.getInstance().getPropertiesConfig(ExperienceConstants.ENV_FILE_NAME);

      if (FR_CA.equals(locale)) {
        redirectURI = (String) envProperties.getProperty(ExperienceConstants.REDIRECT_URI_FR);
        if(!appRootFr.isEmpty()) {
          appRoot = appRootFr + eoInvesting;
        }
      }
      else
      {
        redirectURI = (String) envProperties.getProperty(ExperienceConstants.REDIRECT_URI);
        if(!appRootEn.isEmpty()) {
          appRoot = appRootEn + eoInvesting;
        }
      }

      if(code!=null) {
        String connectId = null;
        MetaData metaData = metaDataUtil.populateMetaData(connectId, esoJsonData, ExperienceConstants.GENERAL_STAGE);
        String clientIPAddress = httpServletRequest.getRemoteAddr();
        HttpHeaders httpHeaders = RestUtil.buildRequestHeaders(locale, clientIPAddress);
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setSystemName(CAA);
        DevicePrintVal devicePrint = new DevicePrintVal();
     //   devicePrint.setDevicePrintVal(sessionUtil.getFromSession(httpServletRequest, SessionUtil.SESSION_KEY_DEVICE_PRINT_VAL).toString());
        loginInfo.setDeviceInfo(devicePrint);

        try {
          OAuthResponse authResponse = uAPFacade.getOAuthToken(code, redirectURI);//
          sessionUtil.authenticateEWSession(authResponse.getAccessToken(), httpServletRequest);
          connectId  = sessionUtil.getConnectIdFromSession(httpServletRequest);
          loginInfo.setLoginId(connectId);
          loginInfo.setLoginStatus(EVENT_STATUS_SUCCESS);
          uAPFacade.createEvent(httpHeaders, metaData, ExperienceConstants.EVENT_STATUS_SUCCESS, loginInfo);
          Response rsEnvironmentJs = environmentJSController.dropdown(httpServletRequest, locale);
          Response rs = getStartedController.retrieveCustomerProfile(httpServletRequest);

//          return Response.seeOther(URI.create(appRoot + "#/start")).build();
//          return Response.seeOther(URI.create(appRoot + "td-eso-experience/src/index.html")).build();
          return Response.seeOther(URI.create(appRoot + "#/start")).build();

        }
        catch (Exception e){
          logger.error("Exception Occured while exchanging auth code for Oauth token", e);
          loginInfo.setLoginStatus(EVENT_STATUS_FAIL);
          uAPFacade.createEvent(httpHeaders, metaData,ExperienceConstants.EVENT_STATUS_FAIL, loginInfo);
          throw new ApiException(ExceptionUtil.buildServerErrorStatus(), e);
        }
      }
      else
      {
        //Currently Web-N2B and Branch-SSO comes to this Location
        String oAuthToken = ClientCredentialsUtil.getClientCredentials();
        sessionUtil.authenticateN2BSession(oAuthToken, httpServletRequest);
        Response rsEnvironmentJs = environmentJSController.dropdown(httpServletRequest,locale);
        Response rs = getStartedController.retrieveCustomerProfile(httpServletRequest);

//        return Response.seeOther(URI.create(appRoot + "#/start")).build();
//        return Response.seeOther(URI.create(appRoot + "td-eso-experience/src/index.html")).build();
        return Response.seeOther(URI.create(appRoot.replace("v1/", "") + "#/start")).build();


      }

		} catch (Exception e) {
			logger.error("Exception Occured in callbackRedirection::", e);
			throw new ApiException(ExceptionUtil.buildServerErrorStatus(), e);
		}

	}
}
