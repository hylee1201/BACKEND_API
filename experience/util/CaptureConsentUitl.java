package com.td.dcts.eso.experience.util;

import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.model.request.ApplicationManagementRestRequest;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.dcts.eso.experience.model.response.WealthClientMasterInfo;
import com.td.dcts.eso.session.model.EsoJsonData;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class CaptureConsentUitl {

  static final XLogger logger = XLoggerFactory.getXLogger(CaptureConsentUitl.class);

  public ApplicationManagementRestRequest createCaptureConsentRequest(Map<String, Object> consentRequest, MetaData metaData, WealthClientMasterInfo wealthClientMasterInfo) throws ApiException {

    logger.entry();

    ApplicationManagementRestRequest applicationManagementRestRequest = new ApplicationManagementRestRequest();

    /**TO DO to change Map<String,Object> to POJO*/
    Map<String, Object> dataMap = new HashMap<String, Object>();
    dataMap.put("personalInfoFirstName",wealthClientMasterInfo.getAboutyou().getPersonalInfo().getFirstName());
    dataMap.put("personalInfoLastName",wealthClientMasterInfo.getAboutyou().getPersonalInfo().getLastName());
    dataMap.put("partyID",wealthClientMasterInfo.getReference().getPartyId());

    consentRequest.put("documentCreateDT",new SimpleDateFormat(ExperienceConstants.DATE_FORMAT).format(new Date()));
    applicationManagementRestRequest.setData(dataMap);
    applicationManagementRestRequest.setConsentData(consentRequest);
    applicationManagementRestRequest.setMetaData(metaData);

    logger.exit();

    return applicationManagementRestRequest;
  }
}
