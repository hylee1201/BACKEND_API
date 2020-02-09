package com.td.dcts.eso.experience;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.facade.SignatureFacade;
import com.td.dcts.eso.experience.handler.PrintHandler;
import com.td.dcts.eso.experience.helper.SubmitAppHelper;
import com.td.dcts.eso.experience.model.esignlive.ESignaturePackage;
import com.td.dcts.eso.experience.model.response.*;
import com.td.dcts.eso.experience.util.CommonUtil;
import com.td.dcts.eso.experience.util.ExceptionUtil;
import com.td.dcts.eso.experience.util.RestUtil;
import com.td.dcts.eso.experience.util.SessionUtil;
import com.td.dcts.eso.session.model.EsoJsonData;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static com.td.dcts.eso.experience.constants.ExperienceConstants.*;

@Controller
@Path("/signature")
public class SignatureController extends BaseController{

  @Autowired
  private SignatureFacade signatureFacade;

  private static final XLogger LOGGER = XLoggerFactory.getXLogger(SignatureController.class);
  private static final String SESSION_ID = "sessionId";
  private static final String PRIMARY_PROFILE_ID = "0";

  @Autowired
  SubmitAppHelper submitAppHelper;

  @Autowired
  PrintHandler printHandler;


  @GET
  @Path("/signUrl/{profileId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getSignatureUrl(@Context HttpServletRequest httpServletRequest,
                                  @PathParam("profileId") String profileId) throws ApiException {
    WealthClientMasterInfo wcmInfo = submitAppHelper.getWealthClientMasterInfo(sessionUtil.getDraftClientProfile(httpServletRequest));
    if (Strings.isNullOrEmpty(profileId) || PRIMARY_PROFILE_ID.equalsIgnoreCase(profileId)) {
      String eSignAlreadyAttempted ;
      eSignAlreadyAttempted = (String) sessionUtil.getFromSession(httpServletRequest, ExperienceConstants.SESSION_KEY_ESIGN_PACKAGE_ALREADY_ATTEMPTED);
      if(E_SIGN_ATTEMPT_ONCE.equals(eSignAlreadyAttempted)) {
        return prepareSignature(httpServletRequest, wcmInfo, ESIGN_USE_PACKAGE_IF_PREVIOUSLY_GENERATED);
      }
      else
      {
        //Sending true flag below will always force esign to Generate package.
        //This is done to fix the timing bug where user restarts esign Package generation by going back and forth.
        return prepareSignature(httpServletRequest, wcmInfo, ESIGN_FORCE_SIGN_URL_ON_ESIGN_PAGE);
      }
    } else {
      String connectId = sessionUtil.getConnectIdFromSession(httpServletRequest);
      EsoJsonData esoJsonData = sessionUtil.getSessionData(httpServletRequest);

      MetaData metaData = metaDataUtil.populateMetaData(connectId, esoJsonData, ExperienceConstants.FINISH_STAGE);
      Response response = signatureFacade.retrieveCoApplicantSignUrl(profileId,wcmInfo, metaData, buildHeaders(httpServletRequest,esoJsonData));
      return response;
    }

  }
  @GET
  @Path("/signUrl")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getSignatureUrl(@Context HttpServletRequest httpServletRequest) throws ApiException {
    return getSignatureUrl(httpServletRequest,"0");
  }

  @GET
  @Path("/setupSignUrl")
  @Produces(MediaType.APPLICATION_JSON)
  public Response setupSignatureUrl(@Context HttpServletRequest httpServletRequest) throws ApiException {
    WealthClientMasterInfo wcmInfo    = submitAppHelper.getWealthClientMasterInfo(sessionUtil.getDraftClientProfile(httpServletRequest));
    if(hasMarginProduct(wcmInfo)){
      //Request with Margin Products can not be expedited through setupSignatureURL
      sessionUtil.setToSession(httpServletRequest, ExperienceConstants.SESSION_KEY_ESIGN_PACKAGE_ALREADY_ATTEMPTED, E_SIGN_ATTEMPT_MORE_THAN_ONCE, true);
      return Response.ok("{\"HasMargin\":\"true\"}", MediaType.APPLICATION_JSON).build();
    }
    else if (isWetSign(wcmInfo)) {
      return Response.ok("{\"isWetSign\":\"true\"}", MediaType.APPLICATION_JSON).build();
    }
      else
     {
       String eSignAlreadyAttempted ;
       eSignAlreadyAttempted = (String) sessionUtil.getFromSession(httpServletRequest, ExperienceConstants.SESSION_KEY_ESIGN_PACKAGE_ALREADY_ATTEMPTED);
      if(eSignAlreadyAttempted==null) {
        sessionUtil.setToSession(httpServletRequest, ExperienceConstants.SESSION_KEY_ESIGN_PACKAGE_ALREADY_ATTEMPTED, E_SIGN_ATTEMPT_ONCE, true);
        return prepareSignature(httpServletRequest, wcmInfo, ESIGN_PREPARE_SIGN_URL_FROM_REVIEW_PAGE);
      }
      else
      {
        sessionUtil.setToSession(httpServletRequest, ExperienceConstants.SESSION_KEY_ESIGN_PACKAGE_ALREADY_ATTEMPTED, E_SIGN_ATTEMPT_MORE_THAN_ONCE, true);
        return Response.ok("{\"eSignAlreadyAttempted\":\"true\"}", MediaType.APPLICATION_JSON).build();
      }

    }
  }

