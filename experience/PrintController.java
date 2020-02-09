package com.td.dcts.eso.experience;

import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.dao.CustomerDAO;
import com.td.dcts.eso.experience.facade.PrintFacade;
import com.td.dcts.eso.experience.model.print.EsoDocumentOrchestrationResponse;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.dcts.eso.experience.model.response.ReferringAgent;
import com.td.dcts.eso.experience.model.response.WealthClientMasterInfo;
import com.td.dcts.eso.experience.util.CommonUtil;
import com.td.dcts.eso.experience.util.ExceptionUtil;
import com.td.dcts.eso.session.model.EsoJsonData;
import org.apache.commons.lang.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

import static com.td.dcts.eso.experience.util.CommonUtil.updateInfoFromESignPackage;


@Path("/print")
@Controller
public class PrintController extends BaseController {

  private static final XLogger LOGGER = XLoggerFactory.getXLogger(PrintController.class);
  private static final String PACKAGE_NAME = "printPackage.pdf";
  private static final String WELCOME_PACKAGE_NAME = "welcomePackage.pdf";

  @Autowired
  private PrintFacade printFacade;

  @Autowired
  private CustomerDAO customerDAO;

  @GET
  @Path("/welcomePackagePdf")
  @Produces(MediaType.APPLICATION_JSON)
  public Response downloadWelcomePackage(@Context HttpServletRequest httpServletRequest) throws ApiException, IOException {
    HttpHeaders httpHeaders = getHttpHeaders(httpServletRequest);
    String connectId = sessionUtil.getConnectIdFromSession(httpServletRequest);
    EsoJsonData esoJsonData = sessionUtil.getSessionData(httpServletRequest);
    MetaData metaData = metaDataUtil.populateMetaData(connectId, esoJsonData, null);
    byte[] pdfPackage = printFacade.printWelcomePackage(metaData, httpHeaders, esoJsonData.get("locale").toString());

    return Response.ok(pdfPackage, MediaType.APPLICATION_OCTET_STREAM)
      .header("Content-Disposition", "attachment; filename=" + WELCOME_PACKAGE_NAME)
      .build();
  }



    @GET
  @Path("/printPackagePdf")
  @Produces(MediaType.APPLICATION_JSON)
  public Response downloadDocumentPackage(@Context HttpServletRequest httpServletRequest,
                                          @QueryParam("acf2id") String acf2id,
                                          @QueryParam("transit") String transit) throws ApiException, IOException {

    LOGGER.info("PrinterController: Start downloading document package");
    String connectId = sessionUtil.getConnectIdFromSession(httpServletRequest);
    EsoJsonData esoJsonData = sessionUtil.getSessionData(httpServletRequest);
    MetaData metaData = metaDataUtil.populateMetaData(connectId, esoJsonData, null);

    WealthClientMasterInfo wcm = customerDAO.getWealthClientMasterInfo(sessionUtil.getDraftClientProfile(httpServletRequest));
    wcm = CommonUtil.resetWCM(wcm, "0");
    WealthClientMasterInfo wcmSavedForeSignPackage =   (WealthClientMasterInfo) sessionUtil.getFromSession(httpServletRequest, ExperienceConstants.SESSION_KEY_ESIGN_PACKAGE);

    updateInfoFromESignPackage(wcmSavedForeSignPackage, wcm);

    if (StringUtils.isNotEmpty(acf2id)) {
      if (wcm.getApplicationInfo().getReferringAgent() != null) {
        wcm.getApplicationInfo().getReferringAgent().setAcf2id(acf2id);
      } else {
        ReferringAgent referringAgent = new ReferringAgent();
        referringAgent.setAcf2id(acf2id);
        wcm.getApplicationInfo().setReferringAgent(referringAgent);
      }
    }

    if (StringUtils.isNotEmpty(transit)) {
      if (wcm.getApplicationInfo().getReferringAgent() != null) {
        wcm.getApplicationInfo().getReferringAgent().setTransitNumber(transit);
      } else {
        ReferringAgent referringAgent = new ReferringAgent();
        referringAgent.setTransitNumber(transit);
        wcm.getApplicationInfo().setReferringAgent(referringAgent);
      }
    }
    sessionUtil.setDraftClientProfile(httpServletRequest, wcm);

    HttpHeaders httpHeaders = getHttpHeaders(httpServletRequest);

    ResponseEntity<EsoDocumentOrchestrationResponse> printPackage
      = printFacade.printPackage(wcm, metaData, httpHeaders);

    if (HttpStatus.OK.equals(printPackage.getStatusCode())) {
      LOGGER.exit("PrinterController::printDocumentPackage finished");

      byte[] pdfPackage = printPackage.getBody().getDocumentResponse().get(0).getPrintPackagePdf();

      return Response.ok(pdfPackage, MediaType.APPLICATION_OCTET_STREAM)
        .header("Content-Disposition", "attachment; filename=" + PACKAGE_NAME)
        .build();
    } else {
      LOGGER.exit("PrinterController::downloadDocumentPackage failed");
      throw new ApiException(ExceptionUtil.buildErrorStatusFromApiErrorStatus(printPackage));
    }

  }

  @GET
  @Path("/printDocList")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getPrintDocList(@Context HttpServletRequest httpServletRequest) throws ApiException {
    LOGGER.info("PrinterController: Start getPrintDocList");

    //Print Doc List is already set to session when esign package is generated.
    List<String> printDocList = (List<String>) sessionUtil.getFromSession(httpServletRequest, ExperienceConstants.SESSION_KEY_PRINT_DOC_LIST);
    return Response.ok(printDocList,MediaType.APPLICATION_JSON).build();
  }

  @POST
  @Path("/printPackageOCP")
  @Produces(MediaType.APPLICATION_JSON)
  public Response printDocumentPackage(@Context HttpServletRequest httpServletRequest) throws ApiException, IOException {

    LOGGER.info("PrinterController: Start printing document package");
    String connectId = sessionUtil.getConnectIdFromSession(httpServletRequest);
    EsoJsonData esoJsonData = sessionUtil.getSessionData(httpServletRequest);
    MetaData metaData = metaDataUtil.populateMetaData(connectId, esoJsonData, null);

    WealthClientMasterInfo wcm = customerDAO.getWealthClientMasterInfo(sessionUtil.getDraftClientProfile(httpServletRequest));
    wcm = CommonUtil.resetWCM(wcm, "0");
    HttpHeaders httpHeaders = getHttpHeaders(httpServletRequest);

    ResponseEntity<EsoDocumentOrchestrationResponse> printPackage
      = printFacade.printPackage(wcm, metaData, httpHeaders);

    if (HttpStatus.OK.equals(printPackage.getStatusCode())) {
      LOGGER.exit("PrinterController::printDocumentPackage finished");
      return Response.ok(printPackage.getBody(), MediaType.APPLICATION_JSON).build();
    } else {
      LOGGER.exit("PrinterController::printDocumentPackage failed");
      throw new ApiException(ExceptionUtil.buildErrorStatusFromApiErrorStatus(printPackage));
    }

  }




}
