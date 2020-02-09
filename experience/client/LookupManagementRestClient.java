package com.td.dcts.eso.experience.client;

import java.util.List;
import java.util.Map;

import com.td.eso.rest.response.model.CountryCodeAreaCodes;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.util.CustomErrorHandler;
import com.td.dcts.eso.experience.util.ExceptionUtil;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.eso.rest.response.model.LookupModel;

@Service
public class LookupManagementRestClient {

	@Autowired
	private RestTemplate restTemplate;

  @Value("${resturl.lookupManager.lists.retrieve.old}")
	private String lookupMangerBaseUrl;

	static final XLogger LOGGER = XLoggerFactory.getXLogger(LookupManagementRestClient.class);

	/**
	 * Makes the down stream service call and returns the response back.
	 *
	 * @param listCd
	 *            String
	 * @return List<LookupModel>
	 * @throws ApiException
	 */

	@Cacheable(value=ExperienceConstants.RESTRICTED_CONNECTID_CACHE, key="#listCd",unless="#result.size() == 0")
	public List<LookupModel> callLoadLookup(String listCd) throws ApiException {
		LOGGER.debug("Call App enging for loopup list code : {}",listCd);
		return callLookupAPI(listCd,new ParameterizedTypeReference<List<LookupModel>>(){});
	}

	@Cacheable(value=ExperienceConstants.REFERENCE_DATA_OCCUPATION_BY_INDUSTRY_CACHE, key="#root.methodName")
	public Map<String,List<LookupModel>> callLoadOccupation() throws ApiException {
		LOGGER.debug("Call App enging for all Occupation grouped by industry");
		return callLookupAPI(ExperienceConstants.RETRIEVE_OCCUPATIONS_BY_INDUSTRY,new ParameterizedTypeReference<Map<String,List<LookupModel>>>(){});
	}

	@Cacheable(value=ExperienceConstants.REFERENCE_DATA_OCCUPATION_BY_INDUSTRY_CACHE, key="#root.methodName")
	public Map<String,List<LookupModel>> callLoadProvState() throws ApiException {
		LOGGER.debug("Call App enging for all Province State grouped by country");
		return callLookupAPI(ExperienceConstants.RETRIEVE_PROV_STATE_BY_COUNTRY,new ParameterizedTypeReference<Map<String,List<LookupModel>>>(){});
	}

/*
	@Cacheable(value=ExperienceConstants.COUNTRY_CODE_AREA_CODE_MAP_CACHE, key="#root.methodName")
	public List<CountryCodeAreaCodes> getCountryCodeAreaCodeMappings() throws ApiException {
		LOGGER.debug("Call App enging to retrieve Country Code and Area Code mappings");
		return callLookupAPI(ExperienceConstants.RETRIEVE_LIST_CODE_COUNTRY_CODE_AREA_CODES_MAPPING, new ParameterizedTypeReference<List<CountryCodeAreaCodes>>(){});
	}
*/

	@SuppressWarnings({ "rawtypes", "unchecked" })
	/**
	 *
	 * @param lookupMangerUrl Sub URL after the LookupManager Base URL
	 * @param responseType response body's type
	 * @return
	 */
	private <T> T callLookupAPI(String lookupMangerUrl, ParameterizedTypeReference<T> responseType) throws ApiException{
		restTemplate.setErrorHandler(new CustomErrorHandler());
		HttpEntity httpEntity = new HttpEntity(new MetaData(),new HttpHeaders());
		LOGGER.debug("Call App enging with URL: {}{}",lookupMangerBaseUrl,lookupMangerUrl);
		ResponseEntity<T> responseEntity = restTemplate.exchange(lookupMangerBaseUrl+lookupMangerUrl,HttpMethod.GET,httpEntity,responseType);
		//The response status code must be checked here, exception should be thrown here to prevent the invalid response from being cached
		if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
			return responseEntity.getBody();
		}
		else{
			LOGGER.error("App enging URL: {}{} return error code {} ",lookupMangerBaseUrl,lookupMangerUrl,responseEntity.getStatusCode());
			throw new ApiException(ExceptionUtil.buildErrorStatusFromApiErrorStatus(responseEntity));
		}
	}
}
