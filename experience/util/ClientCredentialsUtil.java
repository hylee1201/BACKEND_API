package com.td.dcts.eso.experience.util;

import com.td.coreapi.common.oauthsdk.model.OAuthRequest;
import com.td.coreapi.common.oauthsdk.model.OAuthRequestBuilder;
import com.td.coreapi.common.oauthsdk.model.OAuthResponse;
import com.td.coreapi.common.oauthsdk.service.OAuthSDKService;
import com.td.coreapi.common.security.OAuthValidator;
import com.td.coreapi.common.status.ApiConfigException;
import com.td.coreapi.common.status.ApiException;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class ClientCredentialsUtil {

	static final XLogger logger = XLoggerFactory.getXLogger(ClientCredentialsUtil.class);

	public static String getClientCredentials() throws ApiException {
		String clientCredentialToken = null;
		try {
			if (!InMemoryCache.isKeyExistInCache(InMemoryCache.KEY_CLIENT_CREDENTIALS_TOKEN)) {
				clientCredentialToken = getClientCredentialToken();
	            InMemoryCache.addToCache(InMemoryCache.KEY_CLIENT_CREDENTIALS_TOKEN, clientCredentialToken);
	        } else {
	            clientCredentialToken = (String) InMemoryCache.getObjectFromCache(InMemoryCache.KEY_CLIENT_CREDENTIALS_TOKEN);
	            // check if token has expired
	            boolean isValidToken = OAuthValidator.isValidToken(clientCredentialToken);
	            if (!isValidToken) {
	            	clientCredentialToken = getClientCredentialToken();
		            InMemoryCache.addToCache(InMemoryCache.KEY_CLIENT_CREDENTIALS_TOKEN, clientCredentialToken);
	            }
	        }
		} catch (ApiConfigException e) {
			logger.debug("Error getting client credentials token. " + e.getMessage());
			throw new RuntimeException("Error getting client credentials token.");
		} 
		return clientCredentialToken;
	}
	
	private static String getClientCredentialToken() throws ApiConfigException, ApiException {
		OAuthRequest authRequest = new OAuthRequestBuilder().withClientCredentialsRequest().endClientCredentialsRequest().build();
        OAuthResponse authResponse = new OAuthSDKService().getToken(authRequest);
        return authResponse.getAccessToken();
	}

	//get access token from SAML assertion
	public static String getCredentialTokenFromSAML(String base64EncodedSAMLAccertion) throws ApiConfigException, ApiException {
    OAuthRequest oauthRequest2 = new OAuthRequestBuilder().withOAuthRequestNameFromJSON("oauth_request_2").withSAMLBearerAssertionRequest()
      .withBase64EncodedSAMLAssertion(base64EncodedSAMLAccertion)
      .endSAMLBearerAssertionRequest()
      .build();
    OAuthResponse oauthResponse = new OAuthSDKService().getToken(oauthRequest2);

    return oauthResponse.getAccessToken();
  }
}
