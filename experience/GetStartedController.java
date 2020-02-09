package com.td.dcts.eso.experience;

import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.event.response.model.SubApplicationInfo;
import com.td.dcts.eso.events.createapplication.response.SubApplication;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.facade.ApplicationDataFacade;
import com.td.dcts.eso.experience.facade.GetStartedFacade;
import com.td.dcts.eso.experience.handler.ContentHandler;
import com.td.dcts.eso.experience.helper.LeftNavHelper;
import com.td.dcts.eso.experience.model.response.*;
import com.td.dcts.eso.experience.util.ExceptionUtil;
import com.td.dcts.eso.experience.util.SessionUtil;
import com.td.dcts.eso.experience.util.ValidationUtil;
import com.td.dcts.eso.session.model.EsoJsonData;
import com.td.dcts.eso.session.model.EsoSession;
import com.td.eso.constants.WealthConstants;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.td.dcts.eso.experience.util.CommonUtil.updateAllApplicationArray;
import static com.td.dcts.eso.experience.util.SessionUtil.FLOW_ID_N2B;
import static com.td.dcts.eso.experience.constants.ExperienceConstants.*;


@Path("/getStarted")
@Controller
public class GetStartedController extends BaseController {
  private static final XLogger LOGGER = XLoggerFactory.getXLogger(GetStartedController.class);

  @Value("${enrollment.testset.key:ConsumerApplication.From.ReferenceParameters.wsOriginalConsumer}")
  private String testSetKey;

  @Value("${enrollment.testset:urn:appid:EODI:TSF6}")
  private String testSet;

  @Value("${resturl.getStarted.profile.retrieve}")
  private String getStartedProfileRetrieveURL;

  @Autowired
  private ValidationUtil validationUtil;

  @Autowired
  private ApplicationDataFacade applicationDataFacade;

  @Autowired
  private GetStartedFacade getStartedFacade;

  @Autowired
  ContentHandler contentHandler;

  @Value("${enable_connectId_restriction:false}")
  private String enable_connectId_restriction;

  @Value("${connectId_list_config:true}")
  private String connectId_list_config;

  private static final String PREFILL_LOOKUP = "PREFILL_LOOKUP";
  private static final String PREFILL_VERIFY = "PREFILL_VERIFY";
  private static final String PREFILL_REMOVE = "PREFILL_REMOVE";
  private static final String DI_WOT_ACCESS_DENIED = "DI_WOT_ACCESS_DENIED";


