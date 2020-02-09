package com.td.dcts.eso.experience.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.td.coreapi.common.status.ApiException;
import com.td.cts.ae.id.proofing.IPServiceRetrieveRestResponse;
import com.td.cts.ae.id.proofing.IPServiceValidateResponse;
import com.td.cts.ae.id.proofing.model.AnswerChoiceBo;
import com.td.cts.ae.id.proofing.model.QuestionBo;
import com.td.dcts.eso.experience.handler.IDPHandler;
import com.td.dcts.eso.experience.helper.IDPHelper;
import com.td.dcts.eso.experience.model.request.idp.IPAnswers;
import com.td.dcts.eso.experience.model.request.idp.IpAnswer;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.dcts.eso.experience.model.response.WealthClientMasterInfo;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import pegarules.soap.identityvalidation.services.RetrieveIdentityVerificationQuestion;
import pegarules.soap.identityverification.services.ValidateIdentityVerificationAnswers;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class IDPFacade {

  private static final XLogger LOGGER = XLoggerFactory.getXLogger(IDPFacade.class);

  @Autowired
  private IDPHelper idpHelper;

  @Autowired
  private IDPHandler idpHandler;

  public IPServiceRetrieveRestResponse retrieveQuestions(HttpHeaders httpHeaders, MetaData metaData, WealthClientMasterInfo wcm) throws IOException, ApiException {

    RetrieveIdentityVerificationQuestion retrieveIdentityVerificationQuestion = idpHelper.createIdVerificationQuestionRequest(metaData.getSessionId(),wcm);

    try {

      return idpHandler.retrieveQuestions(httpHeaders,metaData,retrieveIdentityVerificationQuestion);

    } catch (Exception e) {
      LOGGER.error("Error occured while retrieving Questions",e);
      throw e;
    }

  }

  public boolean validateQuestions(HttpHeaders httpHeaders, MetaData metaData, IPAnswers ipAnswers,IPServiceRetrieveRestResponse ipServiceRetrieveRestResponse,Date questionSentTime) throws ApiException, JsonProcessingException {

    //validate 2 minute time
    Date now = new Date();

    // take the average time by dividing it by # of questions and conver it to seconds
    long secondsSince = ((now.getTime() - questionSentTime.getTime()) ) / (ipAnswers.getIpAnswers().size() * 1000);


    //first validate the questions
    if (!validateQuestions(ipAnswers,ipServiceRetrieveRestResponse)) {
      return false;
    }

    ValidateIdentityVerificationAnswers validateIdentityVerificationAnswers = idpHelper.createIdpAnswerRequest(ipAnswers,ipServiceRetrieveRestResponse.getCorrelationId(),secondsSince);

    try {

      IPServiceValidateResponse ipServiceValidateResponse = idpHandler.validateQuestions(httpHeaders,metaData,validateIdentityVerificationAnswers);

      return idpHelper.validateResponse(ipServiceValidateResponse);
    }
    catch (Exception e) {
      LOGGER.error("Error occured while validating questions",e);
      throw e;
    }

  }
  // validate user input only
  private boolean validateQuestions(IPAnswers ipAnswers, IPServiceRetrieveRestResponse ipServiceRetrieveRestResponse) {

    String questionNo;
    String selectdAnswer;

    for(IpAnswer anAnswer : ipAnswers.getIpAnswers()) {

      questionNo = anAnswer.getQuestionNO();
      selectdAnswer = anAnswer.getAnswerTXT();

      boolean validQuestion = false;

      for(QuestionBo questionBo : ipServiceRetrieveRestResponse.getQuestions()) {
        if (questionBo.getQuestionNumber().equals(questionNo) && checkAnswer(questionBo.getAnswerChoices(),selectdAnswer)) {
          validQuestion = true;
        }
      }

      if (!validQuestion) return false;

    }

    return true;
  }


  private boolean checkAnswer(List<AnswerChoiceBo> answers, String selectdAnswer) {

    for(AnswerChoiceBo answerChoiceBo : answers) {

      if (answerChoiceBo.getAnswerChoiceText().equals(selectdAnswer)) {
        return true;
      }
    }

    return false;

  }

}
