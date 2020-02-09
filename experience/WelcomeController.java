package com.td.dcts.eso.experience;

import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.facade.WelcomeFacade;
import com.td.dcts.eso.experience.util.SessionUtil;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

@Component
@Path("/welcome")
public class WelcomeController {

  static final XLogger logger = XLoggerFactory.getXLogger(WelcomeController.class);

  private final String N2B = "n2b";

  @Autowired
  private WelcomeFacade welcomeFacade;


  @GET
  @Path("/")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.TEXT_HTML)
  public Response welcome(@Context HttpServletRequest httpServletRequest,
                          @Context HttpServletResponse httpServletResponse,
                          @QueryParam("lang") String lang,
                          @QueryParam("marketingCode") String marketingCode,
                          @QueryParam("sourceCode") String sourceCode,
                          @QueryParam("channel") String channelCode,
                          @QueryParam("sourceUrl") String sourceUrl,
                          @QueryParam(SessionUtil.SESSION_KEY_PRODUCT_ID) List<String> productIds,
                          @QueryParam(N2B) String n2b,
                          @QueryParam("targetURL") String targetURL)
    throws ApiException {
    //default language to en_CA if it's not passed as a query parameter
    lang = lang == null ? ExperienceConstants.EN_CA : lang;
    return welcomeFacade.processWelcome(httpServletRequest, httpServletResponse, lang,marketingCode, sourceCode,  sourceUrl,productIds,n2b, targetURL , channelCode);
  }


  @GET
  @Path("/{lang}")
  @Consumes(MediaType.WILDCARD)
  @Produces(MediaType.TEXT_HTML)
  public Response welcomeWithLang(@Context HttpServletRequest httpServletRequest,
                          @Context HttpServletResponse httpServletResponse,
                          @PathParam("lang") String lang,
                          @QueryParam("marketingCode") String marketingCode,
                          @QueryParam("sourceCode") String sourceCode,
                          @QueryParam("channel") String channelCode,
                          @QueryParam("sourceUrl") String sourceUrl,
                          @QueryParam(SessionUtil.SESSION_KEY_PRODUCT_ID) List<String> productIds,
                          @QueryParam(N2B) String n2b,
                          @QueryParam("targetURL") String targetURL)
    throws ApiException {
    //default language to en_CA if any un-recognized language is passed
    lang = ExperienceConstants.FR_CA.equals(lang) ? ExperienceConstants.FR_CA : ExperienceConstants.EN_CA;
    return welcomeFacade.processWelcome(httpServletRequest, httpServletResponse, lang,marketingCode, sourceCode,  sourceUrl,productIds,n2b, targetURL, channelCode);
  }

  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.TEXT_HTML)
  public Response welcomeSAML(@Context HttpServletRequest httpServletRequest,
                              @Context HttpServletResponse httpServletResponse,
                              @QueryParam("lang") String lang,
                              @QueryParam("marketingCode") String marketingCode,
                              @QueryParam("sourceCode") String sourceCode,
                              @QueryParam("channel") String channelCode,
                              @QueryParam("sourceUrl") String sourceUrl,
                              @QueryParam(SessionUtil.SESSION_KEY_PRODUCT_ID) List<String> productIds,
                              @QueryParam(N2B) String n2b,
                              @QueryParam("targetURL") String targetURL) throws ApiException, IOException {
/*
      if (StringUtils.isBlank(lang)) {
        lang = httpServletRequest.getParameter("lang");
      }
*/
      Response res = welcomeFacade.processWelcomeFromSAML(httpServletRequest, httpServletResponse, lang, marketingCode, sourceCode, channelCode, sourceUrl, productIds, n2b, targetURL);

    return res;
  }
}
