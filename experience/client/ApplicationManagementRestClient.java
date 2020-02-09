package com.td.dcts.eso.experience.client;

import java.util.List;
import java.util.Map;

import com.td.dcts.eso.event.response.model.SubApplicationInfo;
import com.td.dcts.eso.experience.model.response.*;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.td.coreapi.common.config.ApiConfig;
import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.event.response.model.SearchProductContentRequest;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.model.request.ApplicationManagementRestRequest;
import com.td.dcts.eso.experience.util.CustomErrorHandler;
import com.td.dcts.eso.experience.util.ExceptionUtil;
import org.springframework.web.util.UriComponentsBuilder;


@Service
public class ApplicationManagementRestClient {

	@Autowired
	private RestTemplate restTemplate;

	static final XLogger LOGGER = XLoggerFactory.getXLogger(ApplicationManagementRestClient.class);
	/* TODO remove all CC methods and controllers */
	public ResponseEntity<ApplicationManagementRestResponse> makeSubmitAppCall(MultiValueMap<String, String> httpHeaders,
																			   ApplicationManagementRestRequest appManagementRequest) throws ApiException {

		restTemplate.setErrorHandler(new CustomErrorHandler());
		HttpEntity<?> requestEntity = new HttpEntity<ApplicationManagementRestRequest>(
				appManagementRequest, httpHeaders);
		String appMgmtSubmitUrl = ApiConfig.getInstance()
				.getPropertiesConfig(ExperienceConstants.ENV_FILE_NAME)
				.getProperty(ExperienceConstants.SUBMIT_ADJUDICATE_APPLICATION_URL);
		ResponseEntity<ApplicationManagementRestResponse> responseEntity = restTemplate.exchange(appMgmtSubmitUrl,
				HttpMethod.POST, requestEntity, ApplicationManagementRestResponse.class);
		return responseEntity;
	}

	public ResponseEntity<ApplicationManagementRestResponse> makeCheckAdjudicationCall(MultiValueMap<String, String> httpHeaders,
			ApplicationManagementRestRequest appManagementRequest) throws ApiException {

		restTemplate.setErrorHandler(new CustomErrorHandler());
		HttpEntity<?> requestEntity = new HttpEntity<ApplicationManagementRestRequest>(
				appManagementRequest, httpHeaders);
		String checkAdjudicationUrl = ApiConfig.getInstance()
				.getPropertiesConfig(ExperienceConstants.ENV_FILE_NAME)
				.getProperty(ExperienceConstants.APPLICATION_MANAGEMENT_ADJUDICATE_DECISION_URL);
		ResponseEntity<ApplicationManagementRestResponse> responseEntity = restTemplate.exchange(checkAdjudicationUrl,
				HttpMethod.POST, requestEntity, ApplicationManagementRestResponse.class);

		return responseEntity;
	}
	/*
	public ResponseEntity<ApplicationManagementRestResponse> makeAcceptDownsellCall(MultiValueMap<String, String> httpHeaders,
			ApplicationManagementRestRequest appManagementRequest) throws ApiException {

		restTemplate.setErrorHandler(new CustomErrorHandler());
		HttpEntity<?> requestEntity = new HttpEntity<ApplicationManagementRestRequest>(
				appManagementRequest, httpHeaders);
		String acceptDownsellUrl = ApiConfig.getInstance()
				.getPropertiesConfig(ExperienceConstants.ENV_FILE_NAME)
				.getProperty(ExperienceConstants.ACCEPT_DOWNSELL_URL);
		ResponseEntity<ApplicationManagementRestResponse> responseEntity = restTemplate.exchange(acceptDownsellUrl,
				HttpMethod.POST, requestEntity, ApplicationManagementRestResponse.class);

		return responseEntity;
	}
	public ResponseEntity<Object> callRetrieveCustomerProfile(MetaData metaData, MultiValueMap<String, String> httpHeaders) throws ApiException {
		ApplicationManagementRestRequest customerProfileRequest = new ApplicationManagementRestRequest();
		customerProfileRequest.setMetaData(metaData);
		restTemplate.setErrorHandler(new CustomErrorHandler());
		String customerProfileUrl = ApiConfig.getInstance()
				.getPropertiesConfig(ExperienceConstants.ENV_FILE_NAME)
				.getProperty(ExperienceConstants.RETRIEVE_CUSTOMER_PROFILE_URL);
		LOGGER.debug("RetrieveCustomerProfile URL : " + customerProfileUrl);
		HttpEntity<?> httpEntity = new HttpEntity<ApplicationManagementRestRequest>(customerProfileRequest, httpHeaders);
		ResponseEntity<Object> responseEntity = restTemplate.exchange(customerProfileUrl,HttpMethod.POST, httpEntity, Object.class);
		return responseEntity;
	}
	public ResponseEntity<Object> makeCancelCall(String creditRefNumber,ApplicationManagementRestRequest cancelCreditCardRequest, MultiValueMap<String, String> httpHeaders) throws ApiException {
		restTemplate.setErrorHandler(new CustomErrorHandler());
		String cancelCreditCardUrl = ApiConfig.getInstance()
				.getPropertiesConfig(ExperienceConstants.ENV_FILE_NAME)
				.getProperty(ExperienceConstants.CANCEL_APPLICATION_URL);
		LOGGER.debug("Cancel Application URL : " + cancelCreditCardUrl + creditRefNumber);
		HttpEntity<?> httpEntity = new HttpEntity<ApplicationManagementRestRequest>(cancelCreditCardRequest, httpHeaders);
		ResponseEntity<Object> responseEntity = restTemplate.exchange(cancelCreditCardUrl+creditRefNumber,HttpMethod.POST, httpEntity, Object.class);

		return responseEntity;
	}
	public ResponseEntity<Object> makeClientCredentialsCall() throws ApiException{
		String clientCredentialsUrl = ApiConfig.getInstance()
				.getPropertiesConfig(ExperienceConstants.ENV_FILE_NAME)
				.getProperty(ExperienceConstants.CLIENT_CREDENTIALS_URL);
		LOGGER.debug("Client Credentials URL : " + clientCredentialsUrl);
		ResponseEntity<Object> responseEntity = (ResponseEntity<Object>) restTemplate.getForEntity(
				clientCredentialsUrl, Object.class);
		return responseEntity;
	}
	*/