  private boolean isWetSign(WealthClientMasterInfo wcmInfo)
  {
    if(SignatureTypeEnum.WET.equals(wcmInfo.getApplicationInfo().getSignatureType())) {
        return  true;
    }
    return false;
  }

  private boolean hasMarginProduct(WealthClientMasterInfo wcmInfo)
{
  for (Product product: wcmInfo.getProducts()) {
    if (PRODUCT_MARGIN.equals(product.getProductId()) || PRODUCT_USTP.equals(product.getProductId())){
      return  true;
    }
  }
  return false;
}

  private Response prepareSignature(HttpServletRequest httpServletRequest, WealthClientMasterInfo wcmInfo, String generateURL) throws  ApiException {
    String connectId = sessionUtil.getConnectIdFromSession(httpServletRequest);
    EsoJsonData esoJsonData = sessionUtil.getSessionData(httpServletRequest);

    MetaData metaData = metaDataUtil.populateMetaData(connectId, esoJsonData, ExperienceConstants.FINISH_STAGE);

    ESignaturePackage eSignLivePackageSession = new ESignaturePackage();
    WealthClientMasterInfo wcmSavedForeSignPackage =   (WealthClientMasterInfo) sessionUtil.getFromSession(httpServletRequest, ExperienceConstants.SESSION_KEY_ESIGN_PACKAGE);
    if(ESIGN_USE_PACKAGE_IF_PREVIOUSLY_GENERATED.equals(generateURL) && wcmSavedForeSignPackage !=null && wcmSavedForeSignPackage.getApplicationInfo() !=null && wcmSavedForeSignPackage.getApplicationInfo().getSignURL()!=null) {
      //Reuse existing  eSign URL
      eSignLivePackageSession.setPackageId(wcmSavedForeSignPackage.getApplicationInfo().getSignedDocumentPackageId());
      eSignLivePackageSession.setSignUrl(wcmSavedForeSignPackage.getApplicationInfo().getSignURL());

      wcmInfo.getApplicationInfo().setSignedDocumentPackageId(eSignLivePackageSession.getPackageId());
      wcmInfo.getApplicationInfo().setSignURL(eSignLivePackageSession.getSignUrl());
      wcmInfo.getAboutyou().setAccountDetails(wcmSavedForeSignPackage.getAboutyou().getAccountDetails()); //account numbers is set in App Engine
      wcmInfo.getAboutyou().setSupplementFormFlags(wcmSavedForeSignPackage.getAboutyou().getSupplementFormFlags()); //Supplement Flags is set in App Engine

      for (AboutYou applicant : wcmSavedForeSignPackage.getAboutAllApplicantsAndParties()) {//supplement flags set for other applicants
        if (ProfileType.PRIMARY_APPLICANT == applicant.getProfileType() || ProfileType.CO_APPLICANT == applicant.getProfileType()) {
          if (applicant.getSupplementFormFlags() != null) {
            AboutYou wcmAboutYou = CommonUtil.getProfile(wcmInfo, applicant.getProfileId());
            wcmAboutYou.setSupplementFormFlags(applicant.getSupplementFormFlags());
          }
        }
      }
      wcmInfo.getAboutyou().setPdfInfoList(wcmSavedForeSignPackage.getAboutyou().getPdfInfoList()); //set the PDF Info list
      wcmInfo.setProducts(wcmSavedForeSignPackage.getProducts());
      wcmInfo.getUserIdentity().setCmMasterClientId(wcmSavedForeSignPackage.getUserIdentity().getCmMasterClientId());
      wcmInfo.getApplicationInfo().setApplicationPackage(wcmSavedForeSignPackage.getApplicationInfo().getApplicationPackage()); //print doclist
      sessionUtil.setDraftClientProfile(httpServletRequest, wcmInfo);
      return Response.ok(eSignLivePackageSession, MediaType.APPLICATION_JSON).build();
    }
    else {
      //Generate new eSign URL:
      ResponseEntity<WealthClientMasterInfo> responseEntity = signatureFacade.retrieveSignUrl(
          wcmInfo,
          metaData,
          buildHeaders(httpServletRequest, esoJsonData));

      if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
        wcmInfo = responseEntity.getBody();
        ESignaturePackage eSignLivePackage = new ESignaturePackage();// responseEntity.getBody();
        eSignLivePackage.setPackageId(wcmInfo.getApplicationInfo().getSignedDocumentPackageId());
        eSignLivePackage.setSignUrl(wcmInfo.getApplicationInfo().getSignURL());

        //when wcm is updated with latest info from esign steps (like ANR accounts), set print list to session
        List<String> docList = printHandler.printDocList(httpServletRequest,wcmInfo,metaData,buildHeaders(httpServletRequest, esoJsonData));
        sessionUtil.setToSession(httpServletRequest, ExperienceConstants.SESSION_KEY_PRINT_DOC_LIST, docList, true);
        wcmInfo = printHandler.updateDocListInWcm(wcmInfo, docList );

        if(ESIGN_PREPARE_SIGN_URL_FROM_REVIEW_PAGE.equals(generateURL))
        {
          //if we are launching eSignGeneration through setupSignURL, we do not update the main wcmInfo
          sessionUtil.setToSession(httpServletRequest, ExperienceConstants.SESSION_KEY_ESIGN_PACKAGE, wcmInfo, true);
        }
        else {
          sessionUtil.setDraftClientProfile(httpServletRequest, wcmInfo);
        }

        return Response.ok(eSignLivePackage, MediaType.APPLICATION_JSON).build();
      } else {
        throw new ApiException(ExceptionUtil.buildErrorStatusFromApiErrorStatus(responseEntity));
      }
    }
  }

  private HttpHeaders buildHeaders(HttpServletRequest httpServletRequest, EsoJsonData esoJsonData){
    String clientIPAddress = httpServletRequest.getRemoteAddr();
    String locale = (String) esoJsonData.get(SessionUtil.SESSION_KEY_LOCALE);
    String sessionId = (String) esoJsonData.get(SESSION_ID);

    HttpHeaders httpHeaders = RestUtil.buildRequestHeaders(locale, clientIPAddress);
    httpHeaders.add(SESSION_ID, sessionId);
    return httpHeaders;
  }

  @POST
  @Path("/captureSign")
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response captureSign(@Context HttpServletRequest httpServletRequest, @RequestBody WealthClientMasterInfo wcm) throws ApiException, JsonProcessingException {
    MetaData metaData = getMetaData(httpServletRequest, ExperienceConstants.ABOUT_YOU_STAGE);
    EsoJsonData esoJsonData = sessionUtil.getSessionData(httpServletRequest);
    signatureFacade.logCaptureSignEvent(buildHeaders(httpServletRequest, esoJsonData),metaData);
    return Response.ok().build();
  }

  // TODO move the functionality in this endpoint to the /submitApp endpoint
  @POST
  @Path("/sendToFilenet")
  @Produces(MediaType.APPLICATION_JSON)
  public Response sendSignedPackageToFilenet(@Context HttpServletRequest httpServletRequest, @RequestBody ESignaturePackage eSignaturePackage) throws ApiException {

    String connectId = sessionUtil.getConnectIdFromSession(httpServletRequest);
    EsoJsonData esoJsonData = sessionUtil.getSessionData(httpServletRequest);

    MetaData metaData = metaDataUtil.populateMetaData(connectId, esoJsonData, ExperienceConstants.FINISH_STAGE);

    String clientIPAddress = httpServletRequest.getRemoteAddr();
    String locale = (String) esoJsonData.get(SessionUtil.SESSION_KEY_LOCALE);
    String sessionId = (String) esoJsonData.get(SESSION_ID);

    HttpHeaders httpHeaders = RestUtil.buildRequestHeaders(locale, clientIPAddress);
    httpHeaders.add(SESSION_ID, sessionId);

    List<FileNetDocument> res = signatureFacade.sendPackageToFilenet(
      sessionUtil.getDraftClientProfile(httpServletRequest),
      eSignaturePackage.getPackageId(),
      metaData,
      httpHeaders);

    return Response.ok(res, MediaType.APPLICATION_JSON).build();
  }
}
