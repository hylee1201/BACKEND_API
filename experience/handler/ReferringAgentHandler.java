package com.td.dcts.eso.experience.handler;

import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.model.ApplicationMetaData;
import com.td.dcts.eso.experience.model.associatesapi.AssociateStatus;
import com.td.dcts.eso.experience.model.print.EsoDocumentOrchestrationResponse;
import com.td.dcts.eso.experience.model.request.ApplicationManagementRestRequest;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.dcts.eso.experience.model.response.WealthClientMasterInfo;
import com.td.dcts.eso.experience.util.CommonUtil;
import com.td.dcts.eso.experience.util.ExceptionUtil;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

import static com.td.coreapi.common.config.ApiConfig.getInstance;

@Component
public class ReferringAgentHandler {

  private static final String associateApiUrl = getInstance()
    .getPropertiesConfig(ExperienceConstants.ENV_FILE_NAME)
    .getProperty(ExperienceConstants.REFERRING_AGENT_URL);

  @Autowired
  private RestTemplate restTemplate;

  private static final XLogger LOGGER = XLoggerFactory.getXLogger(SignatureHandler.class);

  public ResponseEntity<AssociateStatus> retrieveAssociateStatus(MetaData metaData, HttpHeaders httpHeaders,String idTypeCd, String acf2id) throws ApiException {
    ApplicationManagementRestRequest restRequest = new ApplicationManagementRestRequest();
    restRequest.setMetaData(metaData);
    HttpEntity<ApplicationManagementRestRequest> httpEntity = new HttpEntity<>(restRequest, httpHeaders);

    String url = associateApiUrl+"/"+idTypeCd+"/"+acf2id;
    LOGGER.info("Posting to : " + url);

    ResponseEntity<AssociateStatus> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, AssociateStatus.class);

    if (responseEntity.getStatusCode() != HttpStatus.OK) {
      if (responseEntity.getStatusCode() == HttpStatus.BAD_REQUEST) {
        LOGGER.error("ReferringAgentHandler: Bad request went to App Engine");
        throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), responseEntity.getBody().toString()));
      } else {
        LOGGER.error("ReferringAgentHandler: Response comes back unsuccessfully from App Engine");
        throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.toString()));
      }
    } else {
      return responseEntity;
    }
  }
}
