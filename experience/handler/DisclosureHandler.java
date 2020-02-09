package com.td.dcts.eso.experience.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.disclosureadapter.AssembleDisclosureRequest;
import com.td.dcts.eso.disclosureadapter.AssembleDisclosureResponse;
import com.td.dcts.eso.disclosureadapter.AssembleDisclosureService;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.helper.EventHelper;
import com.td.dcts.eso.experience.model.request.disclosure.GetDisclosureListRequest;
import com.td.dcts.eso.experience.model.response.GetDisclosureListResponse;
import com.td.dcts.eso.experience.util.CustomErrorHandler;
import com.td.dcts.eso.experience.util.ExceptionUtil;
import com.td.eso.util.FeatureToggle;
import org.apache.commons.io.IOUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;


@Service
public class DisclosureHandler {

  static final XLogger LOGGER = XLoggerFactory.getXLogger(DisclosureHandler.class);

  private AssembleDisclosureService assembleDisclosureService ;

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  EventHelper eventHelper;

  @Value("${disclosureapp.retrieve.url}")
  String disclosureAppUrl;

  public GetDisclosureListResponse getDisclosures(HttpHeaders httpHeaders,GetDisclosureListRequest getDisclosureListRequest ) throws ApiException {

    restTemplate.setErrorHandler(new CustomErrorHandler());

    @SuppressWarnings({ "rawtypes", "unchecked" })
    HttpEntity httpEntity = new HttpEntity<GetDisclosureListRequest>(getDisclosureListRequest,httpHeaders);

    LOGGER.debug("About to call disclosure App for product id"+getDisclosureListRequest.getProductsList().get(0).getPegaProductID());
    ResponseEntity<GetDisclosureListResponse> responseEntity = restTemplate.exchange(disclosureAppUrl, HttpMethod.POST, httpEntity, GetDisclosureListResponse.class);
    LOGGER.debug("Retrieved from Disclosure App for product id"+getDisclosureListRequest.getProductsList().get(0).getPegaProductID());

    return responseEntity.getBody();
  }
  private String getErrorMsg(String code, String detail) {
    String msg = "DisclosureHandlerError:" + code;
    return detail != null? msg + " [" + detail + "]": msg;
  }

  @Cacheable(value = ExperienceConstants.DISCLOSURE_DOCUMENT_CACHE,
    key="#assembleDisclosureRequest.getDisclosure().getDisclosureDocId() + #assembleDisclosureRequest.getLanguage() + #assembleDisclosureRequest.getDisclosure().getMetaData()")
    public AssembleDisclosureResponse getDisclosure(AssembleDisclosureRequest assembleDisclosureRequest  ) throws ApiException, JsonProcessingException {

      assembleDisclosureService = new AssembleDisclosureService();

      LOGGER.debug("About to call disclosure service for product id"+assembleDisclosureRequest.getDisclosure().getDisclosureDocId());

      try {
        AssembleDisclosureResponse assembleDisclosureResponse;
        if (FeatureToggle.DISCLOSURE_RETRIEVE_ENABLE()) {
          assembleDisclosureResponse = assembleDisclosureService.assembleDisclosure(assembleDisclosureRequest);
        }
        else
        {
          //Get Mock Disclosure Data
          assembleDisclosureResponse= new ObjectMapper().readValue(getFile("assembleDisclosureResponse.json"), AssembleDisclosureResponse.class);
          assembleDisclosureResponse.getDisclosure().setDisclosureDocId(assembleDisclosureRequest.getDisclosure().getDisclosureDocId());
          assembleDisclosureResponse.getDisclosure().setContentTypeCd(assembleDisclosureRequest.getDisclosure().getContentTypeCd());
        }

        if (assembleDisclosureResponse.isError()) {
          String msg = getErrorMsg("AssembleDisclosureService", assembleDisclosureResponse.getErrorMessage());
          LOGGER.error(msg);
          throw new ApiException(ExceptionUtil.buildServerErrorStatus());
        }
        LOGGER.debug("Retrieved from Disclosure service for product id"+assembleDisclosureRequest.getDisclosure().getDocumentTypeCD());

        return assembleDisclosureResponse;

      } catch (Exception e) {
        String msg = getErrorMsg("GetDisclosureError", assembleDisclosureRequest.getDisclosure().getDocumentTypeCD() + "," + assembleDisclosureRequest.getDisclosure().getDisclosureDocId());
        LOGGER.error(msg, e);
        throw new ApiException(ExceptionUtil.buildServerErrorStatus(), msg, e);
      }
  }

  private static String getFile(String path) {
    String result = "";

    ClassLoader classLoader = DisclosureHandler.class.getClassLoader();
    try {
      result = IOUtils.toString(classLoader.getResourceAsStream(path));
    } catch (IOException e) {
      e.printStackTrace();
    }

    return result;
  }


}
