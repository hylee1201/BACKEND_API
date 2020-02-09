package com.td.dcts.eso.experience.facade;

import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.ApplicationDataController;
import com.td.dcts.eso.experience.handler.ReferringAgentHandler;
import com.td.dcts.eso.experience.model.associatesapi.AssociateStatus;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.dcts.eso.experience.util.ExceptionUtil;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ReferringAgentFacade {

  private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApplicationDataController.class);

  @Autowired
  ReferringAgentHandler referringAgentHandler;

  public ResponseEntity<AssociateStatus> retrieveStatus(  MetaData metaData, HttpHeaders httpHeaders, String idTypeCd, String acf2id) throws ApiException {
    LOGGER.debug("ReferringAgentFacade : retrieveStatus started");
    try {
      return referringAgentHandler.retrieveAssociateStatus(metaData,httpHeaders, idTypeCd, acf2id);
    } catch (Exception e) {
      LOGGER.error("ReferringAgentFacade: validateAgent: Exception Occurred", e);
      throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.INTERNAL_SERVER_ERROR.value(),
        HttpStatus.INTERNAL_SERVER_ERROR.toString()), "ReferringAgentFacade: request failed." + e.getMessage());
    }

  }
}
