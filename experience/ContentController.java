package com.td.dcts.eso.experience;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.helper.CachedObjectHelper;
import com.td.dcts.eso.experience.model.response.LocaleAndOverrideMap;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.annotations.GZIP;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;

import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.dao.CustomerDAO;
import com.td.dcts.eso.experience.facade.ContentFacade;
import com.td.dcts.eso.experience.model.response.Product;
import com.td.dcts.eso.experience.model.response.WealthClientMasterInfo;
import com.td.dcts.eso.experience.util.ExceptionUtil;
import com.td.dcts.eso.experience.util.SessionUtil;
import com.td.dcts.eso.session.model.EsoJsonData;

@Controller
@Path("/content")
public class ContentController {

  /**
   * Logger
   */
  static final XLogger logger = XLoggerFactory.getXLogger(ContentController.class);


  @Value("${wcmContentLocation:WCM}")
  private String wcmContentLocation;

  @Autowired
  private SessionUtil sessionUtil;

  @Autowired
  private ContentFacade contentFacade;

  @Autowired
  private CachedObjectHelper cachedObjectHelper;

  @Autowired
  private CustomerDAO customerDAO;

  /* OCA server endpoint to fetch the WCM (application translation) content */
  @GET
  @Path("/page/{page}")
  //@GZIP
  @Produces(MediaType.APPLICATION_JSON)
  public Response getPageContent(@Context HttpServletRequest httpServletRequest, @PathParam("page") String page) throws ApiException {

    if(!("WCM".equals(wcmContentLocation) || wcmContentLocation == null)){
      throw new RuntimeException("WCM Content pull is disabled.");
//      return Response.ok("").header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
    }
    logger.info("ContentController: getPageContent: Start Retrieving WCM Content");

    // read content for locale and overrides.
    LocaleAndOverrideMap param = contentFacade.getLocaleAndOverrideMapFromSession(httpServletRequest);

    // need Cache Helper to circumvent wrong caching behavior caused by spring proxy class invocation mechanism.
    Map<String, Object> contentMap = cachedObjectHelper.getPageContent(param.getLocale(), param.getOverrideMap(), page);

    logger.info("ContentController: getPageContent: Finished Retrieving WCM Content - Sending back to client");

    return Response.ok(contentMap).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8")
      //.header(HttpHeaders.CONTENT_ENCODING, "gzip")  not gzip at this stage.
      .build();
  }

  @GET
  @Path("/getProperties/{stage}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getProperties(@Context HttpServletRequest httpServletRequest, @PathParam("stage") String stage)
    throws ApiException {
    try {
      String locale = httpServletRequest.getHeader(ExperienceConstants.HTTP_HEADER_LOCALE);
      //ToDo: Review Locale implementation in Release1-FastFollow
      if(locale==null  || locale.isEmpty()){
        EsoJsonData esoData = sessionUtil.getSessionData(httpServletRequest);
        locale = (String) esoData.get(SessionUtil.SESSION_KEY_LOCALE);
      }

      ClassLoader classLoader = getClass().getClassLoader();
      String json = IOUtils.toString(classLoader.getResourceAsStream(stage + "_" + locale + ".json"));

      return Response.ok(json, MediaType.APPLICATION_JSON).build();

    } catch(Exception e) {
      throw new ApiException(ExceptionUtil.buildErrorStatus(404, "Property file not found"));
    }
  }

  @GET
  @Path("/getProductContent")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getProductContentInfo(@Context HttpServletRequest httpServletRequest) throws ApiException {

    try {
      EsoJsonData sessionData = sessionUtil.getSessionData(httpServletRequest);
      String locale = (String) sessionData.get(SessionUtil.SESSION_KEY_LOCALE);

      @SuppressWarnings("unchecked")
      List<String> products = (List<String>) sessionData.get(SessionUtil.SESSION_KEY_PRODUCTS);

      List<Product> info = contentFacade.getProductsContent(locale, products);

      return Response.ok(info, MediaType.APPLICATION_JSON).build();
      /*
			SearchProductContentRequest searchProductContentRequest = new SearchProductContentRequest();
			searchProductContentRequest.setProductId(productId);

			MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
			headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
			headers.add(ExperienceConstants.HTTP_HEADER_CLIENT_IP, httpServletRequest.getRemoteAddr());

			List<Object> responseEntity = applicationManagementRestClient
					.retrieveProductType(searchProductContentRequest, headers);

			if (responseEntity != null) {

				if (responseEntity.size() > 0) {

					logger.debug("searchProductContent found matching product for id=" + productId);

					//ProductType response = mapper.convertValue(responseEntity.get(0), ProductType.class);

					Product content = mapper.readValue(parsed.toString(), new TypeReference<Product>() {
					});
					return Response.ok(content, MediaType.APPLICATION_JSON).build();
				}

				throw new ApiException(ExceptionUtil.buildErrorStatus(500,
						"No matching products found in database for productId=" + productId));

			} else {
				throw new ApiException(
						ExceptionUtil.buildErrorStatus(500, "Error thrown calling appmanagement/searchProductCatalog"));
			}
			*/

    } catch(ApiException e) {
      throw e;
    } catch(IOException e) {
      throw new ApiException(ExceptionUtil.buildErrorStatus(500, "Product content file is not well-formed"),
        e.getMessage());
    }
  }

  @GET
  @Path("/getAllProducts")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAllProducts(@Context HttpServletRequest httpServletRequest) throws ApiException {
    try {
      String locale = httpServletRequest.getHeader(ExperienceConstants.HTTP_HEADER_LOCALE);

      WealthClientMasterInfo wealthCMInfo = customerDAO.getWealthClientMasterInfo(sessionUtil.getDraftClientProfile(httpServletRequest));

      List<Product> products = contentFacade.getProducts(locale, wealthCMInfo);

      return Response.ok(products.toArray(new Product[]{}), MediaType.APPLICATION_JSON).build();
    } catch(IOException e) {
      throw new ApiException(ExceptionUtil.buildErrorStatus(500, "Product content file is not well-formed"), e.getMessage());
    }
  }

}
