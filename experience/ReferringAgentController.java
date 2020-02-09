package com.td.dcts.eso.experience;

import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.facade.ReferringAgentFacade;
import com.td.dcts.eso.experience.model.associatesapi.AssociateStatus;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.dcts.eso.experience.util.ExceptionUtil;
import com.td.dcts.eso.session.model.EsoJsonData;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/referringAgent")
@Controller
public class ReferringAgentController extends BaseController{

  private static final XLogger LOGGER = XLoggerFactory.getXLogger(ReferringAgentController.class);

  @Autowired
  ReferringAgentFacade referringAgentFacade;

  @GET
  @Path("/{idTypeCd}/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response validateAgent(@Context HttpServletRequest httpServletRequest,
                                @PathParam("idTypeCd") String idTypeCd,
                                @PathParam("id") String id) throws ApiException {
    LOGGER.info("ReferringAgentController : validate method Started");

    String connectId = sessionUtil.getConnectIdFromSession(httpServletRequest);
    EsoJsonData esoJsonData = sessionUtil.getSessionData(httpServletRequest);
    MetaData metaData = metaDataUtil.populateMetaData(connectId, esoJsonData, null);

    HttpHeaders httpHeaders = getHttpHeaders(httpServletRequest);

    try {
      ResponseEntity<AssociateStatus> response = referringAgentFacade.retrieveStatus( metaData, httpHeaders,idTypeCd, id);
      LOGGER.error("ReferringAgentController: validateAgent Completed");
      return Response.ok(response.getBody(), MediaType.APPLICATION_JSON).build();
    } catch (Exception e) {
      LOGGER.error("ReferringAgentController: validateAgent: Exception Occurred", e);
      throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.toString()),
        e.getMessage());
    }
  }
}
