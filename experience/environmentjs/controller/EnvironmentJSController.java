package com.td.dcts.eso.experience.environmentjs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.BaseController;
import com.td.dcts.eso.experience.environmentjs.facade.EnvironmentJSFacade;
import com.td.dcts.eso.experience.environmentjs.model.EnvironmentJSOutput;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.dcts.eso.experience.util.SessionUtil;
import com.td.dcts.eso.session.model.EsoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import static com.td.dcts.eso.experience.constants.ExperienceConstants.SAML_DROP_DOWN_CALL_ONCE;

@Controller
@Path("/")
public class EnvironmentJSController extends BaseController{

  private EnvironmentJSFacade environmentJSFacade;

  @Autowired
  public EnvironmentJSController(EnvironmentJSFacade environmentJSFacade) {
    this.environmentJSFacade = environmentJSFacade;
  }

  @GET
  @Path("/environment.js")
  @Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
  public Response dropdown(@Context HttpServletRequest httpServletRequest, String locale) throws ApiException, JsonProcessingException {

    EsoSession newSession = (EsoSession) httpServletRequest.getAttribute(SAML_DROP_DOWN_CALL_ONCE);
    MetaData metaData = getMetaData(httpServletRequest, null, newSession);
    HttpHeaders httpHeaders = getHttpHeaders(httpServletRequest, newSession);
    if(locale==null || locale.isEmpty()) {
      locale = (String) sessionUtil.getFromSession(httpServletRequest, SessionUtil.SESSION_KEY_LOCALE, newSession);
    }
    EnvironmentJSOutput environmentJSOutput = environmentJSFacade.prepareEnvironmentJS(metaData, httpHeaders, locale);

    ObjectMapper objectMapper = new ObjectMapper();

    return Response.ok("window.environment = " + objectMapper.writeValueAsString(environmentJSOutput), javax.ws.rs.core.MediaType.APPLICATION_JSON).build();
  }
}