  @GET
  @Path("/profile")
  @Produces(MediaType.APPLICATION_JSON)
  public Response retrieveCustomerProfile(@Context HttpServletRequest httpServletRequest, EsoSession... newEsoSession) throws ApiException, IOException {
    LOGGER.entry("retrieveCustomerProfile started");
    MetaData metaData = getMetaData(httpServletRequest, ExperienceConstants.GETTING_STARTED_STAGE, newEsoSession);

    WealthClientMasterInfo wcm;

    HttpHeaders httpHeaders = getHttpHeaders(httpServletRequest, newEsoSession);
    if (httpServletRequest.getAttribute(SAML_OAUTH_TOKEN) != null) {
      httpHeaders.add(SAML_OAUTH_TOKEN, (String) httpServletRequest.getAttribute(SAML_OAUTH_TOKEN));
    };
    if (httpServletRequest.getAttribute(WealthConstants.REFERRING_AGENT) != null) {
      httpHeaders.add(WealthConstants.REFERRING_AGENT, (String) httpServletRequest.getAttribute(WealthConstants.REFERRING_AGENT));
    }
    //ToDo: Review Locale implementation in Release1-FastFollow
    String strLocale = httpServletRequest.getHeader(ExperienceConstants.HTTP_HEADER_LOCALE);
    EsoJsonData esoData = sessionUtil.getSessionData(httpServletRequest, newEsoSession);
    if(strLocale == null || strLocale.isEmpty()) {
      strLocale = (String) esoData.get(SessionUtil.SESSION_KEY_LOCALE);
    }
    String strRefNumber = (String) esoData.get(SessionUtil.DI_EXTERNAL_REF_NUMBER);

      if ((metaData.getValetKey() != null && !metaData.getValetKey().isEmpty() )) {
        wcm = getStartedFacade.getWealthClientMasterInfoResponse(metaData, httpHeaders, getStartedProfileRetrieveURL, WealthConstants.PREFILL_TYPE_VALET_KEY);
        getStartedFacade.cleanUpClientProfile(wcm);
      }
      else if ((metaData.getConnectId() != null)){
      httpHeaders.set(testSetKey, testSet);
      LOGGER.debug("retrieveCustomerProfile url={}", getStartedProfileRetrieveURL);
      wcm = getStartedFacade.getWealthClientMasterInfoResponse(metaData, httpHeaders, getStartedProfileRetrieveURL, WealthConstants.PREFILL_TYPE_CONNECT_ID);
      getStartedFacade.cleanUpClientProfile(wcm);

      LOGGER.exit("retrieveCustomerProfile finished");
      if(ExperienceConstants.N2W.equalsIgnoreCase(wcm.getUserIdentity().getUserType())) {
        sessionUtil.setToSession(httpServletRequest, SessionUtil.SESSION_KEY_FLOW_ID, wcm.getUserIdentity().getUserType(), true, newEsoSession);
      }
    }
    else if ( (metaData.getPrimaryPartyId() != null && !metaData.getPrimaryPartyId().isEmpty()))
    {
      httpHeaders.set(testSetKey, testSet);
      LOGGER.debug("retrieveCustomerProfile url={}", getStartedProfileRetrieveURL);
      wcm = getStartedFacade.getWealthClientMasterInfoResponse(metaData, httpHeaders, getStartedProfileRetrieveURL, WealthConstants.PREFILL_TYPE_CM_PARTY_ID);
      getStartedFacade.cleanUpClientProfile(wcm);
    }
      else if ( (metaData.getInboundSamlCode() != null && !metaData.getInboundSamlCode().isEmpty())) {
        LOGGER.debug("retrieveCustomerProfile url={}", getStartedProfileRetrieveURL);
          wcm = getStartedFacade.getWealthClientMasterInfoResponse(metaData, httpHeaders, getStartedProfileRetrieveURL, WealthConstants.PREFILL_TYPE_WEALTH_DESKTOP_CONTEXT);
          getStartedFacade.cleanUpClientProfile(wcm);
      }
    else {
      LOGGER.debug("connectId is null, return empty profile");
      EsoJsonData esoSession = sessionUtil.getJSONSessionData(httpServletRequest, newEsoSession);
      wcm = getStartedFacade.getN2BData(metaData, esoSession, httpHeaders);
    }
    String ipAddress = httpServletRequest.getRemoteAddr();
    String sessionId = (String) esoData.get(SessionUtil.SESSION_KEY_SESSION_ID);


    setSelectedProducts(strLocale, wcm, metaData);
    ApplicationInfo applicationInfo;
    if(wcm.getApplicationInfo() != null) {
      applicationInfo = wcm.getApplicationInfo();
      applicationInfo.setWebsiteSessionLocale(strLocale);
    } else {
      applicationInfo = new ApplicationInfo();
      applicationInfo.setWebsiteSessionLocale(strLocale);
    }
    applicationInfo.setExternalRefNumber(strRefNumber);
    applicationInfo.setIpAddress(ipAddress);
    applicationInfo.setSessionId(sessionId);
    applicationInfo.setApplicationId(metaData.getApplicationId().toString());
    SubApplication subApplication = new SubApplication();
    List<SubApplication> subApplications = new ArrayList<>();
    if(metaData.getSubApplicationList().size() > 0) {
      subApplication.setSubApplicationId(metaData.getSubApplicationList().get(0).getSubApplicationId());
    }
    subApplications.add(subApplication);
    applicationInfo.setSubApplicationList(subApplications);
    wcm.setApplicationInfo(applicationInfo);

    wcm.getUserIdentity().setApplicationId(metaData.getApplicationId().toString());
    wcm.getUserIdentity().setChannel(metaData.getChannel());
    sessionUtil.setDraftClientProfile(httpServletRequest, wcm, newEsoSession);

    if(wcm.getLastApplicationState()!= null && DI_WOT_ACCESS_DENIED.equalsIgnoreCase(wcm.getLastApplicationState().getState())){
      LOGGER.error(DI_WOT_ACCESS_DENIED);
      throw new ApiException(ExceptionUtil.buildServerErrorStatus(DI_WOT_ACCESS_DENIED));
    }
    return Response.ok(wcm, MediaType.APPLICATION_JSON).build();
  }

