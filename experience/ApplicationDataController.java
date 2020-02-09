package com.td.dcts.eso.experience;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.dao.CustomerDAO;
import com.td.dcts.eso.experience.facade.ApplicationDataFacade;
import com.td.dcts.eso.experience.facade.SignatureFacade;
import com.td.dcts.eso.experience.handler.PrintHandler;
import com.td.dcts.eso.experience.helper.ApplicationDataHelper;
import com.td.dcts.eso.experience.helper.LeftNavHelper;
import com.td.dcts.eso.experience.model.response.*;
import com.td.dcts.eso.experience.util.CommonUtil;
import com.td.dcts.eso.experience.util.ExceptionUtil;
import com.td.dcts.eso.experience.util.SessionUtil;
import com.td.dcts.eso.response.model.WealthRestResponse;
import com.td.eso.constants.WealthConstants;
import com.td.eso.util.FeatureToggle;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("/applicationData")
@Controller
public class ApplicationDataController extends BaseController{
	private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApplicationDataController.class);

  @Autowired
  private SignatureFacade signatureFacade;


  @Autowired
	private ApplicationDataFacade applicationDataFacade;

  @Autowired
  private ApplicationDataHelper applicationDataHelper;

  @Autowired
  private CustomerDAO customerDAO;

  @Autowired
  PrintHandler printHandler;

  private ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  @GET
	@Path("/profile")
	@Produces(MediaType.APPLICATION_JSON)
	public Response retrieveAboutYou(@Context HttpServletRequest httpServletRequest) throws ApiException {
    return retrieveAboutYou(httpServletRequest, "0", false, false);
	}


  @GET
  @Path("/review")
  @Produces(MediaType.APPLICATION_JSON)
  public Response retrieveAboutYouReview(@Context HttpServletRequest httpServletRequest) throws ApiException {
    return retrieveAboutYou(httpServletRequest, "0", true, false);
  }

  @GET
  @Path("/finishingup")
  @Produces(MediaType.APPLICATION_JSON)
  public Response retrieveAboutYouFinishingUp(@Context HttpServletRequest httpServletRequest) throws ApiException {
    return retrieveAboutYou(httpServletRequest, "0", false, true);
  }


  @GET
  @Path("/profile/{profileId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response retrieveAboutYouForSeq(@Context HttpServletRequest httpServletRequest,
                                         @PathParam("profileId") String profileId) throws ApiException {

    return retrieveAboutYou(httpServletRequest, profileId, false, false);
  }


  private Response retrieveAboutYou(HttpServletRequest httpServletRequest,
                                         String profileId,
                                         Boolean runReviewValidations,
                                        Boolean runFinishingupProcess) throws ApiException {
    LOGGER.entry("retrieveAboutYou started");
    WealthRestResponse aboutYouRestResponse = new WealthRestResponse();
    try {
      WealthClientMasterInfo wealthCMInfo = applicationDataFacade.getWealthClientMasterInfo(sessionUtil.getDraftClientProfile(httpServletRequest));
      MetaData metaData = getMetaData(httpServletRequest, ExperienceConstants.GENERAL_STAGE);
      HttpHeaders httpHeaders = getHttpHeaders(httpServletRequest);

        if((runFinishingupProcess == true) && (SignatureTypeEnum.WET.equals(wealthCMInfo.getApplicationInfo().getSignatureType()))) {
          //Calling the Endpoint below will run ANR and will get new accounts
          ResponseEntity<WealthClientMasterInfo> responseEntity = signatureFacade.retrieveSignUrl(wealthCMInfo, metaData, httpHeaders);
          if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            wealthCMInfo = responseEntity.getBody();
            List<String> docList = new ArrayList<>();
            if(FeatureToggle.PRINT_PACKAGE_ENABLE() == true)
            {
              docList = printHandler.printDocList(httpServletRequest, wealthCMInfo, metaData,httpHeaders);
            }

            //also set print list to applicationInfo applicationPackage
            wealthCMInfo= printHandler.updateDocListInWcm(wealthCMInfo, docList );          }
        }

      if(runReviewValidations==true){
        //Run Backend Validations for Review Page
        wealthCMInfo = applicationDataFacade.runReviewValidations(metaData, wealthCMInfo, httpHeaders);
      }

      wealthCMInfo = CommonUtil.resetWCM(wealthCMInfo, profileId);
      sessionUtil.setDraftClientProfile(httpServletRequest, wealthCMInfo);
      wealthCMInfo = applicationDataHelper.deattachConsent(wealthCMInfo);
      aboutYouRestResponse.setWealthClientMasterInfo(wealthCMInfo);

    } catch (Exception e) {
      LOGGER.error("Get WealthClientMasterInfo is failed", e);
      throw new ApiException(ExceptionUtil.buildServerErrorStatus());
    } finally {
      LOGGER.exit("retrieveAboutYou finished");
    }
    return Response.ok(aboutYouRestResponse, MediaType.APPLICATION_JSON).build();
  }



  @GET
	@Path("/accountDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public Response retrieveAccountDetails(@Context HttpServletRequest httpServletRequest) throws ApiException {
		LOGGER.entry("accountDetails started");
		WealthRestResponse aboutYouRestResponse = new WealthRestResponse();
		try {
			MetaData metaData = getMetaData(httpServletRequest, ExperienceConstants.ABOUT_YOU_STAGE);
			HttpHeaders httpHeaders = getHttpHeaders(httpServletRequest);
			WealthClientMasterInfo wealthCMInfo = applicationDataFacade.getAccountsAndWealthClientMasterInfo(httpServletRequest, sessionUtil.getDraftClientProfile(httpServletRequest), httpHeaders, metaData);
			aboutYouRestResponse.setWealthClientMasterInfo(wealthCMInfo);

		} catch (Exception e) {
			LOGGER.error("Get WealthClientMasterInfo is failed", e);
			throw new ApiException(ExceptionUtil.buildServerErrorStatus());
		} finally {
			LOGGER.exit("retrieveAboutYou finished");
		}
		return Response.ok(aboutYouRestResponse, MediaType.APPLICATION_JSON).build();
	}


  @POST
  @Path("/profile/{eventId}/{feVal}")
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateProfileWithValidationFlag(@Context HttpServletRequest httpServletRequest, @RequestBody WealthClientMasterInfo wcm,
                                                  @PathParam("eventId") String eventId,
                                                  @PathParam("feVal") String feVal) throws ApiException, IOException {
    return updateProfile(httpServletRequest, wcm, eventId, Boolean.parseBoolean(feVal), "");
  }

  @POST
	@Path("/profile/{eventId}")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateProfileWithoutValFlag(@Context HttpServletRequest httpServletRequest, @RequestBody WealthClientMasterInfo wcm, @PathParam("eventId") String eventId) throws ApiException, IOException {
    return updateProfile(httpServletRequest, wcm, eventId, false, "");
  }

  @POST
  @Path("/product/{productId}/{feVal}")
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateAccountsWithValidationFlag(@Context HttpServletRequest httpServletRequest, @RequestBody WealthClientMasterInfo wcm,
                                                  @PathParam("productId") String productId,
                                                  @PathParam("feVal") String feVal) throws ApiException, IOException {
    return updateProfile(httpServletRequest, wcm, WealthConstants.ACCOUNT_DETAILS, Boolean.parseBoolean(feVal), productId);
  }


  private Response updateProfile(HttpServletRequest httpServletRequest, WealthClientMasterInfo wcm, String eventId, Boolean validationSuccessfulFrontEnd, String productId) throws ApiException, IOException {
		LOGGER.entry("updateProfile started");
		MetaData metaData = getMetaData(httpServletRequest, ExperienceConstants.ABOUT_YOU_STAGE);
    applicationDataFacade.updateEligibilitySummary(wcm, eventId, validationSuccessfulFrontEnd);
		if (WealthConstants.PRODUCT_SELECTOR.equals(eventId)) {
			metaDataUtil.updateSubApplicationList(metaData, wcm);
			List<String> products = new ArrayList<>();
			for (Product p: wcm.getProducts()) {
				products.add(p.getProductId());
			}
			sessionUtil.setToSession(httpServletRequest, SessionUtil.SESSION_KEY_PRODUCTS, products, true);
		}
		HttpHeaders httpHeaders = getHttpHeaders(httpServletRequest);
		try {

		  Response response = applicationDataFacade.updateProfile(httpServletRequest, wcm, eventId, httpHeaders, metaData, validationSuccessfulFrontEnd, productId);
      if (response.getEntity() != null) {
        EligibilityStatus eligibilityStatus = objectMapper.readValue((String) response.getEntity(), EligibilityStatus.class);

        //special case where session needs to be updated with credit score. it is hack, can be re-factored later
        //using a better pattern.
        if (eligibilityStatus.getCreditScore() != null) {

          WealthClientMasterInfo wealthCMInfo = customerDAO.getWealthClientMasterInfo(sessionUtil.getDraftClientProfile(httpServletRequest));
          wealthCMInfo.getAboutyou().getEligibilityInfo().setCreditScore(String.valueOf(eligibilityStatus.getCreditScore()));
          sessionUtil.setDraftClientProfile(httpServletRequest, wealthCMInfo);

          eligibilityStatus.setCreditScore(null);

          Response updateResponse = Response.ok(objectMapper.writeValueAsString(eligibilityStatus), MediaType.APPLICATION_JSON).build();

          return updateResponse;

        }
      }





      return response;

		} catch (Exception e) {
			LOGGER.error("Parsing WealthClientMasterInfo JSON data is failed", e);
			throw new ApiException(ExceptionUtil.buildServerErrorStatus());
		} finally {
			LOGGER.exit("updateProfile finished");
		}

	}




  @POST
  @Path("/allApplicantsAndPartyArrayUpdate")
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response allApplicantsAndPartyArrayUpdate(@Context HttpServletRequest httpServletRequest, @RequestBody WealthClientMasterInfo wcm) throws ApiException {
    return allApplicantsAndPartyArrayUpdateFunction(httpServletRequest, wcm, WealthConstants.CO_APPLICANTS, false);
  }

  @POST
  @Path("/allApplicantsAndPartyArrayUpdate/{eventId}/{feVal}")
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response allApplicantsAndPartyArrayUpdateWithEvent
    (@Context HttpServletRequest httpServletRequest,
     @RequestBody WealthClientMasterInfo wcm,
     @PathParam("eventId") String eventId,
     @PathParam("feVal") String feVal) throws ApiException {
    return allApplicantsAndPartyArrayUpdateFunction(httpServletRequest, wcm, eventId, Boolean.parseBoolean(feVal));
  }

  private Response allApplicantsAndPartyArrayUpdateFunction
    (HttpServletRequest httpServletRequest, WealthClientMasterInfo wcm, String eventId, Boolean validationSuccessfulFrontEnd ) throws ApiException {
    LOGGER.entry("updateProfile started");
    MetaData metaData = getMetaData(httpServletRequest, ExperienceConstants.ABOUT_YOU_STAGE);
    HttpHeaders httpHeaders = getHttpHeaders(httpServletRequest);
    try {
      LeftNavHelper leftNavHelper = new LeftNavHelper();

      leftNavHelper.updateLeftNav(wcm,  eventId, validationSuccessfulFrontEnd);
      applicationDataFacade.updateForUserIdentity(wcm);
      sessionUtil.setDraftClientProfile(httpServletRequest, wcm);
      Response updateResponse = Response.ok(wcm, MediaType.APPLICATION_JSON).build();

      return updateResponse;

    } catch (Exception e) {
      LOGGER.error("allApplicantsAndPartyArrayUpdate failed", e);
      throw new ApiException(ExceptionUtil.buildServerErrorStatus());
    } finally {
      LOGGER.exit("allApplicantsAndPartyArrayUpdate finished");
    }

  }

  @POST
  @Path("/identity")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response setupIdentity(@Context HttpServletRequest httpServletRequest,
                                @RequestBody Map<String, String> inputData) throws ApiException {
    return setupIdentityForProfile(httpServletRequest, inputData);
  }


  private Response setupIdentityForProfile(HttpServletRequest httpServletRequest, Map<String, String> inputData) throws ApiException {
    LOGGER.entry("updateProfile started");
    MetaData metaData = getMetaData(httpServletRequest, ExperienceConstants.ABOUT_YOU_STAGE);
    HttpHeaders httpHeaders = getHttpHeaders(httpServletRequest);
    try {

      IdentityManagementResponse connectId = applicationDataFacade.setupIdentity(httpServletRequest, httpHeaders, metaData,
        inputData);
      return Response.ok(connectId, MediaType.APPLICATION_JSON).build();

    } catch (Exception e) {
      LOGGER.error("Parsing WealthClientMasterInfo JSON data is failed", e);
      throw new ApiException(ExceptionUtil.buildServerErrorStatus());
    } finally {
      LOGGER.exit("updateProfile finished");
    }

  }


}
