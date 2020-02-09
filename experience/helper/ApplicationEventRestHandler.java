package com.td.dcts.eso.experience.helper;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.td.dcts.eso.events.EsoCreateApplicationRequestBuilder;
import com.td.dcts.eso.events.createapplication.request.EsoCreateApplicationRequest;
import com.td.dcts.eso.events.createapplication.response.EsoCreateApplicationResponse;
import com.td.dcts.eso.experience.util.ExceptionUtil;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.td.coreapi.common.config.ApiConfig;
import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.util.CustomErrorHandler;
import com.td.dcts.eso.experience.model.response.MetaData;

@Service
public class ApplicationEventRestHandler {

  @Autowired
  private RestTemplate restTemplate;

  private ObjectMapper objectMapper = new ObjectMapper();

  static final XLogger LOGGER = XLoggerFactory.getXLogger(ApplicationEventRestHandler.class);

  public EsoCreateApplicationResponse makeCreateApplicationEventCall(MultiValueMap<String, String> httpHeaders, Map<String, Object> parameters, MetaData metaData) throws ApiException, JsonProcessingException {

    restTemplate.setErrorHandler(new CustomErrorHandler());

    EsoCreateApplicationRequest esoCreateApplicationRequest = new EsoCreateApplicationRequestBuilder()
      .setProductId(metaData.getProductId())
      .setEventMetaDataSchemaCD("-1")	// TODO - TBD
      .setEventMetaData(objectMapper.writeValueAsString(populateAppData(parameters)))
      .setCreateSubAppInd(true)
      .setCreateEventInd(true)
      .setResumeInd(false)
      .createEsoCreateApplicationRequest();

    HttpEntity<?> requestEntity = new HttpEntity<>(esoCreateApplicationRequest, httpHeaders);

    String appCreateEventUrl = ApiConfig.getInstance()
      .getPropertiesConfig(ExperienceConstants.ENV_FILE_NAME)
      .getProperty(ExperienceConstants.CREATE_APPLICATION_EVENT_URL);

    ResponseEntity<EsoCreateApplicationResponse> responseEntity = restTemplate.exchange(appCreateEventUrl,
      HttpMethod.POST, requestEntity, EsoCreateApplicationResponse.class);

    if (!HttpStatus.OK.equals(responseEntity.getStatusCode())){
      throw new ApiException(ExceptionUtil.buildServerErrorStatus());
    }

    EsoCreateApplicationResponse body = responseEntity.getBody();
    LOGGER.debug("New application created, ID: {}", body.getApplication().getApplicationId());

    return body;
  }

  private Map<String, Object> populateAppData(Map<String, Object> parameters) {
    Map<String, Object> data = new HashMap<>();
    data.put("marketingCode",parameters.get("marketingCode"));
    data.put("sourceCode",parameters.get("sourceCode"));
    data.put("sourceUrl",parameters.get("sourceUrl"));
    data.put("locale",parameters.get("locale"));
    data.put("channelId",parameters.get("channelId"));
    data.put("productId",parameters.get("productId"));
    return data;
  }

}
