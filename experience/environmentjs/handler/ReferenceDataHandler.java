package com.td.dcts.eso.experience.environmentjs.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.model.request.ApplicationManagementRestRequest;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.dcts.eso.experience.util.CustomErrorHandler;
import com.td.dcts.eso.experience.util.ExceptionUtil;
import com.td.dcts.eso.experience.util.ReferenceModelDeserializer;
import com.td.dcts.eso.interceptor.APIRequestInterceptor;
import com.td.eso.rest.response.model.LookupModel;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class ReferenceDataHandler {

 private RestTemplate referenceDataRestTemplate ;
 private APIRequestInterceptor apiRequestInterceptor;

  @Value("${resturl.lookupManager.lists.retrieve}")
  private String referenceDataUrl;

  static final XLogger LOGGER = XLoggerFactory.getXLogger(ReferenceDataHandler.class);

  public <T> T callReferenceDataApi(MetaData metaData, HttpHeaders headers, ParameterizedTypeReference<T> responseType) throws ApiException {
    LOGGER.debug("Call App engine with URL: {}", referenceDataUrl);
    referenceDataRestTemplate = new RestTemplate();
    referenceDataRestTemplate.setInterceptors(Arrays.asList((ClientHttpRequestInterceptor) apiRequestInterceptor));
    referenceDataRestTemplate.setErrorHandler(new CustomErrorHandler());
    addMessageConverterToRestTemplate(referenceDataRestTemplate);

    ApplicationManagementRestRequest restRequest = new ApplicationManagementRestRequest();
    restRequest.setMetaData(metaData);
    HttpEntity<ApplicationManagementRestRequest> httpEntity = new HttpEntity<>(restRequest, headers);

    ResponseEntity<T> responseEntity = referenceDataRestTemplate.exchange(referenceDataUrl, HttpMethod.POST, httpEntity, responseType);

    if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
      return responseEntity.getBody();
    } else {
      LOGGER.error("App engine URL: {} return error code {} ", referenceDataUrl, responseEntity.getStatusCode());
      throw new ApiException(ExceptionUtil.buildErrorStatusFromApiErrorStatus(responseEntity));
    }
  }


  private void addMessageConverterToRestTemplate(RestTemplate restTemplate){
    ObjectMapper mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addDeserializer(LookupModel.class, new ReferenceModelDeserializer());
    mapper.registerModule(module);

    //find and replace Jackson message converter with our own
    for (HttpMessageConverter<?> httpMessageConverter: restTemplate.getMessageConverters()) {
      if (httpMessageConverter instanceof MappingJackson2HttpMessageConverter){
        ((MappingJackson2HttpMessageConverter) httpMessageConverter).setObjectMapper(mapper);
      }
    }
  }

  @Autowired
  public void setApiRequestInterceptor(APIRequestInterceptor apiRequestInterceptor) {
    this.apiRequestInterceptor = apiRequestInterceptor;
  }
}
