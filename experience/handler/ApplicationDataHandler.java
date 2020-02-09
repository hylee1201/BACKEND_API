package com.td.dcts.eso.experience.handler;

import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.client.ApplicationManagementRestClient;
import com.td.dcts.eso.experience.model.response.MetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

@Component
public class ApplicationDataHandler {
  @Autowired
  private ApplicationManagementRestClient applicationManagementRestClient;


  /**
   *
   * @param metaData
   * @param draftWcm
   * @param httpHeaders
   * @return JSON format string
   * @throws ApiException
   */
  public ResponseEntity<String> getApplicationResponse(MetaData metaData, Object draftWcm, MultiValueMap<String, String> httpHeaders, String url) throws ApiException {
    ResponseEntity<String> responseEntity = applicationManagementRestClient.getTextResponse(metaData, draftWcm, httpHeaders, url);
    return responseEntity;
  }
}
