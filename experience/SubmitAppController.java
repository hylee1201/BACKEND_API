package com.td.dcts.eso.experience;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.td.dcts.eso.experience.facade.SubmitAppFacade;
import com.td.dcts.eso.experience.model.response.WealthClientMasterInfo;
import com.td.dcts.eso.experience.model.docgen.PdfContainer;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.handler.SubmitAppHandler;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.util.ExceptionUtil;
import com.td.dcts.eso.experience.util.MetaDataUtil;
import com.td.dcts.eso.experience.util.RestUtil;
import com.td.dcts.eso.experience.util.SessionUtil;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.dcts.eso.session.model.EsoJsonData;

import java.io.IOException;

@Controller
@Path("/submitApp")
public class SubmitAppController {

  @Autowired
  private SubmitAppFacade submitAppFacade;

  private static final XLogger LOGGER = XLoggerFactory.getXLogger(SubmitAppHandler.class);
  private static final String SESSION_Id = "sessionId";

  @Autowired
  private SessionUtil sessionUtil;

  @Autowired
  private MetaDataUtil metaDataUtil;

  @GET
  @Path("/retrievePDF")
  @Produces(MediaType.APPLICATION_JSON)
  public Response generatePDF(@Context HttpServletRequest httpServletRequest) throws ApiException {

    String connectId = sessionUtil.getConnectIdFromSession(httpServletRequest);
    EsoJsonData esoJsonData = sessionUtil.getSessionData(httpServletRequest);
    MetaData metaData = metaDataUtil.populateMetaData(connectId, esoJsonData, ExperienceConstants.GENERAL_STAGE);
    String clientIPAddress = httpServletRequest.getRemoteAddr();
    String locale = (String) esoJsonData.get(SessionUtil.SESSION_KEY_LOCALE);
    HttpHeaders httpHeaders = RestUtil.buildRequestHeaders(locale, clientIPAddress);

    ResponseEntity<PdfContainer> responseEntity = submitAppFacade.retrievePDF(sessionUtil.getDraftClientProfile(httpServletRequest), metaData, httpHeaders);

    if(HttpStatus.OK.equals(responseEntity.getStatusCode())) {

      PdfContainer pdfContainer = responseEntity.getBody();

      return Response.ok(pdfContainer.getFile(), MediaType.APPLICATION_OCTET_STREAM)
        .header("Content-Disposition", "attachment; filename=" + pdfContainer.getFileName())
        .build();
    } else {
      throw new ApiException(ExceptionUtil.buildErrorStatusFromApiErrorStatus(responseEntity));
    }
  }

  @POST
  @Path("/submit")
  @Produces(MediaType.APPLICATION_JSON)
  public Response submit(@Context HttpServletRequest httpServletRequest) throws ApiException, IOException {

    String connectId = sessionUtil.getConnectIdFromSession(httpServletRequest);
    EsoJsonData esoJsonData = sessionUtil.getSessionData(httpServletRequest);

    MetaData metaData = metaDataUtil.populateMetaData(connectId, esoJsonData, ExperienceConstants.FINISH_STAGE);

    String clientIPAddress = httpServletRequest.getRemoteAddr();
    String locale = (String) esoJsonData.get(SessionUtil.SESSION_KEY_LOCALE);
    String sessionId = (String) esoJsonData.get(SESSION_Id);

    HttpHeaders httpHeaders = RestUtil.buildRequestHeaders(locale, clientIPAddress);
    httpHeaders.add(SESSION_Id, sessionId);

    //ToDo. Raj Change the return type from Object to Boolean once the testing of Process API is complete
    WealthClientMasterInfo res = submitAppFacade.submit(httpHeaders, metaData, httpServletRequest);

    return Response.ok(res, MediaType.APPLICATION_JSON).build();
  }
}
