package com.td.dcts.eso.experience;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.td.dcts.eso.experience.facade.ValidationFacade;
import com.td.dcts.eso.experience.model.response.WealthClientMasterInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;

import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.util.ExceptionUtil;

import java.io.IOException;

@Path("/validate")
@Controller
public class ValidationController extends BaseController {
  private static final String VALID = "valid";
  private static final String INVALID = "invalid";
  private static final String VALID_NOT_LOGGED_IN = "alive";

  @Autowired
  private ValidationFacade validationFacade;


  @GET
	@Path("/session")
	@Produces(MediaType.TEXT_PLAIN)
	public String isSessionValid(@Context HttpServletRequest httpServletRequest) throws ApiException {
		if (sessionUtil.isSessionValid(httpServletRequest)) {
      return VALID; // user has logged in
    } else if (sessionUtil.isSessionAlive(httpServletRequest)) {
		  return VALID_NOT_LOGGED_IN; // application has started (i.e. sessionID has been created)
		} else {
			throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase()),"Session Expired");
		}
	}


  @GET
  @Path("/currentState")
  @Produces(MediaType.TEXT_PLAIN)
  public String getCurrentState(@Context HttpServletRequest httpServletRequest) throws ApiException, JsonProcessingException {
    return sessionUtil.getCurrentAppState(httpServletRequest);
  }


  @GET
  @Path("/bootstrap")
  @Produces(MediaType.TEXT_PLAIN)
  public String isBootstrapValid(@Context HttpServletRequest httpServletRequest) throws ApiException, IOException {
    if (validationFacade.isBootstrapValid(httpServletRequest)) {
      return VALID; // New Application  is good
    } else {
      return INVALID; // New Application  should be created
    }
  }


}
