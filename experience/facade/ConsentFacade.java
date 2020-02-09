package com.td.dcts.eso.experience.facade;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.ConsentController;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.handler.ConsentHandler;
import com.td.dcts.eso.experience.model.response.ConsentInfo;
import com.td.dcts.eso.experience.model.response.DisclosureDocumentInfo;
import com.td.dcts.eso.experience.model.response.FundsTransfer;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.dcts.eso.experience.model.response.WealthClientMasterInfo;
import com.td.dcts.eso.experience.util.SessionUtil;
import com.td.dcts.eso.session.model.EsoJsonData;
import com.td.eso.constants.WealthConstants;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Service
public class ConsentFacade {

  @Autowired
  private SessionUtil sessionUtil;
  @Autowired
  private ConsentHandler consentHandler;
  @Autowired
  private TransfersFacade transfersFacade;

  private ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  private static final XLogger LOGGER = XLoggerFactory.getXLogger(ConsentController.class);



  public DisclosureDocumentInfo retrieveDisclosureByDocument(HttpServletRequest httpServletRequest,
                                                             String documentId
                                                                      )throws ApiException, IOException {
    WealthClientMasterInfo wealthClientMasterInfo = objectMapper.readValue(sessionUtil.getDraftClientProfile(httpServletRequest), WealthClientMasterInfo.class);

    DisclosureDocumentInfo disclosureDocumentInfo ;

    //Popup links for disclosures are only available in T&C consents for now.
    disclosureDocumentInfo = findDisclosureDocument(wealthClientMasterInfo.getAboutyou().getTcConsentInfo(), documentId);

    //If other documents have disclosure popup link then we need to use the code below to populate other discosures
    //    if(disclosureDocumentInfo==null){
    //      disclosureDocumentInfo = findDisclosureDocument(wealthClientMasterInfo.getAboutyou().getMarginConsentInfo(), documentId);
    //    }

    if(disclosureDocumentInfo==null)
    {
      //If we are unable to find disclosure then we need to send blank disclosure object.
      disclosureDocumentInfo = new DisclosureDocumentInfo();
    }
    return disclosureDocumentInfo;
  }

  private DisclosureDocumentInfo  findDisclosureDocument(ConsentInfo consentInfo, String documentType) {
    for (DisclosureDocumentInfo disclosureDocumentInfo:consentInfo.getDisclosureDocumentInfoList()) {
      if(documentType.equals(disclosureDocumentInfo.getProductId())){
        return disclosureDocumentInfo;
      }
    }
    return null;
  }

    public ResponseEntity<WealthClientMasterInfo> captureConsentRequest(HttpServletRequest httpServletRequest, MetaData metaData, HttpHeaders httpHeaders,
                                                                      Boolean recordInConsentDB, FundsTransfer fundsTransfer)throws ApiException, IOException {

    LOGGER.entry("captureConsents started");
    EsoJsonData esoJsonData = sessionUtil.getSessionData(httpServletRequest);
    String clientIPAddress = httpServletRequest.getRemoteAddr();
    String locale = (String) esoJsonData.get(SessionUtil.SESSION_KEY_LOCALE);
    String sessionId = (String) esoJsonData.get(SessionUtil.SESSION_KEY_SESSION_ID);
    httpHeaders.add(SessionUtil.SESSION_KEY_SESSION_ID, sessionId);
    httpHeaders.add(ExperienceConstants.HTTP_HEADER_CLIENT_IP, clientIPAddress);
    httpHeaders.add(ExperienceConstants.HTTP_HEADER_LOCALE, locale);
    boolean isTransfersConsent = false;
    WealthClientMasterInfo wealthClientMasterInfo = objectMapper.readValue(sessionUtil.getDraftClientProfile(httpServletRequest), WealthClientMasterInfo.class);
    LOGGER.debug("Calling createCaptureConsentRequest");

    if (fundsTransfer != null && fundsTransfer.getFundsTransferItems().size() > 0) {
      transfersFacade.separateTransfersInBothCurrencies(fundsTransfer);
      wealthClientMasterInfo.setFundsTransfers(fundsTransfer);
      isTransfersConsent = true;
    }
    ResponseEntity<WealthClientMasterInfo> captureConsentResponse  = consentHandler.makeCaptureConsentCall(httpHeaders, metaData, wealthClientMasterInfo, ExperienceConstants.CAPTURE_CONSENT_URL, recordInConsentDB, isTransfersConsent, WealthConstants.TC_CONSENT);

    return captureConsentResponse;
  }

}
