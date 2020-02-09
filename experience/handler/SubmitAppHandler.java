package com.td.dcts.eso.experience.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.event.response.model.SubApplicationInfo;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.dao.CustomerDAO;
import com.td.dcts.eso.experience.model.request.ApplicationManagementRestRequest;
import com.td.dcts.eso.experience.model.response.FileNetDocument;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.dcts.eso.experience.model.response.WealthClientMasterInfo;
import com.td.dcts.eso.experience.util.CustomErrorHandler;
import com.td.dcts.eso.experience.util.ExceptionUtil;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;

import static com.td.coreapi.common.config.ApiConfig.getInstance;

@Service
public class SubmitAppHandler {

  private static final String pdfURL = getInstance()
    .getPropertiesConfig(ExperienceConstants.ENV_FILE_NAME)
    .getProperty(ExperienceConstants.SUBMITAPP_APP_RETRIEVE_PDF);

  @Autowired
  @Qualifier("restTemplatePdf")
  private RestTemplate restTemplate;

  @Autowired
  private CustomerDAO customerDao;

  private static final XLogger LOGGER = XLoggerFactory.getXLogger(SubmitAppHandler.class);

  /**
   * Makes the down stream service call and returns the response back.
   *
   * @param subAppID             TODO
   * @param MultiValueMap        <String, String> HttpHeaders
   * @param appManagementRequest ApplicationManagementRestRequest
   * @param url                  String
   * @return ResponseEntity
   * @throws ApiException
   */

  public ResponseEntity<byte[]> retrievePDF(List<FileNetDocument> fileNetDocuments, MetaData metaData, MultiValueMap<String, String> httpHeaders) throws ApiException {
    restTemplate.setErrorHandler(new CustomErrorHandler());

    UriComponentsBuilder builder = null;

    builder = UriComponentsBuilder.fromHttpUrl(pdfURL);

    ApplicationManagementRestRequest restRequest = new ApplicationManagementRestRequest();
    restRequest.setMetaData(metaData);
    restRequest.setData(fileNetDocuments);

    HttpEntity httpEntity = new HttpEntity(restRequest, httpHeaders);

    //Add content type pdf for the stream that we are expecting
    /*
    List<MediaType> supportedApplicationTypes = new ArrayList<>();
    MediaType pdfApplication = new MediaType("application", "pdf", StandardCharsets.UTF_8);
    supportedApplicationTypes.add(pdfApplication);

    ByteArrayHttpMessageConverter byteArrayHttpMessageConverter = new ByteArrayHttpMessageConverter();
    byteArrayHttpMessageConverter.setSupportedMediaTypes(supportedApplicationTypes);
    List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
    messageConverters.add(byteArrayHttpMessageConverter);
    restTemplate.setMessageConverters(messageConverters);
    */
    ResponseEntity<byte[]> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, httpEntity, byte[].class);

    return responseEntity;
  }

  /**
   * Makes the down stream service call and returns the response back.
   *
   * @param MultiValueMap        <String, String> HttpHeaders
   * @param appManagementRequest ApplicationManagementRestRequest
   * @param url                  String
   * @return ResponseEntity
   * @throws ApiException
   */

  //ToDo. Raj Change the return type from Object to Boolean once the testing of Process API is complete
  public WealthClientMasterInfo submit(MetaData metaData, MultiValueMap<String, String> httpHeaders, WealthClientMasterInfo wcm) throws ApiException, IOException {
    restTemplate.setErrorHandler(new CustomErrorHandler());
    String subAppID;
    Integer appID = metaData.getApplicationId();
    subAppID = null;
    List<SubApplicationInfo> subAppIDs = metaData.getSubApplicationList();
    if(subAppIDs != null) {
      if(!subAppIDs.isEmpty()) {
        subAppID = subAppIDs.get(0).getSubApplicationId();
      }
    }

    String url = getInstance()
      .getPropertiesConfig(ExperienceConstants.ENV_FILE_NAME)
      .getProperty(ExperienceConstants.SUBMITAPP_APP_SUBMIT);

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

    if(subAppID != null && !subAppID.isEmpty()) {
      builder.queryParam("subappID", subAppID);
    }
    if(appID != null) {
      builder.queryParam("appID", appID);
    }

    ApplicationManagementRestRequest restRequest = new ApplicationManagementRestRequest();
    restRequest.setMetaData(metaData);
    restRequest.setData(wcm);

    HttpEntity<ApplicationManagementRestRequest> httpEntity = new HttpEntity<>(restRequest,httpHeaders);

    WealthClientMasterInfo wealthClientMasterInfo = new WealthClientMasterInfo();

    LOGGER.info("Posting to : " + builder.build().encode().toUri());
    ResponseEntity<Object> responseEntity = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, httpEntity, Object.class);

    if(responseEntity.getStatusCode() != HttpStatus.OK) {
      if (responseEntity.getStatusCode() == HttpStatus.BAD_REQUEST) {
        LOGGER.error("SubmitAppHandler: Bad request went to App Engine");
        throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), responseEntity.getBody().toString()));
      } else {
        LOGGER.error("SubmitAppHandler: Response comes back unsuccessfully from App Engine");
        throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.toString()));
      }
    } else {
      wealthClientMasterInfo = customerDao.getWealthClientMasterInfo(new ObjectMapper().writeValueAsString(responseEntity.getBody()));
    }

    return wealthClientMasterInfo;
  }
}

