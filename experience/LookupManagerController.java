package com.td.dcts.eso.experience;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.td.coreapi.common.status.ApiException;
import com.td.eso.rest.response.model.LookupModel;


//@Path("/lookupManager")
//@Controller
public class LookupManagerController extends BaseController{

/*
	@Autowired
	private LookupManagementRestClient lookupManagementRestClient;

	static final XLogger LOGGER = XLoggerFactory.getXLogger(LookupManagerController.class);

	@GET
	@Path("/lists/{listCd}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response retrieveLookup(@PathParam("listCd") String listCd, @Context HttpServletRequest httpServletRequest) throws ApiException {
		List<LookupModel> LookupModelList = lookupManagementRestClient.callLoadLookup(listCd);
		LOGGER.debug("Retrieved lookup list {}", listCd);
		return Response.ok(LookupModelList, MediaType.APPLICATION_JSON).build();
	}

	@GET
	@Path("/listOccupations/{industryId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response retriveOccupationsByIndustry(@PathParam("industryId") String industryId, @Context HttpServletRequest httpServletRequest) throws ApiException {
		Map<String, List<LookupModel>> industryOccupationMap = lookupManagementRestClient.callLoadOccupation();
		LOGGER.debug("Retrieved ocp list for industry {}", industryId);
		return Response.ok(industryOccupationMap.get(industryId), MediaType.APPLICATION_JSON).build();
	}

	@GET
	@Path("/listProvOrState/{countryCd}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response retriveProvOrStateByCountry(@PathParam("countryCd") String countryCd, @Context HttpServletRequest httpServletRequest) throws ApiException {
		Map<String, List<LookupModel>> provStateMap = lookupManagementRestClient.callLoadProvState();
		LOGGER.debug("Retrieved province & state list for couintry {}", countryCd);
		return Response.ok(provStateMap.get(countryCd), MediaType.APPLICATION_JSON).build();
	}
*/

}
