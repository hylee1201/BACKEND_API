package com.td.dcts.eso.experience.handler;

import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.client.ApplicationManagementRestClient;
import com.td.dcts.eso.experience.facade.DisclosureFacade;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.dcts.eso.experience.model.response.WealthClientMasterInfo;
import com.td.eso.constants.WealthConstants;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
@Service
public class ConsentHandler {
  @Autowired
  private ApplicationManagementRestClient applicationManagementRestClient;
  private static final XLogger LOGGER = XLoggerFactory.getXLogger(DisclosureFacade.class);
  public ResponseEntity<WealthClientMasterInfo> makeCaptureConsentCall(HttpHeaders httpHeaders, MetaData metaData, WealthClientMasterInfo wealthClientMasterInfo,
                                                                       String consentURL, Boolean recordInConsentDB, boolean isTransfersConsent, String type)throws ApiException, IOException {
    LOGGER.debug("Calling makeCaptureConsentCall");
    if (type==null || type.isEmpty() ){
      type = WealthConstants.TC_CONSENT;
    }
    ResponseEntity<WealthClientMasterInfo> responseEntity = applicationManagementRestClient.
      captureConsent(httpHeaders, metaData, wealthClientMasterInfo, consentURL, recordInConsentDB, isTransfersConsent, type);
    return responseEntity;
  }
}
