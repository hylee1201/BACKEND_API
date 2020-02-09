package com.td.dcts.eso.experience.facade;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.dao.CustomerDAO;
import com.td.dcts.eso.experience.handler.SubmitAppHandler;
import com.td.dcts.eso.experience.helper.SubmitAppHelper;
import com.td.dcts.eso.experience.model.docgen.PdfContainer;
import com.td.dcts.eso.experience.model.response.*;
import com.td.dcts.eso.experience.util.CommonUtil;
import com.td.dcts.eso.experience.util.ExceptionUtil;
import com.td.dcts.eso.experience.util.SessionUtil;
import com.td.eso.util.FeatureToggle;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.td.dcts.eso.experience.constants.ExperienceConstants.CHANNEL_SELF_SERVE_WEB;
import static com.td.dcts.eso.experience.constants.ExperienceConstants.N2B;
import static com.td.dcts.eso.experience.util.CommonUtil.updateInfoFromESignPackage;

@Component
public class SubmitAppFacade {

  @Autowired
  SubmitAppHandler submitAppHandler;

  @Autowired
  SubmitAppHelper submitAppHelper;

  @Autowired
  private SessionUtil sessionUtil;

  @Autowired
  private CustomerDAO customerDAO;

  @Autowired
  private ConsentFacade consentFacade;


  private static final XLogger LOGGER = XLoggerFactory.getXLogger(SubmitAppFacade.class);

  DateFormat pdfFileDateFormat = new SimpleDateFormat("MMddyyyy");


  public WealthClientMasterInfo submit(HttpHeaders httpHeaders, MetaData metaData, HttpServletRequest httpServletRequest) throws ApiException, IOException {

    String wealthJSONData = sessionUtil.getDraftClientProfile(httpServletRequest);
    WealthClientMasterInfo wealthCMInfo = null;
    wealthCMInfo = customerDAO.getWealthClientMasterInfo(wealthJSONData);
    wealthCMInfo = CommonUtil.resetWCM(wealthCMInfo, "0");
    WealthClientMasterInfo wcmSavedForeSignPackage =   (WealthClientMasterInfo) sessionUtil.getFromSession(httpServletRequest, ExperienceConstants.SESSION_KEY_ESIGN_PACKAGE);
    updateInfoFromESignPackage(wcmSavedForeSignPackage, wealthCMInfo);
    sessionUtil.setDraftClientProfile(httpServletRequest, wealthCMInfo);


    //set client type in WCM before submit
    String clientType = CommonUtil.getCMClientType(wealthCMInfo.getAboutyou().getInvestmentInfo());
    String userType = wealthCMInfo.getUserIdentity().getUserType();

    wealthCMInfo.getUserIdentity().setClientTyp(clientType);
    ConsentInfo tcConsentInfo;
    if(!N2B.equals(userType)) {
      String tcConsentInfoStr = (String) sessionUtil.getFromSession(httpServletRequest, SessionUtil.TC_CONSENT_CAPTURED);
      //A Consent capture call should be initiated in Parallel and should have populated the tcConsent Info object
      //Consent capture did not happen.  Consents for T&C must happen Captured before proceeding with Submission
      if (FeatureToggle.CONSENT_CAPTURE_ENABLE()) {
        if (CHANNEL_SELF_SERVE_WEB.equals(wealthCMInfo.getUserIdentity().getChannel())) {
          if (tcConsentInfoStr != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            tcConsentInfo = (ConsentInfo) objectMapper.readValue(tcConsentInfoStr, ConsentInfo.class);
            if (tcConsentInfo.getDisclosureDocumentInfoList() != null && tcConsentInfo.getDisclosureDocumentInfoList().size() > 0) {
              wealthCMInfo.getAboutyou().setTcConsentInfo(tcConsentInfo);
            } else {
              ResponseEntity<WealthClientMasterInfo> responseEntity = consentFacade.captureConsentRequest(httpServletRequest, metaData, httpHeaders, true, null);
              wealthCMInfo.getAboutyou().setTcConsentInfo(responseEntity.getBody().getAboutyou().getTcConsentInfo());
            }
          } else {
            ResponseEntity<WealthClientMasterInfo> responseEntity = consentFacade.captureConsentRequest(httpServletRequest, metaData, httpHeaders, true, null);
            wealthCMInfo.getAboutyou().setTcConsentInfo(responseEntity.getBody().getAboutyou().getTcConsentInfo());
          }
        }
      }
    }
    //validate data before submit if validation is enabled
    boolean isDataValid = true;
    if (FeatureToggle.BACKEND_VALIDATION_ENABLE()) {
      isDataValid = submitAppHelper.validateWealthModel(wealthCMInfo);
    }
    if(isDataValid) {
      WealthClientMasterInfo wealthClientMasterInfo = submitAppHandler.submit(metaData, httpHeaders, wealthCMInfo);
      //WealthClientMasterInfo wealthClientMasterInfo = responseEntity.getBody();

      //ToDo Refactor to get the Actual Object
      sessionUtil.setDraftClientProfile(httpServletRequest, wealthClientMasterInfo);

      return wealthClientMasterInfo;
    } else {  //backend validation failed in experience, throw exception
      LOGGER.error("SubmitAppFacade: Backend validation failed inside experience");
      throw new ApiException(ExceptionUtil.buildServerErrorStatus(), "SubmitAppFacade: Backend validation failed inside experience");
    }


  }

  public ResponseEntity<PdfContainer> retrievePDF(String wealthClientMasterInfoJson, MetaData metaData, HttpHeaders httpHeaders) throws ApiException {
    WealthClientMasterInfo wealthClientMasterInfo = submitAppHelper.getWealthClientMasterInfo(wealthClientMasterInfoJson);

    ResponseEntity<byte[]> responseEntity = submitAppHandler.retrievePDF(wealthClientMasterInfo.getAboutyou().getFileNetDocumentList(), metaData, httpHeaders);

    String downloadFileName = generatePDFFileName(wealthClientMasterInfo);

    PdfContainer pdfContainer = new PdfContainer();
    pdfContainer.setFileName(downloadFileName);
    pdfContainer.setFile(responseEntity.getBody());
    return ResponseEntity.ok().body(pdfContainer);
  }

  private String generatePDFFileName(WealthClientMasterInfo wealthClientMasterInfo){
    Date fileNetDocumentDate = wealthClientMasterInfo.getAboutyou().getFileNetDocumentList().get(0).getDocumentDate();

    final String fileNetDocumentDateStr = pdfFileDateFormat.format(fileNetDocumentDate);

    final PersonalInfo personalInfo = wealthClientMasterInfo.getAboutyou().getPersonalInfo();

    String fileName = personalInfo.getLastName().trim() + "_" + personalInfo.getFirstName().trim().substring(0,1) + "_" + fileNetDocumentDateStr + ".pdf" ;

    return fileName;
  }



}
