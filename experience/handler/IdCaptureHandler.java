package com.td.dcts.eso.experience.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.td.coreapi.common.config.ApiConfig;
import com.td.dcts.eso.experience.helper.FileUploadForm;
import com.td.dcts.eso.experience.model.response.IDCaptureInfo;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.dcts.eso.experience.model.response.WealthClientMasterInfo;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Base64;

@Component
public class IdCaptureHandler {

  static final XLogger LOGGER = XLoggerFactory.getXLogger(IdCaptureHandler.class);

  @Value("${id.capture.file.upload.url}")
  private String idCaptureFileUploadUrl;

  @Autowired
  @Qualifier("restTemplateIdCapture")
  private RestTemplate restTemplate;

//ToDo  Fix the duplication of code below in FastFollow release

  public IDCaptureInfo verifyUpload(String idUploadVerifyUrl, String session, String verificationKey, WealthClientMasterInfo wealthClientMasterInfoFromSession, String appID, String subAppID, FileUploadForm form) throws IOException {
    LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    if (wealthClientMasterInfoFromSession != null) {
      ObjectMapper mapper = ApiConfig.getInstance().getMapper();
      String wealthClientMasterInfoFromSessionJson = mapper.writeValueAsString(wealthClientMasterInfoFromSession);
      map.add("profile", wealthClientMasterInfoFromSessionJson);
    }
    map.add("idImage", form.getData());
    map.add("fileName", form.getFileName());
    map.add("fileType", form.getFileType());
    map.add("imageSize", form.getImageSize());
    map.add("appId", appID);
    map.add("subAppId", subAppID);
    map.add("verificationKey", verificationKey);


    return idCaptureEndpointCall(map, idUploadVerifyUrl, session,  form);
  }

  public IDCaptureInfo persistFile(String idCaptureFileUploadUrl, WealthClientMasterInfo wealthClientMasterInfoFromSession, String appID, String subAppID, FileUploadForm form, MetaData metaData) throws IOException {
    UriComponentsBuilder uriComponents = UriComponentsBuilder.newInstance()
      .fromHttpUrl(idCaptureFileUploadUrl).queryParam("appID", appID)
      .queryParam("subAppID", subAppID);

    LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    if (wealthClientMasterInfoFromSession != null) {
      ObjectMapper mapper = ApiConfig.getInstance().getMapper();
      String wealthClientMasterInfoFromSessionJson = mapper.writeValueAsString(wealthClientMasterInfoFromSession);
      map.add("wealthClientMasterInfoFromSession", wealthClientMasterInfoFromSessionJson);
    }
    map.add("idImage", form.getData());
    map.add("fileName", form.getFileName());
    map.add("fileType", form.getFileType());
    map.add("fileSize", form.getImageSize());
    map.add("appId", appID);
    map.add("subAppId", subAppID);


    return idCaptureEndpointCall(map, uriComponents.toUriString(), null, form);
  }

  //ToDo: Refactor the parameters to the function below
  private IDCaptureInfo idCaptureEndpointCall(LinkedMultiValueMap<String, Object> map , String idCaptureFileUploadUrl, String session, FileUploadForm form) throws IOException {

    LOGGER.info("App engine IdCapture call URL: {} fileName {} ", idCaptureFileUploadUrl, form.getFileName());

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    headers.add("Cookie", "TDESOSESSIONID=" + session);

    HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);


    return restTemplate.exchange(idCaptureFileUploadUrl, HttpMethod.POST, requestEntity, IDCaptureInfo.class).getBody();
  }

  public IDCaptureInfo sendIdCaptureandReturndUpdatedWealthModel(WealthClientMasterInfo wealthClientMasterInfoFromSession, String appId, String subAppId, FileUploadForm form, String session) throws IOException {
    LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();

    ObjectMapper mapper = ApiConfig.getInstance().getMapper();
    String wealthClientMasterInfoFromSessionJson = mapper.writeValueAsString(wealthClientMasterInfoFromSession);
    // Apply Base64 encode so that French can be passed properly
    map.add("wealthClientMasterInfoFromSession", new String(Base64.getEncoder().encode(wealthClientMasterInfoFromSessionJson.getBytes())));
    map.add("idImage", form.getData());
    map.add("fileName", form.getFileName());
    map.add("fileType", form.getFileType());
    map.add("fileSize", form.getImageSize());


    LOGGER.info("App engine IdCapture call URL: {} fileName {} ", idCaptureFileUploadUrl, form.getFileName());

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    headers.set("SESSION_ID", session);

    HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(
      map, headers);

    UriComponentsBuilder uriComponents = UriComponentsBuilder.newInstance()
      .fromHttpUrl(idCaptureFileUploadUrl).queryParam("appID", appId)
      .queryParam("subAppID", subAppId);

    return restTemplate.exchange(uriComponents.toUriString(), HttpMethod.POST, requestEntity, IDCaptureInfo.class).getBody();
  }

}
