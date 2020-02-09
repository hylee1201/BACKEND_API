package com.td.dcts.eso.experience.handler;

import com.td.coreapi.common.status.ApiException;
import com.td.cts.ae.id.proofing.IPServiceRetrieveRestResponse;
import com.td.cts.ae.id.proofing.IPServiceValidateResponse;
import com.td.dcts.eso.experience.client.ApplicationManagementRestClient;
import com.td.dcts.eso.experience.facade.DisclosureFacade;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.dcts.eso.experience.model.response.WealthClientMasterInfo;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pegarules.soap.identityvalidation.services.RetrieveIdentityVerificationQuestion;
import pegarules.soap.identityverification.services.ValidateIdentityVerificationAnswers;

import java.io.IOException;

@Service
public class IDPHandler {

  private static final XLogger LOGGER = XLoggerFactory.getXLogger(DisclosureFacade.class);

  @Value("${resturl.idp.retrievequestions}")
  String idpRetrieveQuestionsUrl;

  @Value("${resturl.idp.validatequestions}")
  String idpValidateQuestionsUrl;


  @Autowired
  private ApplicationManagementRestClient applicationManagementRestClient;


  public IPServiceRetrieveRestResponse retrieveQuestions(HttpHeaders httpHeaders, MetaData metaData, RetrieveIdentityVerificationQuestion retrieveIdentityVerificationQuestion)throws ApiException, IOException {

    LOGGER.debug("Inside retrieveQuestions");
    //TO DO
    //product id in metaData is a synthetic one and it is set to null to avoid an issue in app engine.
    metaData.setProductId(null);

    IPServiceRetrieveRestResponse ipServiceRetrieveRestResponse = applicationManagementRestClient.getResponse(metaData,retrieveIdentityVerificationQuestion,httpHeaders,idpRetrieveQuestionsUrl,IPServiceRetrieveRestResponse.class);

    return ipServiceRetrieveRestResponse;
  }

  public IPServiceValidateResponse validateQuestions(HttpHeaders httpHeaders, MetaData metaData, ValidateIdentityVerificationAnswers validateIdentityVerificationAnswers) throws ApiException {

    LOGGER.debug("Inside validateQuestions");
     //TO DO
    //product id in metaData is a synthetic one and it is set to null to avoid an issue in app engine.
    metaData.setProductId(null);

    return applicationManagementRestClient.getResponse(metaData, validateIdentityVerificationAnswers,httpHeaders,idpValidateQuestionsUrl,IPServiceValidateResponse.class);

  }
}
