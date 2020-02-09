package com.td.dcts.eso.experience;

import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.facade.HealthFacade;
import com.td.dcts.eso.experience.model.HealthCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/health")
@Controller
public class HealthController {

  @Autowired
  private HealthFacade healthFacade;

  @GET
  @Path("/")
  @Produces(MediaType.APPLICATION_JSON)
  public Response retrieveHealth(@Context HttpServletRequest httpServletRequest) throws ApiException {
    List<HealthCheck> healthChecks = new ArrayList<>();
    healthChecks = healthFacade.retrieveHealth(httpServletRequest);
    return Response.ok(healthChecks, MediaType.APPLICATION_JSON).build();
  }
}
