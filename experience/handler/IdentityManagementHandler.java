package com.td.dcts.eso.experience.handler;

import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.event.response.model.SubApplicationInfo;
import com.td.dcts.eso.experience.model.identity.Identity;
import com.td.dcts.eso.experience.model.identity.IdentityRequest;
import com.td.dcts.eso.experience.model.request.ApplicationManagementRestRequest;
import com.td.dcts.eso.experience.model.response.AccountNumber;
import com.td.dcts.eso.experience.model.response.IdentityManagementResponse;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.dcts.eso.experience.model.response.WealthClientMasterInfo;
import com.td.dcts.eso.experience.util.CustomErrorHandler;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers.data;

@Service
public class IdentityManagementHandler {

  @Autowired
  private RestTemplate restTemplate;

  @Value("${resturl.identityManagement.reserve}")
  String identityManagementUrl;

  static final XLogger LOGGER = XLoggerFactory.getXLogger(AccountDetailsHandler.class);


  public IdentityManagementResponse setupIdentity(HttpHeaders httpHeaders, MetaData metaData, Identity identity, WealthClientMasterInfo wcm) throws ApiException {
    String subAppID;
    String appID = metaData.getApplicationId().toString();
    subAppID = "";
    List<SubApplicationInfo> subAppIDs = metaData.getSubApplicationList();
    if(subAppIDs != null) {
      if(!subAppIDs.isEmpty()) {
        subAppID = subAppIDs.get(0).getSubApplicationId();
      }
    }

    restTemplate.setErrorHandler(new CustomErrorHandler());
    @SuppressWarnings({"rawtypes", "unchecked"})

    ApplicationManagementRestRequest restRequest = new ApplicationManagementRestRequest();
    restRequest.setMetaData(metaData);
    IdentityRequest identityRequest = new IdentityRequest();
    identityRequest.setIdentity(identity);
    restRequest.setData(identityRequest);
    restRequest.setConsentData(wcm);
    HttpEntity<ApplicationManagementRestRequest> httpEntity = new HttpEntity<>(restRequest, httpHeaders);
    ResponseEntity<IdentityManagementResponse> responseEntity = restTemplate.exchange(identityManagementUrl, HttpMethod.POST, httpEntity, IdentityManagementResponse.class);
    LOGGER.exit(responseEntity);
    return responseEntity.getBody();

  }

}