  private void setSelectedProducts(String strLocale, WealthClientMasterInfo wcm, MetaData metaData) throws IOException, ApiException {
    Map<String, Product> products = contentHandler.getAllProductsMap(strLocale);
    for(SubApplicationInfo subApp : metaData.getSubApplicationList()) {
      Product p = products.get(subApp.getProductId());
      if(p != null) {
        wcm.getProducts().add(p);
      }
    }
  }


  @POST
  @Path("/remove")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response removeProfile(@Context HttpServletRequest httpServletRequest, @RequestBody WealthClientMasterInfo wcmInput ) throws ApiException, IOException {
    //Lookup is always done through AccessCard:  Remove the connect Id that might be there in
    WealthClientMasterInfo blankWCM = createN2BProfile(wcmInput);
    blankWCM.getUserIdentity().setSourceApplication(wcmInput.getUserIdentity().getSourceApplication());
    return loadData(httpServletRequest, blankWCM, PREFILL_REMOVE, null);

  }

  @POST
  @Path("/lookup")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response lookupId(@Context HttpServletRequest httpServletRequest, @RequestBody WealthClientMasterInfo wcmInput ) throws ApiException, IOException {
    //Lookup is always done through AccessCard:  Remove the connect Id that might be there in
    WealthClientMasterInfo blankWCM = createN2BProfile(wcmInput);
    blankWCM.getUserIdentity().setAccessCard(wcmInput.getUserIdentity().getAccessCard());
    blankWCM.getUserIdentity().setSourceApplication(wcmInput.getUserIdentity().getSourceApplication());
    return loadData(httpServletRequest, blankWCM, PREFILL_LOOKUP, null);
  }


  @POST
  @Path("/verifyAlternateID")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response verifyAlternateID(@Context HttpServletRequest httpServletRequest, @RequestBody WealthClientMasterInfo wcmInput ) throws ApiException, IOException {
      return loadData(httpServletRequest, wcmInput, PREFILL_VERIFY, false);

    }


  @POST
  @Path("/verifyAlternateID/{feVal}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response verifyAlternateIDWithValidationParam(@Context HttpServletRequest httpServletRequest, @RequestBody WealthClientMasterInfo wcmInput, @PathParam("feVal") String feVal ) throws ApiException, IOException {
    return loadData(httpServletRequest, wcmInput, PREFILL_VERIFY,  Boolean.parseBoolean(feVal));
  }


  private WealthClientMasterInfo createN2BProfile(WealthClientMasterInfo inputProfile)
    {
      WealthClientMasterInfo wcm = new WealthClientMasterInfo();
      //populte user type (EW, N2B, N2W, etc)
      UserIdentity userIdentity = new UserIdentity();
      userIdentity.setUserType(N2B);
      userIdentity.setChannel(inputProfile.getUserIdentity().getChannel());
      userIdentity.setApplicationId(inputProfile.getUserIdentity().getApplicationId());
      userIdentity.setSubApplicationList(inputProfile.getUserIdentity().getSubApplicationList());
      wcm.setUserIdentity(userIdentity);
      wcm.setApplicationInfo(inputProfile.getApplicationInfo());
      getStartedFacade.createPrimaryApplicant(wcm);
      return wcm;
    }