	public ResponseEntity<Object> makeEnrollAndReserveCall(Object requestObj, MetaData metaData,
			MultiValueMap<String, String> httpHeaders) {
		ApplicationManagementRestRequest enrollAndReserveCallRequest = new ApplicationManagementRestRequest();
		enrollAndReserveCallRequest.setMetaData(metaData);
		enrollAndReserveCallRequest.setData(requestObj);
		String appMgmtSubmitUrl = ApiConfig.getInstance()
				.getPropertiesConfig(ExperienceConstants.ENV_FILE_NAME)
				.getProperty(ExperienceConstants.ENROLL_AND_RESERVE_URL);
		LOGGER.debug("Enroll and reserve URL : " + appMgmtSubmitUrl);
		HttpEntity<?> httpEntity = new HttpEntity<>(enrollAndReserveCallRequest, httpHeaders);
		ResponseEntity<Object> responseEntity = restTemplate.exchange(appMgmtSubmitUrl,HttpMethod.POST, httpEntity, Object.class);
		return responseEntity;
	}

	public ResponseEntity<Object> makeFinalizeEWCall(Object requestObj, MetaData metaData,
			MultiValueMap<String, String> httpHeaders) {
		ApplicationManagementRestRequest finalizeEWRequest = new ApplicationManagementRestRequest();
		finalizeEWRequest.setMetaData(metaData);
		finalizeEWRequest.setData(requestObj);
		String appMgmtSubmitUrl = ApiConfig.getInstance()
				.getPropertiesConfig(ExperienceConstants.ENV_FILE_NAME)
				.getProperty(ExperienceConstants.FINALIZE_EW_URL);
		LOGGER.debug("Finalize EW URL : " + appMgmtSubmitUrl);
		HttpEntity<?> httpEntity = new HttpEntity<>(finalizeEWRequest, httpHeaders);
		ResponseEntity<Object> responseEntity = restTemplate.exchange(appMgmtSubmitUrl, HttpMethod.POST, httpEntity, Object.class);
		return responseEntity;
	}


  public ResponseEntity<WealthClientMasterInfo> captureConsent(MultiValueMap<String, String> httpHeaders, MetaData metaData, WealthClientMasterInfo wcm,
                                                               String consentURL, Boolean recordInConsentDB, boolean isTransfersConsent, String type) throws ApiException {
    String subAppID;
    Integer appID = metaData.getApplicationId();
    subAppID = null;
    List<SubApplicationInfo> subAppIDs = metaData.getSubApplicationList();
    if(subAppIDs != null) {
      if(!subAppIDs.isEmpty()) {
        subAppID = subAppIDs.get(0).getSubApplicationId();
      }
    }

    String url = ApiConfig.getInstance()
      .getPropertiesConfig(ExperienceConstants.ENV_FILE_NAME)
      .getProperty(consentURL);

    if(recordInConsentDB){
      url = url+"InDB";
    }
    url = url + "?type=" + type;

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
    if(subAppID != null && !subAppID.isEmpty()) {
      builder.queryParam("subappID", subAppID);
    }
    if(appID != null){
      builder.queryParam("appID", appID);
    }

    if(isTransfersConsent)
    {
      builder.queryParam("isTransfersConsent", isTransfersConsent);
    }
    ApplicationManagementRestRequest restRequest = new ApplicationManagementRestRequest();
    restRequest.setMetaData(metaData);
    restRequest.setData(wcm);
    HttpEntity<ApplicationManagementRestRequest> httpEntity = new HttpEntity<>(restRequest, httpHeaders);
    ResponseEntity<WealthClientMasterInfo> responseEntity = restTemplate.exchange(builder.build().encode().toUri(),HttpMethod.POST, httpEntity, WealthClientMasterInfo.class);
    LOGGER.exit(responseEntity);
    return responseEntity;
  }


