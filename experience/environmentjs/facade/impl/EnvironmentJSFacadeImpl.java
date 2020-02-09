package com.td.dcts.eso.experience.environmentjs.facade.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.environmentjs.facade.EnvironmentJSFacade;
import com.td.dcts.eso.experience.environmentjs.facade.ReferenceDataHelper;
import com.td.dcts.eso.experience.environmentjs.model.EnvironmentJSOutput;
import com.td.dcts.eso.experience.environmentjs.model.JavaScriptEnumValues;
import com.td.dcts.eso.experience.model.response.MetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;


@Service
public class EnvironmentJSFacadeImpl implements EnvironmentJSFacade {

	@Value("${id.upload.url:https://upload.dev.td.com/eo/investing/v1/idCapture}")
	private String uploadIdUrl;

  @Value("${wcmContentLocation:WCM}")
  private String wcmContentLocation;

  @Value("${credentialsUsernamePattern:^[a-gwyzA-GWYZ]0[aA]([0-1][a-zA-Z0-9][0-9]|2[a-gA-G][0-9]|2[hH][0-3])0[12]$|^[h-np-suvH-NP-SUV]0[aA]0[aA][0-2][a-zA-Z0-9][0-9]$|^[tT][0-8][a-zA-Z0-9][0-9][a-zA-Z0-9][0-9][a-zA-Z0-9][0-9]$}")
  private String credentialsUsernamePattern;

  @Value("${sendAppIDToTagging:true}")
  private String sendAppIdToTagging;

	private ReferenceDataHelper referenceDataHelper;


  @Autowired
	public EnvironmentJSFacadeImpl(ReferenceDataHelper referenceDataFacade) {
		this.referenceDataHelper = referenceDataFacade;
	}

	@Override
	public EnvironmentJSOutput prepareEnvironmentJS(MetaData metaData, HttpHeaders headers, String locale) throws ApiException, JsonProcessingException {
		EnvironmentJSOutput environmentJSOutput = new EnvironmentJSOutput();

		JavaScriptEnumValues javaScriptEnumValues = new JavaScriptEnumValues();
		environmentJSOutput.setJavaScriptEnumValues(javaScriptEnumValues);

		environmentJSOutput.setReferenceDataHandlerOutput(referenceDataHelper.getReferenceData(metaData, headers));
		environmentJSOutput.setUploadIdUrl(uploadIdUrl);

		//ToDo: Remove the next two lines in Sprint 47 as NexusCard need to be handled seperately
    environmentJSOutput.getReferenceData().remove(environmentJSOutput.getReferenceData().get("identificationTypesAssisted"));
    environmentJSOutput.getReferenceData().put("identificationTypesAssisted", environmentJSOutput.getReferenceData().get("identificationTypes"));
    environmentJSOutput.getReferenceData().get("identificationTypesAssisted").get(6).setDescription("Nexus Card");
    environmentJSOutput.getReferenceData().get("identificationTypesAssisted").get(6).setDescriptionFr("Carte Nexus");
    environmentJSOutput.getReferenceData().get("identificationTypesAssisted").get(1).setDescription("Passport");
    environmentJSOutput.getReferenceData().get("identificationTypesAssisted").get(1).setDescriptionFr("Passeport");

    environmentJSOutput.setBootstrapDone(true);
    environmentJSOutput.setLanguage(locale);
    environmentJSOutput.setWcmContentLocation(wcmContentLocation);
    environmentJSOutput.setCredentialsUsernamePattern(credentialsUsernamePattern);
    environmentJSOutput.setCanadaPhoneCode("1-CA");

    if (Boolean.parseBoolean(sendAppIdToTagging)) {
      environmentJSOutput.setApplicationId(metaData.getApplicationId().toString());
    } else {
      environmentJSOutput.setApplicationId("");
    }
		return environmentJSOutput;
	}
}
