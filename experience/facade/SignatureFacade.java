package com.td.dcts.eso.experience.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.events.createevent.request.EsoCreateEventRequest;
import com.td.dcts.eso.events.createevent.request.Event;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.handler.SignatureHandler;
import com.td.dcts.eso.experience.helper.EventHelper;
import com.td.dcts.eso.experience.helper.SubmitAppHelper;
import com.td.dcts.eso.experience.model.esignlive.ESignaturePackage;
import com.td.dcts.eso.experience.model.response.AccountDetails;
import com.td.dcts.eso.experience.model.response.AccountNumber;
import com.td.dcts.eso.experience.model.response.FileNetDocument;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.dcts.eso.experience.model.response.WealthClientMasterInfo;
import com.td.dcts.eso.experience.util.ExceptionUtil;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Component
public class SignatureFacade {

  @Autowired
  SignatureHandler signatureHandler;

  @Autowired
  SubmitAppHelper submitAppHelper;


  @Autowired
  EventHelper eventHelper;

  private static final XLogger LOGGER = XLoggerFactory.getXLogger(SignatureFacade.class);



    public ResponseEntity<WealthClientMasterInfo> retrieveSignUrl(WealthClientMasterInfo wcmInfo, MetaData metaData, HttpHeaders httpHeaders) throws ApiException {

    ResponseEntity<WealthClientMasterInfo> response = signatureHandler.launcheSign(wcmInfo, metaData, httpHeaders);
    return response;
  }



  public void logCaptureSignEvent(HttpHeaders httpHeaders, MetaData metaData) throws JsonProcessingException, ApiException {

      EsoCreateEventRequest esoCreateEventRequest = new EsoCreateEventRequest();

      Event event = new Event();
      event.setApplicationId(String.valueOf(metaData.getApplicationId()));
      event.setSubApplicationId(String.valueOf(metaData.getSubApplicationList().get(0).getSubApplicationId()));
      event.setProductId(metaData.getProductId());
      event.setEventTypeCD(ExperienceConstants.EVENT_TYPE_SIGN_CAPTURE);
      event.setEventStatus(ExperienceConstants.EVENT_STATUS_SUCCESS);
      event.setBusinessOutcomeCD(ExperienceConstants.DEFAULT_BUSINESS_OUTCOME);
      event.setEventMetaDataJSON("");
      esoCreateEventRequest.setEvent(event);

      eventHelper.logEvent(esoCreateEventRequest, httpHeaders, metaData);
  }



  public List<FileNetDocument> sendPackageToFilenet(String wealthClientMasterInfoJson, String packageId, MetaData metaData, HttpHeaders httpHeaders) throws ApiException {
    WealthClientMasterInfo wcmInfo = submitAppHelper.getWealthClientMasterInfo(wealthClientMasterInfoJson);

    // TODO remove this hardcoded account - this was added just to facilitate testing
    AccountDetails accountDetails = wcmInfo.getAboutyou().getAccountDetails();
    if (accountDetails.getAccounts() == null || accountDetails.getAccounts().size() == 0) {
      accountDetails.setAccounts(new ArrayList<>());
      AccountNumber cadAcct = new AccountNumber();
      AccountNumber usdAcct = new AccountNumber();
      cadAcct.setAccountNumber("04BLY3A");
      usdAcct.setAccountNumber("04BLY3B");
      cadAcct.setAccountType("CASH");
      usdAcct.setAccountType("CASH");
      cadAcct.setCurrency("CAD");
      usdAcct.setCurrency("USD");
      cadAcct.setNewClientIdReservedInd(false);
      usdAcct.setNewClientIdReservedInd(false);
      accountDetails.getAccounts().add(cadAcct);
      accountDetails.getAccounts().add(usdAcct);
    }

    wcmInfo.getApplicationInfo().setSignedDocumentPackageId(packageId);

    return signatureHandler.sendPackageToFilenet(wcmInfo, metaData, httpHeaders);
  }

  public Response retrieveCoApplicantSignUrl(String profileId, WealthClientMasterInfo wcm, MetaData metaData, HttpHeaders httpHeaders)
      throws ApiException{
    ResponseEntity<ESignaturePackage> response = signatureHandler.launcheCoApplicantSign(profileId,wcm, metaData, httpHeaders);

    if (HttpStatus.OK.equals(response.getStatusCode())) {
      ESignaturePackage eSignLivePackage = response.getBody();
      return Response.ok(eSignLivePackage, MediaType.APPLICATION_JSON).build();
    } else {
      throw new ApiException(ExceptionUtil.buildErrorStatusFromApiErrorStatus(response));
    }
  }
}
