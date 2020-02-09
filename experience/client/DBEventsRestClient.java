package com.td.dcts.eso.experience.client;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.td.coreapi.common.config.ApiConfig;
import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.util.CustomErrorHandler;
import com.td.dcts.eso.experience.model.response.MetaData;

@Service
public class DBEventsRestClient {

	@Autowired
	private RestTemplate restTemplate;

	static final XLogger LOGGER = XLoggerFactory.getXLogger(DBEventsRestClient.class);

	/**
	 * Makes the down stream service call and returns the response back.
	 * @param subAppID TODO
	 * @param MultiValueMap
	 *            <String, String> HttpHeaders
	 * @param appManagementRequest
	 *            ApplicationManagementRestRequest
	 * @param url
	 *            String
	 *
	 * @return ResponseEntity
	 * @throws ApiException
	 */

	public ResponseEntity<Object> getEvents(Integer appId, String subAppID, MetaData metaData, MultiValueMap<String, String> httpHeaders) throws ApiException {
		restTemplate.setErrorHandler(new CustomErrorHandler());

		String url = ApiConfig.getInstance()
						.getPropertiesConfig(ExperienceConstants.ENV_FILE_NAME)
						.getProperty(ExperienceConstants.RETRIEVE_DB_EVENTS_URL);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url).queryParam("appID", appId);
		if(subAppID != null && !subAppID.isEmpty()) {
			builder.queryParam("subAppID", subAppID);
		}
		HttpEntity httpEntity = new HttpEntity(metaData, httpHeaders);
		ResponseEntity<Object> responseEntity = restTemplate.exchange( builder.build().encode().toUri(), HttpMethod.GET, httpEntity, Object.class);

		return responseEntity;
	}
}
