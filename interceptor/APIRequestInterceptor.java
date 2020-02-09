package com.td.dcts.eso.interceptor;

import com.td.coreapi.common.status.ApiConfigException;
import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.util.ClientCredentialsUtil;
import com.td.dcts.eso.session.model.EsoOAuthData;
import com.td.dcts.eso.session.service.IESOSessionService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import static com.td.dcts.eso.experience.constants.ExperienceConstants.*;
import static com.td.dcts.eso.experience.util.CommonUtil.addBearerToAuthToken;

@Component
public class APIRequestInterceptor implements ClientHttpRequestInterceptor {

	static final XLogger logger = XLoggerFactory.getXLogger(APIRequestInterceptor.class);
	public static final String X_SECONDARY_AUTH = "X-SecondaryAuth";

	@Autowired
	private IESOSessionService iESOSessionService;

	public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
		try {
			addAuthHeaderToOutgoingRequest(httpRequest);
		} catch (ApiConfigException e) {
			// This exception is swallowed because if token validation is
			// enabled, API will return exception which is already handled in
			// experience
			logger.error("Unable to add headers to API outgoing request : " + e);
		} catch (ApiException e) {
			// This exception is swallowed because if token validation is
			// enabled, API will return exception which is already handled in
			// experience
			logger.error("Unable to add headers to API outgoing request : " + e);
		}
		return clientHttpRequestExecution.execute(httpRequest, bytes);
	}

	private void addAuthHeaderToOutgoingRequest(HttpRequest httpRequest) throws ApiConfigException, ApiException {
		HttpHeaders headers = httpRequest.getHeaders();
		EsoOAuthData esoOAuthData = null;
		String oathToken = null;

		// APIRequestInterceptor is used by restTemplate for all outbound
		// restful request from OCA to API. So we should try to get token from
		// Request Attribute only when the request is initiated from front-end,
		// other server side initiated http requests such as PreloadService
		// won't have token, so it should skip this step
		// and get token from ClientCredentialsUtil in next step

		RequestAttributes requestAttribute = RequestContextHolder.getRequestAttributes();
		if (requestAttribute != null) {
			HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttribute).getRequest();

			String sessionId = iESOSessionService.getSessionId(httpServletRequest);
			esoOAuthData = iESOSessionService.getOAuthDataAttribute(sessionId);
			if (esoOAuthData != null) {
				oathToken = esoOAuthData.getToken();
			} else {
			  if (httpRequest.getHeaders().containsKey(SAML_OAUTH_TOKEN)) {
			    oathToken = httpRequest.getHeaders().get(SAML_OAUTH_TOKEN).get(0);
//			    httpRequest.getHeaders().remove(SAML_OAUTH_TOKEN);
        }
				logger.info("APIRequestInterceptor: Session/OAuth token is null, will use client credentials token");
			}
		}

		if (oathToken == null) {
			oathToken = ClientCredentialsUtil.getClientCredentials();
		}

    headers.add(javax.ws.rs.core.HttpHeaders.AUTHORIZATION, addBearerToAuthToken(oathToken));
    headers.add(X_SECONDARY_AUTH, addBearerToAuthToken(oathToken));
	}
}
