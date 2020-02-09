package com.td.dcts.eso.experience.environmentjs.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.environmentjs.model.EnvironmentJSOutput;
import com.td.dcts.eso.experience.model.response.MetaData;
import org.springframework.http.HttpHeaders;

public interface EnvironmentJSFacade {
  EnvironmentJSOutput prepareEnvironmentJS(MetaData metaData, HttpHeaders header, String locale) throws ApiException, JsonProcessingException;
}
