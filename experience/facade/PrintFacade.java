package com.td.dcts.eso.experience.facade;

import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.ApplicationDataController;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.handler.PrintHandler;
import com.td.dcts.eso.experience.model.print.EsoDocumentOrchestrationResponse;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.dcts.eso.experience.model.response.WealthClientMasterInfo;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

import static com.td.coreapi.common.config.ApiConfig.getInstance;

@Component
public class PrintFacade {

  private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApplicationDataController.class);

  @Autowired
  private PrintHandler printHandler;

  private static final String welcomePackageEn = getInstance()
    .getPropertiesConfig(ExperienceConstants.ENV_FILE_NAME)
    .getProperty(ExperienceConstants.WELCOME_PACKAGE_EN);

  private static final String welcomePackageFr = getInstance()
    .getPropertiesConfig(ExperienceConstants.ENV_FILE_NAME)
    .getProperty(ExperienceConstants.WELCOME_PACKAGE_FR);

  public byte[] printWelcomePackage(MetaData metaData, HttpHeaders httpHeaders, String locale) throws ApiException, IOException {
    String filePath ;
    if(ExperienceConstants.FR_CA.toString().toLowerCase().equals(locale.toString().toLowerCase())){
      filePath = welcomePackageFr;
    }
    else
    {
      filePath = welcomePackageEn;
    }
    Path pdfPath = Paths.get(filePath);
    byte[] pdf = Files.readAllBytes(pdfPath);
    return pdf;
  }

  public ResponseEntity<EsoDocumentOrchestrationResponse> printPackage(WealthClientMasterInfo wcm, MetaData metaData, HttpHeaders httpHeaders) throws ApiException {
    try {
      return printHandler.printPackage(wcm, metaData, httpHeaders);
    } catch (Exception e) {
      LOGGER.error("PrintFacade: Error occured while calling print package", e);
      throw e;
    }

  }
}
