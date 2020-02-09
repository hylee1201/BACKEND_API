package com.td.dcts.eso.experience.handler;

import java.util.List;
import java.util.Map;

import com.td.dcts.eso.event.response.model.SubApplicationInfo;
import com.td.dcts.eso.experience.model.request.ApplicationManagementRestRequest;
import com.td.dcts.eso.experience.model.response.AccountDetails;
import com.td.dcts.eso.experience.model.response.AccountNumber;
import com.td.dcts.eso.experience.model.response.WealthClientMasterInfo;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.td.coreapi.common.config.ApiConfig;
import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.util.CustomErrorHandler;
import com.td.dcts.eso.experience.util.ExceptionUtil;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.eso.rest.response.model.LookupModel;

@Service
public class AccountDetailsHandler{

	@Autowired
	private RestTemplate restTemplate;

  @Value("${resturl.accountDetails.reserve}")
	String accountDetailsBaseUrl;

	static final XLogger LOGGER = XLoggerFactory.getXLogger(AccountDetailsHandler.class);

	/**
	 * Makes the down stream service call and returns the response back.
	 * @param <T>
	 *
	 * @param listCd
	 *            String
	 * @return List<LookupModel>
	 * @throws ApiException
	 */


	public ResponseEntity<AccountNumber[]> getAccountDetails(HttpHeaders httpHeaders, WealthClientMasterInfo wcm, MetaData metaData, String cmClientTyp) throws ApiException {

    String subAppID;
    String appID = metaData.getApplicationId().toString();
    subAppID = "";
    List<SubApplicationInfo> subAppIDs = metaData.getSubApplicationList();
    if(subAppIDs != null) {
      if(!subAppIDs.isEmpty()) {
        subAppID = subAppIDs.get(0).getSubApplicationId();
      }
    }



		restTemplate.setErrorHandler(new CustomErrorHandler());
		String acReservationURL;
		acReservationURL = String.format("%s/%s/%s?appID=%s&subAppID=%s",accountDetailsBaseUrl, wcm.getCmClientID(), cmClientTyp, appID, subAppID);;

    ApplicationManagementRestRequest restRequest = new ApplicationManagementRestRequest();
    restRequest.setMetaData(metaData);
    restRequest.setData(wcm);

		@SuppressWarnings({ "rawtypes", "unchecked" })
		HttpEntity<ApplicationManagementRestRequest> httpEntity = new HttpEntity<>(restRequest,httpHeaders);

		ResponseEntity<AccountNumber[]> responseEntity = restTemplate.exchange( acReservationURL, HttpMethod.POST, httpEntity, AccountNumber[].class);

		return responseEntity;
	}

}
