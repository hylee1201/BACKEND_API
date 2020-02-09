package com.td.dcts.eso.experience;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.td.coreapi.common.config.ApiConfig;
import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.dao.CustomerDAO;
import com.td.dcts.eso.experience.facade.ConsentFacade;
import com.td.dcts.eso.experience.facade.DisclosureFacade;
import com.td.dcts.eso.experience.model.request.disclosure.GetDisclosureListRequest;
import com.td.dcts.eso.experience.model.response.*;
import com.td.dcts.eso.experience.util.ExceptionUtil;
import com.td.dcts.eso.experience.util.SessionUtil;
import com.td.dcts.eso.response.model.WealthRestResponse;
import com.td.dcts.eso.session.model.EsoJsonData;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Map;

@Path("/consent")
@Controller
public class ConsentController extends BaseController {

  private static final XLogger LOGGER = XLoggerFactory.getXLogger(ConsentController.class);

  @Autowired
  private ConsentFacade consentFacade;

  @Autowired
  private DisclosureFacade disclosureFacade;

  private ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  @Autowired
  private CustomerDAO customerDAO;

  @POST
  @Path("/capture")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response captureConsents(@Context HttpServletRequest httpServletRequest) throws ApiException, IOException {
    ResponseEntity<WealthClientMasterInfo> responseEntity = captureConsentsCall(httpServletRequest, false, null);
    sessionUtil.setDraftClientProfile(httpServletRequest, responseEntity.getBody());
    LOGGER.exit("captureConsents finished");
    return Response.ok(responseEntity.getBody(), MediaType.APPLICATION_JSON).build();

  }

  @POST
  @Path("/captureInDB")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response captureConsentsInDB(Object request, @Context HttpServletRequest httpServletRequest) throws ApiException, IOException {
    ResponseEntity<WealthClientMasterInfo> responseEntity = captureConsentsCall(httpServletRequest, true, null);

    LOGGER.exit("captureConsents finished");
    sessionUtil.setToSession(httpServletRequest, SessionUtil.TC_CONSENT_CAPTURED, new ObjectMapper().writeValueAsString(responseEntity.getBody().getAboutyou().getTcConsentInfo()), true);

    return Response.ok(responseEntity.getBody(), MediaType.APPLICATION_JSON).build();

  }

  public ResponseEntity<WealthClientMasterInfo> captureConsentsCall(@Context HttpServletRequest httpServletRequest, Boolean recordInConsentDB, FundsTransfer fundsTransfer) throws ApiException, IOException {
    MetaData metaData = getMetaData(httpServletRequest, ExperienceConstants.CAPTURE_CONSENT_STAGE);
    HttpHeaders httpHeaders = getHttpHeaders(httpServletRequest);
    ResponseEntity<WealthClientMasterInfo> responseEntity = consentFacade.captureConsentRequest(httpServletRequest, metaData, httpHeaders, recordInConsentDB, fundsTransfer);
    return responseEntity;
  }

  @POST
  @Path("/captureTransfers")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response captureFundsTransferType(@RequestBody FundsTransfer fundsTransfer, @Context HttpServletRequest httpServletRequest) throws ApiException, IOException {
    ResponseEntity<WealthClientMasterInfo> responseEntity = captureConsentsCall(httpServletRequest, false, fundsTransfer);
    sessionUtil.setDraftClientProfile(httpServletRequest, responseEntity.getBody());
    LOGGER.exit("captureConsents finished");
    return Response.ok(responseEntity.getBody(), MediaType.APPLICATION_JSON).build();
  }

  @POST
  @Path("/retrieve")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response retrieveConsents(Object request, @Context HttpServletRequest httpServletRequest) throws ApiException, IOException {

    LOGGER.debug("Calling retrieveConsent");

    Map<String, Object> requestMap = (Map<String, Object>) request;

    ObjectMapper mapper = ApiConfig.getInstance().getMapper();
    GetDisclosureListRequest getDisclosureListRequest = mapper.readValue(mapper.writeValueAsString(requestMap),GetDisclosureListRequest.class);

    EsoJsonData esoJsonData = sessionUtil.getSessionData(httpServletRequest);

    HttpHeaders httpHeaders = getHttpHeaders(httpServletRequest);
    httpHeaders.add(HttpHeaders.COOKIE,"TDESOSESSIONID="+esoJsonData.get(SessionUtil.SESSION_KEY_SESSION_ID));

    GetDisclosureListResponse getDisclosureListResponse = disclosureFacade.getDisclosures(httpHeaders,getDisclosureListRequest);

    return Response.ok(getDisclosureListResponse,MediaType.APPLICATION_JSON).build();
  }

  @GET
  @Path("/retrieveContract")
  @Produces(MediaType.APPLICATION_JSON)
  public Response retrieveContracts(@Context HttpServletRequest httpServletRequest) throws ApiException {
    LOGGER.debug("Retrieve Contract Started");
    MetaData metaData = getMetaData(httpServletRequest, ExperienceConstants.ACKNOWLEDGEMENT_AGREEMENT_STAGE);
    WealthRestResponse wealthCMInfo = retrieveDisclosures(metaData,httpServletRequest, "0");
    return Response.ok(wealthCMInfo, MediaType.APPLICATION_JSON).build();
  }

