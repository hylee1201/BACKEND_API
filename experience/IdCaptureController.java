package com.td.dcts.eso.experience;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.dao.CustomerDAO;
import com.td.dcts.eso.experience.handler.IdCaptureHandler;
import com.td.dcts.eso.experience.helper.FileUploadForm;
import com.td.dcts.eso.experience.helper.IdCaptureHelper;
import com.td.dcts.eso.experience.model.response.IDCaptureInfo;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.dcts.eso.experience.model.response.WealthClientMasterInfo;
import com.td.dcts.eso.experience.util.ExceptionUtil;
import com.td.dcts.eso.experience.util.MetaDataUtil;
import com.td.dcts.eso.experience.util.SessionUtil;
import org.apache.http.HttpStatus;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Base64;

@Path("/idCapture")
@Component
public class IdCaptureController extends BaseController {

  private IdCaptureHandler idCaptureHandler;

  private MetaDataUtil metaDataUtility;

  private IdCaptureHelper idCaptureHelper;

  private CustomerDAO customerDAO;

  private static final XLogger logger = XLoggerFactory.getXLogger(IdCaptureController.class);

  private SessionUtil sessionUtil;


//accessControlAllowOrigin is replaced by application.url.appRootUrlEn and application.url.appRootUrlFr
  @Value("${application.url.appRootUrlEn:}")
  private String appRootEn;

  @Value("${application.url.appRootUrlFr:}")
  private String appRootFr;


  @Value("${id.capture.file.upload.url}")
  private String idCaptureFileUploadUrl;

  @Value("${id.upload.verify.url:}")
  private String idCaptureVerifyUploadUrl;


  @Value("${id.upload.url}")
  private String idUploadUrl;

  @Value("${upload.secure.check:Y}")
  private String uploadSecureCheck; // for lower environment, this flag should be set to N

  @Autowired
  public IdCaptureController(IdCaptureHandler idCaptureHandler, MetaDataUtil metaDataUtil, IdCaptureHelper idCaptureHelper, CustomerDAO customerDAO, SessionUtil sessionUtil) {
    this.idCaptureHandler = idCaptureHandler;
    this.metaDataUtility = metaDataUtil;
    this.idCaptureHelper = idCaptureHelper;
    this.customerDAO = customerDAO;
    this.sessionUtil = sessionUtil;
  }


  @Path("/direct")
  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA + ";charset=UTF-8")
  public Response uploadFileLegacy(@Context HttpServletRequest httpServletRequest, @MultipartForm FileUploadForm form) throws IOException, ApiException {
    try {

      WealthClientMasterInfo wealthClientMasterInfoFromSession = customerDAO.getWealthClientMasterInfo(sessionUtil.getDraftClientProfile(httpServletRequest));
       String lang= wealthClientMasterInfoFromSession.getApplicationInfo().getWebsiteSessionLocale();
      Response.ResponseBuilder responseBuilder = Response.status(HttpStatus.SC_CREATED);

      if(ExperienceConstants.FR_CA.equals(lang)) {
        if(!appRootFr.isEmpty()) {
          responseBuilder.header("Access-Control-Allow-Origin", appRootFr) //Use Root French URL at appRootFr for accessControlAllowOrigin
            .header("Access-Control-Allow-Credentials", "true");
        }
      }
    else
      if(!appRootEn.isEmpty()) {
        responseBuilder.header("Access-Control-Allow-Origin", appRootEn) //Use Root English URL at appRootEn for accessControlAllowOrigin
          .header("Access-Control-Allow-Credentials", "true");
      }

      MetaData metaData = getMetaData(httpServletRequest,ExperienceConstants.GENERAL_STAGE);
      String subAppID = metaDataUtility.getSubApplicationId(metaData);
      String appID = String.valueOf(getMetaData(httpServletRequest, ExperienceConstants.GENERAL_STAGE).getApplicationId());
      if (appID == null || appID.trim().isEmpty() || subAppID == null || subAppID.trim().isEmpty()) {
        logger.error("AppID or SubAppId cannot be null!");
        throw new ApiException(ExceptionUtil.buildServerErrorStatus());
      }
      idCaptureHelper.verify(form);
      // profile was Base64 encoded so that French can be passed properly
      WealthClientMasterInfo mergedWealthModel = mergeProfileIntoSessionWealthModel(wealthClientMasterInfoFromSession, new String(Base64.getDecoder().decode(form.getProfile())));

      form.setFileType(idCaptureHelper.getFileType(form.getFileName()));
      IDCaptureInfo idCaptureInfo = idCaptureHandler.sendIdCaptureandReturndUpdatedWealthModel(mergedWealthModel, appID,subAppID,form, metaData.getSessionId());

      mergedWealthModel.setIdCaptureInfo(idCaptureInfo);

      sessionUtil.setDraftClientProfile(httpServletRequest, mergedWealthModel);

      return responseBuilder.entity(mergedWealthModel).build();
    } catch (Exception e) {
      logger.error("uploadFile Exception", e);
      throw e;
    }
  }

  private WealthClientMasterInfo mergeProfileIntoSessionWealthModel(WealthClientMasterInfo wealthClientMasterInfoFromSession, String profile) throws IOException {
    ObjectMapper mapper = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    WealthClientMasterInfo wealthClientMasterInfoFromProfile = mapper.readValue(profile, WealthClientMasterInfo.class);
    wealthClientMasterInfoFromSession.setIdCaptureInfo(wealthClientMasterInfoFromProfile.getIdCaptureInfo());
    return wealthClientMasterInfoFromSession;
  }
}
