package com.td.dcts.eso.experience;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.event.response.model.SubApplicationInfo;
import com.td.dcts.eso.experience.client.DBEventsRestClient;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.util.ExceptionUtil;
import com.td.dcts.eso.experience.util.MetaDataUtil;
import com.td.dcts.eso.experience.util.RestUtil;
import com.td.dcts.eso.experience.util.SessionUtil;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.dcts.eso.session.model.EsoJsonData;

@Controller
@Path("/events")
public class DBEventsController extends BaseController{

	@Autowired
	private DBEventsRestClient dbEventsRestClient ;

	static final XLogger LOGGER = XLoggerFactory.getXLogger(DBEventsRestClient.class);

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEvents(@Context HttpServletRequest httpServletRequest) throws ApiException {

		MetaData metaData = getMetaData(httpServletRequest, ExperienceConstants.GENERAL_STAGE);
		HttpHeaders httpHeaders = getHttpHeaders(httpServletRequest);

		//TODO: applicationId is already in metadata variable. Do we just reuse it?
		Integer appID = metaData.getApplicationId();
		String subAppID = null;
		List<SubApplicationInfo> subAppIDs = metaData.getSubApplicationList();
		if(subAppIDs != null) {
			if(!subAppIDs.isEmpty()) {
				subAppID = subAppIDs.get(0).getSubApplicationId();
			}
		}

		ResponseEntity<Object> responseEntity = dbEventsRestClient.getEvents(appID, subAppID, metaData, httpHeaders);
		if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
			return Response.ok(responseEntity.getBody(), MediaType.APPLICATION_JSON).build();
		} else {
			throw new ApiException(ExceptionUtil.buildErrorStatusFromApiErrorStatus(responseEntity));
		}
	}
}
