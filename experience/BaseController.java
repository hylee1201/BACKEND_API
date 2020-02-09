package com.td.dcts.eso.experience;

import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.dcts.eso.experience.util.MetaDataUtil;
import com.td.dcts.eso.experience.util.RestUtil;
import com.td.dcts.eso.experience.util.SessionUtil;
import com.td.dcts.eso.session.model.EsoJsonData;
import com.td.dcts.eso.session.model.EsoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;

public abstract class BaseController {

	@Autowired
	protected SessionUtil sessionUtil;
	@Autowired
	protected MetaDataUtil metaDataUtil;

	public BaseController() {
		super();
	}

	protected MetaData getMetaData(HttpServletRequest httpServletRequest, String stage, EsoSession... newEsoSession) throws ApiException {
		String connectId = null;
		if (newEsoSession == null || newEsoSession.length < 1) {
		  //From the SSO route, the connect_id is not the user ID.
		  connectId = sessionUtil.getConnectIdFromSession(httpServletRequest, newEsoSession);
    }
		EsoJsonData esoJsonData = sessionUtil.getSessionData(httpServletRequest, newEsoSession);
		MetaData metaData = metaDataUtil.populateMetaData(connectId, esoJsonData, stage);
		return metaData;
	}

	protected HttpHeaders getHttpHeaders(HttpServletRequest httpServletRequest, EsoSession... newEsoSession) throws ApiException {
	  String locale;

	  //Eliminate unnecessary DB call most of the time to get locale from the http header
	  if(httpServletRequest.getHeader(ExperienceConstants.HTTP_HEADER_LOCALE) != null) {
      locale = httpServletRequest.getHeader(ExperienceConstants.HTTP_HEADER_LOCALE);
    } else {
      EsoJsonData esoJsonData = sessionUtil.getSessionData(httpServletRequest, newEsoSession);
      locale = (String) esoJsonData.get(SessionUtil.SESSION_KEY_LOCALE);
    }

    String clientIPAddress = httpServletRequest.getRemoteAddr();

    return RestUtil.buildRequestHeaders(locale, clientIPAddress);
	}

	protected boolean isEmpty(String value){
		return value == null || value.trim().isEmpty();
	}
}