  public ResponseEntity<GetDisclosureListResponse> retrieveConsents(MultiValueMap<String, String> httpHeaders,
                                                                    Map<String, Object> appManagementRequest) throws ApiException {

    restTemplate.setErrorHandler(new CustomErrorHandler());
    HttpEntity<?> requestEntity = new HttpEntity<>(
      appManagementRequest, httpHeaders);
    String appMgmtSubmitUrl = ApiConfig.getInstance()
      .getPropertiesConfig(ExperienceConstants.ENV_FILE_NAME)
      .getProperty(ExperienceConstants.RETRIEVE_CONSENT_URL);
    ResponseEntity<GetDisclosureListResponse> responseEntity = restTemplate.exchange(appMgmtSubmitUrl,
      HttpMethod.POST, requestEntity, GetDisclosureListResponse.class);

    return responseEntity;
  }

	public List<Object> retrieveProductType(SearchProductContentRequest searchProductContentRequest,
			MultiValueMap<String, String> httpHeaders) throws ApiException{
		String retrieveProductTypeUrl = ApiConfig.getInstance()
				.getPropertiesConfig(ExperienceConstants.ENV_FILE_NAME)
				.getProperty(ExperienceConstants.RETRIEVE_PRODUCT_TYPE_URL);

		HttpEntity<?> requestEntity = new HttpEntity<Object>(searchProductContentRequest, httpHeaders);

		List<Object> responseEntity = (List<Object>) restTemplate.postForObject(retrieveProductTypeUrl, requestEntity,
				Object.class);

		LOGGER.debug("Retrieve Product URL : " + retrieveProductTypeUrl);

		return responseEntity;
	}

	/**
	 *
	 * @param metaData
	 * @param httpHeaders
	 * @return JSON format string
	 * @throws ApiException
	 */
	public ResponseEntity<String> getTextResponse(MetaData metaData, MultiValueMap<String, String> httpHeaders, String url) throws ApiException {
		return getTextResponse(metaData,null,httpHeaders,url);
	}

	/**
	 *
	 * @param metaData
	 * @param data
	 * @param httpHeaders
	 * @return JSON format string
	 * @throws ApiException
	 */
	public ResponseEntity<String> getTextResponse(MetaData metaData, Object data, MultiValueMap<String, String> httpHeaders, String url) throws ApiException {
		LOGGER.entry(url);
		ApplicationManagementRestRequest restRequest = new ApplicationManagementRestRequest();
		restRequest.setMetaData(metaData);
		restRequest.setData(data);
		HttpEntity<ApplicationManagementRestRequest> httpEntity = new HttpEntity<>(restRequest, httpHeaders);
		ResponseEntity<String> responseEntity = restTemplate.exchange(url,HttpMethod.POST, httpEntity, String.class);
		LOGGER.exit(responseEntity);
		return responseEntity;
	}

	/**
	 *
	 * @param metaData
	 * @param data
	 * @param httpHeaders
	 * @return Response Body as type <T>
	 * @throws ApiException
	 */
	public <T> T getResponse(MetaData metaData, Object data, MultiValueMap<String, String> httpHeaders, String url, Class<T> responseType) throws ApiException {
		LOGGER.entry(url);
		ApplicationManagementRestRequest restRequest = new ApplicationManagementRestRequest();
		restRequest.setMetaData(metaData);
		restRequest.setData(data);
		HttpEntity<ApplicationManagementRestRequest> httpEntity = new HttpEntity<>(restRequest, httpHeaders);
		ResponseEntity<T> responseEntity = restTemplate.exchange(url,HttpMethod.POST, httpEntity, responseType);
		if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
			LOGGER.exit(responseEntity);
			return responseEntity.getBody();
		}
		else{
			LOGGER.error("App enging URL: {} returns error code {} ",url,responseEntity.getStatusCode());
			throw new ApiException(ExceptionUtil.buildErrorStatusFromApiErrorStatus(responseEntity));
		}
	}

  public <T> T retrieveResponse(MetaData metaData, Object data, MultiValueMap<String, String> httpHeaders, String url, Class<T> responseType) throws ApiException {
    LOGGER.entry(url);

    HttpEntity<Object> httpEntity = new HttpEntity<>(data, httpHeaders);
    ResponseEntity<T> responseEntity = restTemplate.exchange(url,HttpMethod.POST, httpEntity, responseType);
    if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
      LOGGER.exit(responseEntity);
      return responseEntity.getBody();
    }
    else{
      LOGGER.error("App enging URL: {} returns error code {} ",url,responseEntity.getStatusCode());
      throw new ApiException(ExceptionUtil.buildErrorStatusFromApiErrorStatus(responseEntity));
    }
  }

}
