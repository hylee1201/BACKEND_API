package com.td.dcts.eso.experience;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.cache.NoCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.coreapi.common.config.ApiConfig;
import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.session.service.IESOSessionService;

@Path("/metrix")
@Component
public class ThreatMetrixController {

  @Autowired
  private IESOSessionService iEsoSessionService;

  @GET
  @NoCache
  @Path("details")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getMetrixDetails(@Context HttpServletRequest httpServletRequest) throws ApiException {

    String threatMetrixUrl = ApiConfig.getInstance()
      .getPropertiesConfig(ExperienceConstants.ENV_FILE_NAME).getProperty(ExperienceConstants.THREAT_METRIX_TAGS_URL);

    Map<String, String> response = new HashMap<String, String>();
    String esoSessionId = iEsoSessionService.getSessionId(httpServletRequest);
    response.put("url", threatMetrixUrl.replaceFirst("SESSION_ID", esoSessionId));

    return Response.ok(response, MediaType.APPLICATION_JSON).build();
  }
}