    private void   setupUserIdentityInAboutYou(WealthClientMasterInfo wcmInput) {

      //Setup a new userIdentityObject in AboutYou.
      if (wcmInput.getUserIdentity() != null && wcmInput.getAboutyou() != null) {
        UserIdentity aboutYouUserIdentity = new UserIdentity();
        aboutYouUserIdentity.setConnectId(wcmInput.getUserIdentity().getConnectId());
        aboutYouUserIdentity.setUnverifiedWebborkerCredentials(wcmInput.getUserIdentity().getUnverifiedWebborkerCredentials());
        aboutYouUserIdentity.setAliasName(wcmInput.getUserIdentity().getAliasName());
        aboutYouUserIdentity.setAccessCard(wcmInput.getUserIdentity().getAccessCard());
        aboutYouUserIdentity.setHasAccessCard(wcmInput.getUserIdentity().getHasAccessCard());
        aboutYouUserIdentity.setApplicationId(wcmInput.getUserIdentity().getApplicationId());
        aboutYouUserIdentity.setSubApplicationList(wcmInput.getUserIdentity().getSubApplicationList());
        aboutYouUserIdentity.setSourceApplication(wcmInput.getUserIdentity().getSourceApplication());

        if(wcmInput.getAboutyou().getProfileType() != null && ProfileType.PRIMARY_APPLICANT.toString().equals(wcmInput.getAboutyou().getProfileType().toString())) {
          aboutYouUserIdentity.setUserType(wcmInput.getUserIdentity().getUserType());
          aboutYouUserIdentity.setCmMasterClientId(wcmInput.getUserIdentity().getCmMasterClientId());
        }
        else
        {
          aboutYouUserIdentity.setUserType(N2B);
        }
        aboutYouUserIdentity.setChannel(wcmInput.getUserIdentity().getChannel());

        aboutYouUserIdentity.setHasAccessCard(wcmInput.getUserIdentity().getHasAccessCard());
        aboutYouUserIdentity.setHasAccessCard(wcmInput.getUserIdentity().getHasAccessCard());
        wcmInput.getAboutyou().setUserIdentity(aboutYouUserIdentity);
      }
    }


