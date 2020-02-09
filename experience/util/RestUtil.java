package com.td.dcts.eso.experience.util;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.td.dcts.eso.experience.constants.ExperienceConstants;

@Component
public class RestUtil {

	static final XLogger logger = XLoggerFactory.getXLogger(RestUtil.class);

	public static HttpHeaders buildRequestHeaders(String locale, String clientIPAddress) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set(ExperienceConstants.HTTP_HEADER_CLIENT_IP, clientIPAddress);
		headers.set(ExperienceConstants.HTTP_HEADER_LOCALE, locale);

		return headers;
	}

	public static String appendParamsToUrl(String connectId, String productId, String customerProfileUrl) {
		StringBuffer stringBufferUrl = new StringBuffer(customerProfileUrl);

		stringBufferUrl.append(ExperienceConstants.FORWARD_SLASH);
		stringBufferUrl.append(connectId);
		stringBufferUrl.append(ExperienceConstants.FORWARD_SLASH);
		stringBufferUrl.append(productId);

		return stringBufferUrl.toString();
	}
}