  @GET
  @Path("/retrieveCredentialsConsent")
  @Produces(MediaType.APPLICATION_JSON)
  public Response retrieveCredentialsConsent(@Context HttpServletRequest httpServletRequest) throws ApiException {
    LOGGER.debug("Retrieve Credentials Consent Started");
    MetaData metaData = getMetaData(httpServletRequest, ExperienceConstants.CREDENTIALS_SETUP_STAGE);
    WealthRestResponse wealthCMInfo = retrieveDisclosures(metaData,httpServletRequest, "0");
    return Response.ok(wealthCMInfo, MediaType.APPLICATION_JSON).build();

  }


  @GET
  @Path("/retrieveConsentByStage/{stage}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response retrieveConsentByStage(@Context HttpServletRequest httpServletRequest, @PathParam("stage") String stage) throws ApiException {
    LOGGER.debug("Retrieve Consent By Stage");
    MetaData metaData = getMetaData(httpServletRequest, stage);
    WealthRestResponse wealthCMInfo = retrieveDisclosures(metaData,httpServletRequest, "0");
    return Response.ok(wealthCMInfo, MediaType.APPLICATION_JSON).build();

  }

  @GET
  @Path("/retrieveConsentByStage/{stage}/{profileId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response retrieveConsentByStageForProfile(@Context HttpServletRequest httpServletRequest, @PathParam("stage") String stage , @PathParam("profileId") String profileId) throws ApiException {
    LOGGER.debug("Retrieve Consent By Stage");
    MetaData metaData = getMetaData(httpServletRequest, stage);
    WealthRestResponse wealthCMInfo = retrieveDisclosures(metaData,httpServletRequest, profileId);
    return Response.ok(wealthCMInfo, MediaType.APPLICATION_JSON).build();

  }


  @GET
  @Path("/retrieveConsentByDocument/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response retrieveConsentByDocument(@Context HttpServletRequest httpServletRequest, @PathParam("id") String id) throws ApiException, IOException {
    LOGGER.debug("Retrieve Consent By Stage");
    MetaData metaData = getMetaData(httpServletRequest, id);
    DisclosureDocumentInfo disclosureDocumentInfo =  consentFacade.retrieveDisclosureByDocument(httpServletRequest, id);
    return Response.ok(disclosureDocumentInfo, MediaType.APPLICATION_JSON).build();
  }


  @GET
  @Path("/retrieveLoginConsent")
  @Produces(MediaType.APPLICATION_JSON)
  public Response retrieveLoginConsent(@Context HttpServletRequest httpServletRequest) throws ApiException {
    LOGGER.debug("Retrieve Login Consent Started");
    MetaData metaData = getMetaData(httpServletRequest, ExperienceConstants.N2B_GET_TO_KNOW_YOU_STAGE);
    WealthRestResponse wealthCMInfo = retrieveDisclosures(metaData,httpServletRequest, "0");
    return Response.ok(wealthCMInfo, MediaType.APPLICATION_JSON).build();

  }

  @GET
  @Path("/retrieveTransferTypeConsent")
  @Produces(MediaType.APPLICATION_JSON)
  public Response retrieveTransfersConsent(@Context HttpServletRequest httpServletRequest) throws ApiException {
    LOGGER.debug("Retrieve Transfers Type Consent Started");
    MetaData metaData = getMetaData(httpServletRequest, ExperienceConstants.TRANSFERTYPE_CONSENT_STAGE);
    WealthRestResponse wealthCMInfo = retrieveDisclosures(metaData,httpServletRequest, "0");
    return Response.ok(wealthCMInfo, MediaType.APPLICATION_JSON).build();
  }

  @GET
  @Path("/retrieveTransfersTCConsent")
  @Produces(MediaType.APPLICATION_JSON)
  public Response retrieveTransfersTCConsent(@Context HttpServletRequest httpServletRequest) throws ApiException {
    LOGGER.debug("Retrieve Transfers TC Consent Started");
    MetaData metaData = getMetaData(httpServletRequest, ExperienceConstants.TRANSFERS_TC_CONSENT_STAGE);
    WealthRestResponse wealthCMInfo = retrieveDisclosures(metaData,httpServletRequest, "0");
    return Response.ok(wealthCMInfo, MediaType.APPLICATION_JSON).build();
  }


  private WealthRestResponse retrieveDisclosures(MetaData metaData,HttpServletRequest httpServletRequest, String profileId) throws ApiException {

    WealthRestResponse wcmResponse = new WealthRestResponse();
    HttpHeaders httpHeaders = getHttpHeaders(httpServletRequest);
    String strLocale = httpServletRequest.getHeader(ExperienceConstants.HTTP_HEADER_LOCALE);
    //ToDo: Review Locale implementation in Release1-FastFollow
    if(strLocale==null   || strLocale.isEmpty()){
      EsoJsonData esoData = sessionUtil.getSessionData(httpServletRequest);
      strLocale = (String) esoData.get(SessionUtil.SESSION_KEY_LOCALE);
    }

    try{
      WealthClientMasterInfo wealthCMInfo = customerDAO.getWealthClientMasterInfo(sessionUtil.getDraftClientProfile(httpServletRequest));
      disclosureFacade.populateDisclosureDetails(metaData,httpHeaders,wealthCMInfo, strLocale);
      wcmResponse.setWealthClientMasterInfo(wealthCMInfo);

      // Save back to session
      sessionUtil.setDraftClientProfile(httpServletRequest, wealthCMInfo);

    } catch(Exception e) {
      LOGGER.error("Retrieve Credentials consent failed", e);
      throw new ApiException(ExceptionUtil.buildServerErrorStatus());
    }
    return wcmResponse;

  }

}