  private Response loadData(HttpServletRequest httpServletRequest, WealthClientMasterInfo wcmInput, String typeOfLoad, Boolean validationSuccessfulFrontEnd ) throws ApiException, IOException
    {
      String searchBy ;
      LOGGER.entry("retrieveCustomerProfile started");
      //Front end sends ConnectID/AccessCard field in the userIdentity.accessCard field.
      //If user input matches the ConnectId format (Less than 10 characters, then we should switch the AccessCard
      // to connectId.
      //Once Front end sends data in correct field, code below would not be needed or executed.
      if((wcmInput.getUserIdentity().getAccessCard()!=null && (!wcmInput.getUserIdentity().getAccessCard().isEmpty())))
      {
        if(wcmInput.getUserIdentity().getAccessCard().length()<10)
        {
          wcmInput.getUserIdentity().setConnectId(wcmInput.getUserIdentity().getAccessCard());
          wcmInput.getUserIdentity().setAccessCard("");
        }
      }
      MetaData metaData = getMetaData(httpServletRequest, ExperienceConstants.GETTING_STARTED_STAGE);
      metaData.setConnectId(wcmInput.getUserIdentity().getConnectId());
      metaData.setAccessCard(wcmInput.getUserIdentity().getAccessCard());

      if(wcmInput.getUserIdentity().getConnectId()!=null && !wcmInput.getUserIdentity().getConnectId().isEmpty())
      {
        if ((FLOW_ID_N2B.equals(wcmInput.getUserIdentity().getUserType())) || PREFILL_LOOKUP.equals(typeOfLoad)) {
          //Load complete Profile
          searchBy = WealthConstants.PREFILL_TYPE_CONNECT_ID;
        }
        else {
          //Profile is pre-loaded through WD or ACP, we only need to validate connectID
          searchBy = WealthConstants.PREFILL_TYPE_VALIDATE_CONNECT_ID_CM_PARTY_ID_FLOW;
        }
      }
      else if (wcmInput.getUserIdentity().getAccessCard()!=null && !wcmInput.getUserIdentity().getAccessCard().isEmpty())
      {
        if ((FLOW_ID_N2B.equals(wcmInput.getUserIdentity().getUserType())) || PREFILL_LOOKUP.equals(typeOfLoad)) {
          //Load complete Profile
          searchBy = WealthConstants.PREFILL_TYPE_ACCESS_CARD;
        } else {
          //Profile is pre-loaded through WD or ACP, we only need to validate AccessCard
          searchBy = WealthConstants.PREFILL_TYPE_VALIDATE_ACCESS_CARD_FOR_PARTY_ID_FLOW;
        }
      }
      else
      {
        if(PREFILL_REMOVE.equals(typeOfLoad)) {
          HttpHeaders httpHeaders = getHttpHeaders(httpServletRequest);
          applicationDataFacade.recordUpdateApplicationEvent(metaData, wcmInput, httpHeaders);
        }

        if (!N2B.equals(wcmInput.getUserIdentity().getUserType()) &&  (
          ((wcmInput.getUserIdentity().getAccessCard() == null
          || wcmInput.getUserIdentity().getAccessCard().isEmpty())
        )
        &&
          (
            ((wcmInput.getUserIdentity().getConnectId() == null
              || wcmInput.getUserIdentity().getConnectId().isEmpty())
            )
            )))
        {
          wcmInput.getUserIdentity().setUnverifiedWebborkerCredentials(true);
        }
        setupUserIdentityInAboutYou(wcmInput);
        updateAllApplicationArray(wcmInput);
        buildLeftNavAndSaveToSession (wcmInput, httpServletRequest, WealthConstants.GET_STARTED, validationSuccessfulFrontEnd);
        return Response.ok(wcmInput, MediaType.APPLICATION_JSON).build();
      }
      WealthClientMasterInfo wcm;

      HttpHeaders httpHeaders = getHttpHeaders(httpServletRequest);
      //ToDo: Review Locale implementation in Release1-FastFollow
      String strLocale = httpServletRequest.getHeader(ExperienceConstants.HTTP_HEADER_LOCALE);
      EsoJsonData esoData = sessionUtil.getSessionData(httpServletRequest);
      if(strLocale == null || strLocale.isEmpty()) {
        strLocale = (String) esoData.get(SessionUtil.SESSION_KEY_LOCALE);
      }
      String strRefNumber = (String) esoData.get(SessionUtil.DI_EXTERNAL_REF_NUMBER);


      httpHeaders.set(testSetKey, testSet);
      LOGGER.debug("retrieveCustomerProfile url={}", getStartedProfileRetrieveURL);
      wcm = getStartedFacade.getWealthClientMasterInfoResponse(metaData, httpHeaders, getStartedProfileRetrieveURL, searchBy);
      getStartedFacade.cleanUpClientProfile(wcm);


      //If the user started as N2B but then Agent entered an AccessCard that was selected by EW or N2W customers then
      //The code below would execute.
      if (FLOW_ID_N2B.equals(wcmInput.getUserIdentity().getUserType())
        &&  !FLOW_ID_N2B.equals(wcm.getUserIdentity().getUserType()))
      {
        wcm.setApplicationInfo(wcmInput.getApplicationInfo());
        wcm.getUserIdentity().setApplicationId(wcmInput.getUserIdentity().getApplicationId());
        wcm.getUserIdentity().setAccessCard(wcmInput.getUserIdentity().getAccessCard());
        wcm.getUserIdentity().setChannel(wcmInput.getUserIdentity().getChannel());
        wcm.getUserIdentity().setSourceApplication(wcmInput.getUserIdentity().getSourceApplication());

        wcm.setLastApplicationState(wcmInput.getLastApplicationState());

        //If user is NOT N2B and there was no accessCard entered by user then we would not create another Webbroker ID for the user
        if ((wcm.getUserIdentity().getAccessCard() == null || wcm.getUserIdentity().getAccessCard().isEmpty())){
          wcm.getUserIdentity().setUnverifiedWebborkerCredentials(true);
        }
        setupUserIdentityInAboutYou(wcm);
        buildLeftNavAndSaveToSession (wcm, httpServletRequest, WealthConstants.GET_STARTED, validationSuccessfulFrontEnd);

        return Response.ok(wcm, MediaType.APPLICATION_JSON).build();
      }
      //If User prefilled using PartyID then code below would execute
      if (wcmInput.getReference().getPartyId() !=null)
      {
        if(!wcmInput.getReference().getPartyId().equals(wcm.getReference().getPartyId())) {
          //ConnectID and AccessCard provided by user is not associated with the PartyID provided by WealthDesktop
          wcmInput.getUserIdentity().setConnectId("");
          wcmInput.getUserIdentity().setAccessCard("");
          wcmInput.getUserIdentity().setUnverifiedWebborkerCredentials(true);
        }
        else
        {
          if (wcm.getUserIdentity().getConnectId()!=null){
            wcmInput.getUserIdentity().setConnectId(wcm.getUserIdentity().getConnectId());
          }
        }
      }
      setupUserIdentityInAboutYou(wcmInput);
      buildLeftNavAndSaveToSession (wcmInput, httpServletRequest, WealthConstants.GET_STARTED, validationSuccessfulFrontEnd);
      return Response.ok(wcmInput, MediaType.APPLICATION_JSON).build();
    }

