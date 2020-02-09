package com.td.dcts.eso.experience.client;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.td.coreapi.common.config.ApiConfig;
import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.util.CustomErrorHandler;

@Service
public class ValetRestClient {

	static final XLogger LOGGER = XLoggerFactory.getXLogger(ValetRestClient.class);

	/**
	 * Makes the down stream service call and returns the response back.
	 * 
	 * @param valetId
	 *            String
	 * @return ResponseEntity
	 * @throws ApiException
	 */
	public ResponseEntity<String> getOfferData(String valetId) throws ApiException {
		
		int timeOut = Integer.parseInt(ApiConfig.getInstance().getPropertiesConfig(ExperienceConstants.ENV_FILE_NAME)
				.getProperty(ExperienceConstants.VALET_SERVICE_TIMEOUT));
		LOGGER.debug("ValetService timeOut  {}ms ", timeOut);
		
		RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory(timeOut));
		restTemplate.setErrorHandler(new CustomErrorHandler());
		
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		
		String valetServiceUrl = ApiConfig.getInstance().getPropertiesConfig(ExperienceConstants.ENV_FILE_NAME)
				.getProperty(ExperienceConstants.VALET_SERVICE_URL);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(valetServiceUrl).queryParam("valetID", valetId);
		LOGGER.debug("ValetService URL : {} ", valetServiceUrl);
		
		ResponseEntity<String> responseEntity = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity,
				String.class);
		return responseEntity;
	}
	
	private ClientHttpRequestFactory clientHttpRequestFactory(int timeOut) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(timeOut);
        factory.setConnectTimeout(timeOut);
        return factory;
    }

}
