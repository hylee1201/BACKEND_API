package com.td.dcts.eso.experience.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.handler.DisclosureHandler;
import com.td.dcts.eso.experience.helper.DisclosureHelper;
import com.td.dcts.eso.experience.model.request.disclosure.GetDisclosureListRequest;
import com.td.dcts.eso.experience.model.response.GetDisclosureListResponse;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.dcts.eso.experience.model.response.WealthClientMasterInfo;
import com.td.eso.constants.WealthConstants;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;


@Component
public class DisclosureFacade {

  private static final XLogger LOGGER = XLoggerFactory.getXLogger(DisclosureFacade.class);

  @Autowired
  private DisclosureHandler disclosureHandler;

  @Autowired
  private DisclosureHelper disclosureHelper;

  public GetDisclosureListResponse getDisclosures(HttpHeaders httpHeaders,GetDisclosureListRequest getDisclosureListRequest) throws ApiException {
    LOGGER.debug("Inside getDisclosures method.");
    return disclosureHandler.getDisclosures(httpHeaders,getDisclosureListRequest);
  }

  public void populateDisclosureDetails(MetaData metaData, HttpHeaders httpHeaders,WealthClientMasterInfo wcm, String locale) throws ApiException,JsonProcessingException {
    LOGGER.debug("Inside populateDisclosureDetails method.");

    disclosureHelper.populateDisclosureDetails(metaData, httpHeaders, wcm, locale);

    //update the event
    disclosureHelper.updateEvent(metaData,wcm,httpHeaders, WealthConstants.ABOUT_YOU_PERSONAL_INFO);

  }

  public void populateLoginConsents(WealthClientMasterInfo wealthClientMasterInfo) throws ApiException {

    LOGGER.debug("Inside populateLoginConsents method.");

    disclosureHelper.loadN2bLoginConsents(wealthClientMasterInfo);

  }


}
