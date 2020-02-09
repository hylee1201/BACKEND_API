package com.td.dcts.eso.experience.handler;

import com.google.common.base.Strings;
import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.model.esignlive.ESignaturePackage;
import com.td.dcts.eso.experience.model.request.ApplicationManagementRestRequest;
import com.td.dcts.eso.experience.model.response.AboutYou;
import com.td.dcts.eso.experience.model.response.FileNetDocument;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.dcts.eso.experience.model.response.WealthClientMasterInfo;
import com.td.dcts.eso.experience.util.CommonUtil;
import com.td.dcts.eso.experience.util.ExceptionUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static com.td.coreapi.common.config.ApiConfig.getInstance;

@Service
public class SignatureHandler {

  private static final String signUrl = getInstance()
    .getPropertiesConfig(ExperienceConstants.ENV_FILE_NAME)
    .getProperty(ExperienceConstants.SIGNATURE_URL);

  private static final String coApplicantsignUrl = getInstance()
    .getPropertiesConfig(ExperienceConstants.ENV_FILE_NAME)
    .getProperty(ExperienceConstants.COAPPLICANT_SIGNATURE_URL);

  private static final String filenetSendUrl = getInstance()
    .getPropertiesConfig(ExperienceConstants.ENV_FILE_NAME)
    .getProperty(ExperienceConstants.SIGNATURE_SEND_TO_FILENET_URL);

  private static String packageName_EN = "TD New Account Application";
  private static String packageName_FR = "Le demande d’ouverture de compte TD";

  @Autowired
  private RestTemplate restTemplate;

  private static final XLogger LOGGER = XLoggerFactory.getXLogger(SignatureHandler.class);

  public ResponseEntity<WealthClientMasterInfo> launcheSign(
    WealthClientMasterInfo wcm,
    MetaData metaData,
    MultiValueMap<String, String> httpHeaders) throws ApiException {

    ApplicationManagementRestRequest restRequest = new ApplicationManagementRestRequest();
    restRequest.setMetaData(metaData);
    restRequest.setData(wcm);
    HttpEntity<ApplicationManagementRestRequest> httpEntity = new HttpEntity<>(restRequest, httpHeaders);

    LOGGER.info("Posting to : " + signUrl);
    ResponseEntity<WealthClientMasterInfo> responseEntity = restTemplate.exchange(signUrl, HttpMethod.POST, httpEntity, WealthClientMasterInfo.class);

    if(responseEntity.getStatusCode() != HttpStatus.OK) {
      if (responseEntity.getStatusCode() == HttpStatus.BAD_REQUEST) {
        LOGGER.error("SignatureHandler: Bad request went to App Engine");
        throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), responseEntity.getBody().toString()));
      } else {
        LOGGER.error("SignatureHandler: Response comes back unsuccessfully from App Engine");
        throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.toString()));
      }
    } else {
      return responseEntity;
    }
  }

  public ResponseEntity<ESignaturePackage> launcheCoApplicantSign(
    String profileId,
    WealthClientMasterInfo wcm,
    MetaData metaData,
    MultiValueMap<String, String> httpHeaders) throws ApiException {

    ApplicationManagementRestRequest restRequest = new ApplicationManagementRestRequest();
    restRequest.setMetaData(metaData);
    HttpEntity<ApplicationManagementRestRequest> httpEntity = new HttpEntity<>(restRequest, httpHeaders);

    String packageId = wcm.getApplicationInfo().getSignedDocumentPackageId();
    AboutYou profile = CommonUtil.getProfile(wcm, profileId);
    if (Strings.isNullOrEmpty(packageId) || profile == null || profile.getContactInfo() == null
      || Strings.isNullOrEmpty(profile.getContactInfo().getEmail())) {
      throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), "eSign Package ID/CoApplicant Email is empty."));
    }
    String email = profile.getContactInfo().getEmail();
    String coapplicantURL = coApplicantsignUrl + "/" + packageId + "/" + email;

    LOGGER.info("Posting to : " + coapplicantURL);
    ResponseEntity<ESignaturePackage> cosignResponse = restTemplate.exchange(coapplicantURL, HttpMethod.POST, httpEntity, ESignaturePackage.class);

    if(cosignResponse.getStatusCode() != HttpStatus.OK) {
      if (cosignResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
        LOGGER.error("SignatureHandler: Bad request went to App Engine");
        throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), cosignResponse.getBody().toString()));
      } else {
        LOGGER.error("SignatureHandler: Response comes back unsuccessfully from App Engine");
        throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.toString()));
      }
    } else {
      return cosignResponse;
    }
  }


  public List<FileNetDocument> sendPackageToFilenet(
      WealthClientMasterInfo wcm,
      MetaData metaData,
      MultiValueMap<String, String> httpHeaders) throws ApiException {

    Map<String, Object> restRequest = new HashMap<>();
    restRequest.put("metaData", metaData);
    restRequest.put("data", wcm);

    HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(restRequest, httpHeaders);

    LOGGER.info("Posting to : " + filenetSendUrl);
    ResponseEntity<List<FileNetDocument>> responseEntity = restTemplate.exchange(filenetSendUrl, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<FileNetDocument>>(){});

    if(responseEntity.getStatusCode() != HttpStatus.OK) {
      if (responseEntity.getStatusCode() == HttpStatus.BAD_REQUEST) {
        LOGGER.error("SignatureHandler: Bad request went to App Engine");
        throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), responseEntity.getBody().toString()));
      } else {
        LOGGER.error("SignatureHandler: Response comes back unsuccessfully from App Engine");
        throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.toString()));
      }
    } else {
      return responseEntity.getBody();
    }

  }
}

