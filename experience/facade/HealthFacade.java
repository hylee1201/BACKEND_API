package com.td.dcts.eso.experience.facade;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.td.coreapi.common.config.ApiConfig;
import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.model.HealthCheck;
import com.td.dcts.eso.experience.model.request.ApplicationManagementRestRequest;
import com.td.dcts.eso.experience.util.CustomErrorHandler;
import com.td.dcts.eso.experience.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;


@Component
public class HealthFacade {

  @Autowired
  private RestTemplate restTemplate;

  public List<HealthCheck> retrieveHealth(HttpServletRequest httpServletRequest) throws ApiException {
    ObjectMapper objectMapper = new ObjectMapper();
    List<HealthCheck> healthChecks = new ArrayList<>();
    try {
      restTemplate.setErrorHandler(new CustomErrorHandler());

      String healthCheck = ApiConfig.getInstance().getStringFromFile("healthCheck.json");
    //  String healthCheckDcd = (new String(Base64.getDecoder().decode(healthCheck )));
      Map<String,HealthCheck> runCheck = objectMapper.readValue(healthCheck , new TypeReference<Map<String, HealthCheck>>() {});
      for (Map.Entry<String, HealthCheck> checkMe : runCheck.entrySet()) {
        String key = checkMe.getKey();
        HealthCheck healthCheckItem = checkMe.getValue();
        HttpEntity<?> requestEntity = null;
        HttpMethod httpMethod;
        if("POST".equalsIgnoreCase(healthCheckItem.getMethod()))
        {
          httpMethod = HttpMethod.POST;
          requestEntity = objectMapper.readValue(healthCheckItem.getRequestEntity(), new TypeReference<HttpEntity<?>>(){}) ;
        }
        else
        {
          httpMethod = HttpMethod.GET;
        }
        healthCheckItem.setLastRunTime(new java.util.Date().toString());
        ResponseEntity<Object> responseEntity = null;
        try {
          responseEntity = restTemplate.exchange(healthCheckItem.getURL(), httpMethod, requestEntity, Object.class);
          healthCheckItem.setSuccessFlag("SUCCESS");
          healthCheckItem.setResponse(objectMapper.writeValueAsString(responseEntity));
        }
        catch (Exception e2){
       try {
         healthCheckItem.setSuccessFlag("FAIL");
         healthCheckItem.setResponse(objectMapper.writeValueAsString(responseEntity));
       }
       catch(Exception e3){

           }
        }
        healthChecks.add(healthCheckItem);
      }

    } catch (Exception e) {
      throw new ApiException(ExceptionUtil.buildServerErrorStatus(), e);
    }


    return healthChecks;
  }
}
