package com.td.dcts.eso.experience;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.td.coreapi.common.status.ApiException;
import com.td.cts.ae.id.proofing.IPServiceRetrieveRestResponse;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.dao.CustomerDAO;
import com.td.dcts.eso.experience.facade.IDPFacade;
import com.td.dcts.eso.experience.handler.ApplicationDataHandler;
import com.td.dcts.eso.experience.model.request.idp.IPAnswers;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.dcts.eso.experience.model.response.WealthClientMasterInfo;
import com.td.dcts.eso.experience.util.SessionUtil;
import com.td.eso.constants.WealthConstants;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Path("/idp")
@Component
public class IDPController extends BaseController {

  private static final XLogger LOGGER = XLoggerFactory.getXLogger(IDPController.class);

  private ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  @Value("${resturl.about.you.profile.update}")
  private String updateAboutYouURL;

  @Autowired
  private ApplicationDataHandler applicationDataHandler;

  @Autowired
  private IDPFacade idpFacade;

  @Autowired
  private CustomerDAO customerDAO;

  @GET
  @Path("/retrieveQuestions")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response retrieveQuestions(@Context HttpServletRequest httpServletRequest) throws ApiException, IOException {
    MetaData metaData = getMetaData(httpServletRequest, ExperienceConstants.ABOUT_YOU_STAGE);
    HttpHeaders httpHeaders = getHttpHeaders(httpServletRequest);

    WealthClientMasterInfo wcm = customerDAO.getWealthClientMasterInfo(sessionUtil.getDraftClientProfile(httpServletRequest));

    IPServiceRetrieveRestResponse ipServiceRetrieveRestResponse = idpFacade.retrieveQuestions(httpHeaders, metaData,wcm);
    //If we are doing ID Proofing then we are supposed to Freeze further editing of AboutYou page
    wcm.getAboutyou().setAboutYouFrozen(true);
    // Save back to session.
    sessionUtil.setDraftClientProfile(httpServletRequest, wcm);
    ResponseEntity<String> responseEntity = applicationDataHandler.getApplicationResponse(metaData, wcm, httpHeaders, updateAboutYouURL + "/" + WealthConstants.ID_PROOFING);

    //store in session quetions and time for late validation
    sessionUtil.storeInSession(httpServletRequest,objectMapper.writeValueAsString(ipServiceRetrieveRestResponse) , SessionUtil.SESSION_KEY_IDP_QUESTIONS);
    sessionUtil.storeInSession(httpServletRequest,String.valueOf(new Date().getTime()),SessionUtil.SESSION_KEY_IDP_QUESTION_SENT_TIME);

    LOGGER.exit("retrieveQuestions finished");
    return Response.ok(ipServiceRetrieveRestResponse, MediaType.APPLICATION_JSON).build();

  }

  @POST
  @Path("/validateQuestions")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response validateQuestions(@Context HttpServletRequest httpServletRequest,@RequestBody IPAnswers ipAnswers) throws ApiException, IOException {

    MetaData metaData = getMetaData(httpServletRequest, ExperienceConstants.ABOUT_YOU_STAGE);
    HttpHeaders httpHeaders = getHttpHeaders(httpServletRequest);

    IPServiceRetrieveRestResponse ipServiceRetrieveRestResponse = objectMapper.readValue(sessionUtil.retrieveFromSession(httpServletRequest,SessionUtil.SESSION_KEY_IDP_QUESTIONS),IPServiceRetrieveRestResponse.class);
    String questionSentTimeInMs = sessionUtil.retrieveFromSession(httpServletRequest,SessionUtil.SESSION_KEY_IDP_QUESTION_SENT_TIME);
    Date questionSentTime = new Date(Long.valueOf(questionSentTimeInMs));

    boolean result  = idpFacade.validateQuestions(httpHeaders, metaData,ipAnswers,ipServiceRetrieveRestResponse,questionSentTime);

    Map<String,Object> response = new HashMap<>();
    response.put("result",result ? true : false);

    LOGGER.exit("validateQuestions finished");
    return Response.ok(response, MediaType.APPLICATION_JSON).build();

  }
}