    private void buildLeftNavAndSaveToSession(WealthClientMasterInfo wcm, HttpServletRequest httpServletRequest, String eventId, Boolean validationSuccessfulFrontEnd) throws ApiException,IOException {
      LeftNavHelper leftNavHelper = new LeftNavHelper();
      leftNavHelper.updateLeftNav(wcm, eventId,  validationSuccessfulFrontEnd);
      sessionUtil.setDraftClientProfile(httpServletRequest, wcm);
    }

  @GET
  @Path("/products")
  @Produces(MediaType.APPLICATION_JSON)
  public Response retrieveSelectedProducts(@Context HttpServletRequest httpServletRequest) throws ApiException {
    LOGGER.entry("retrieveSelectedProducts started");
    Map<String, Product> products;
    try {
      //ToDo: Review Locale implementation in Release1-FastFollow
      String strLocale = httpServletRequest.getHeader(ExperienceConstants.HTTP_HEADER_LOCALE);
      if(strLocale == null || strLocale.isEmpty()) {
        EsoJsonData esoData = sessionUtil.getSessionData(httpServletRequest);
        strLocale = (String) esoData.get(SessionUtil.SESSION_KEY_LOCALE);
      }

      products = contentHandler.getAllProductsMap(strLocale);
    } catch(IOException e) {
      throw new ApiException(ExceptionUtil.buildServerErrorStatus());
    }
    MetaData metaData = getMetaData(httpServletRequest, ExperienceConstants.GETTING_STARTED_STAGE);
    WealthClientMasterInfo wcm = new WealthClientMasterInfo();
    for(SubApplicationInfo subApp : metaData.getSubApplicationList()) {
      Product p = products.get(subApp.getProductId());
      if(p != null) {
        wcm.getProducts().add(p);
      }
    }
    return Response.ok(wcm, MediaType.APPLICATION_JSON).build();
  }

}

