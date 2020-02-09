package com.td.dcts.eso.experience.facade;


import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.util.CustomErrorHandler;
import com.td.dcts.eso.experience.util.ExceptionUtil;
import com.td.eso.rest.response.model.CancelApplicationResponse;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Component
public class CancelApplicationFacade {

  @Autowired
  private RestTemplate restTemplate;

  @Value("${resturl.application.cancel}")
  String applicationCancelURL;

  static final XLogger LOGGER = XLoggerFactory.getXLogger(CancelApplicationFacade.class);

  public ResponseEntity<CancelApplicationResponse> cancelApplication(String appId,String subAppId) throws ApiException {
    CancelApplicationResponse cancelResponse = new CancelApplicationResponse();
    Map<String, String> uriParams = new HashMap<String, String>();
    uriParams.put("applicationId", appId);
    cancelResponse.setApplicationId(appId);
    uriParams.put("subApplicationId",subAppId);
    cancelResponse.setSubAppId(subAppId);
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(applicationCancelURL);
    URI cancelURI = builder.buildAndExpand(uriParams).toUri();

    restTemplate.setErrorHandler(new CustomErrorHandler());
    ResponseEntity<String> response = restTemplate.exchange(cancelURI, HttpMethod.DELETE, null,String.class);
    if (HttpStatus.OK.equals(response.getStatusCode())) {
      cancelResponse.setResponseId(response.getBody());
      return ResponseEntity.ok().body(cancelResponse);
    } else {
      LOGGER.error("App engine URL: {} return error code {} ", cancelURI, response.getStatusCode());
      throw new ApiException(ExceptionUtil.buildErrorStatusFromApiErrorStatus(response));
    }
  }

}
