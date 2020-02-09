package com.td.dcts.eso.experience.service;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

//import com.td.dcts.eso.experience.client.LookupManagementRestClient;
import com.td.eso.constants.LookupConstants;

public class PreLoadService implements ApplicationListener<ContextRefreshedEvent> {

/*
	@Autowired
	LookupManagementRestClient lookupclient;
*/

	static final XLogger LOGGER = XLoggerFactory.getXLogger(PreLoadService.class);

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		LOGGER.info("PreLoadService triggered by event {} ", event);
		loadAllLookupData();
		LOGGER.info("PreLoadService completed by event {} ", event);
	}

	public void loadAllLookupData() {
		try {
/*
			lookupclient.callLoadLookup(LookupConstants.LOOKUP_LIST_CODE_COUNTRY_RD);
			lookupclient.callLoadLookup(LookupConstants.LOOKUP_LIST_CODE_EMPLOYMENT_STATUS_TYPE_RD);
			lookupclient.callLoadLookup(LookupConstants.LOOKUP_LIST_CODE_GENDER_TYPE_RD);
			lookupclient.callLoadLookup(LookupConstants.LOOKUP_LIST_CODE_INDUSTRY_RD);
			lookupclient.callLoadLookup(LookupConstants.LOOKUP_LIST_CODE_LANGUAGE_CODE_RD);
			lookupclient.callLoadLookup(LookupConstants.LOOKUP_LIST_CODE_MARITAL_STATUS_RD);
			lookupclient.callLoadLookup(LookupConstants.LOOKUP_LIST_CODE_PARTY_ADDRESS_RELATION_TYPE_PHONE_ONLY_RD);
			lookupclient.callLoadLookup(LookupConstants.LOOKUP_LIST_CODE_SUFFIX_TYPE_RD);
			lookupclient.callLoadLookup(LookupConstants.LOOKUP_LIST_CODE_UNIT_COMPLEX_TYPE_RD);
			lookupclient.callLoadOccupation();
			lookupclient.callLoadProvState();
*/
			LOGGER.debug("loadAllLookupData completed");
		} catch (Exception e) {
			LOGGER.warn("loadAllLookupData with error:{}",e.getMessage());
		}
	}

}
