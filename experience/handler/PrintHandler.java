package com.td.dcts.eso.experience.handler;

import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.model.print.EsoDocumentOrchestrationResponse;
import com.td.dcts.eso.experience.model.request.ApplicationManagementRestRequest;
import com.td.dcts.eso.experience.model.response.ApplicationInfo;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.dcts.eso.experience.model.response.WealthClientMasterInfo;
import com.td.dcts.eso.experience.util.ExceptionUtil;
import com.td.dcts.eso.experience.util.SessionUtil;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.td.coreapi.common.config.ApiConfig.getInstance;

@Component
public class PrintHandler {

  private static final String printUrl = getInstance()
    .getPropertiesConfig(ExperienceConstants.ENV_FILE_NAME)
    .getProperty(ExperienceConstants.PRINT_PACKAGE_URL);

  private static final String printDocListUrl = getInstance()
    .getPropertiesConfig(ExperienceConstants.ENV_FILE_NAME)
    .getProperty(ExperienceConstants.PRINT_DOCLIST_URL);

  @Autowired
  private RestTemplate restTemplate;

  private static final XLogger LOGGER = XLoggerFactory.getXLogger(SignatureHandler.class);

  public ResponseEntity<EsoDocumentOrchestrationResponse> printPackage(WealthClientMasterInfo wcm, MetaData metaData, HttpHeaders httpHeaders) throws ApiException {

    LOGGER.info("Posting to : " + printUrl);
    ResponseEntity<EsoDocumentOrchestrationResponse> responseEntity = restTemplate.exchange(printUrl, HttpMethod.POST, prepareHttpEntity(wcm,metaData,httpHeaders), EsoDocumentOrchestrationResponse.class);

    return processReturn(responseEntity);
  }

  public WealthClientMasterInfo updateDocListInWcm(WealthClientMasterInfo wcm, List<String>  docList) throws ApiException{
    //also set print list to applicationInfo applicationPackage
    if (wcm.getApplicationInfo() != null) {
      wcm.getApplicationInfo().setApplicationPackage(docList);
    }else{
      ApplicationInfo applicationInfo = new ApplicationInfo();
      applicationInfo.setApplicationPackage(docList);
      wcm.setApplicationInfo(applicationInfo);
    }
  return wcm;
  }

  public List<String> printDocList(HttpServletRequest httpServletRequest, WealthClientMasterInfo wcm, MetaData metaData, HttpHeaders httpHeaders) throws ApiException{
    LOGGER.info("Posting to : " + printDocListUrl);

    ResponseEntity<List<String>> responseEntity = restTemplate.exchange(printDocListUrl, HttpMethod.POST, prepareHttpEntity(wcm, metaData, httpHeaders), new ParameterizedTypeReference<List<String>>() {
    });
    return (List<String>) processReturn(responseEntity).getBody();
  }

  private HttpEntity<ApplicationManagementRestRequest> prepareHttpEntity(WealthClientMasterInfo wcm, MetaData metaData, HttpHeaders httpHeaders) {
    ApplicationManagementRestRequest restRequest = new ApplicationManagementRestRequest();
    restRequest.setMetaData(metaData);
    restRequest.setData(wcm);

    HttpEntity<ApplicationManagementRestRequest> httpEntity = new HttpEntity<>(restRequest, httpHeaders);
    return httpEntity;
  }

  private  ResponseEntity processReturn(ResponseEntity responseEntity) throws ApiException{
    if (responseEntity.getStatusCode() != HttpStatus.OK) {
      if (responseEntity.getStatusCode() == HttpStatus.BAD_REQUEST) {
        LOGGER.error("PrintHandler: Bad request went to App Engine");
        throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), responseEntity.getBody().toString()));
      } else {
        LOGGER.error("PrintHandler: Response comes back unsuccessfully from App Engine");
        throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.toString()));
      }
    } else {
      return responseEntity;
    }
  }


}
