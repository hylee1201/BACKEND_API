package com.td.dcts.eso.experience;

import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.event.response.model.SubApplicationInfo;
import com.td.dcts.eso.experience.facade.CancelApplicationFacade;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.dcts.eso.experience.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Controller
@Path("/cancelApplicationBranch")
public class CancelApplicationForBranchController extends BaseController {

  @Autowired
  private SessionUtil sessionUtil;
  @Autowired
  private CancelApplicationFacade cancelApplicationFacade;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response cancelApplication(@Context HttpServletRequest httpServletRequest) throws ApiException {
    MetaData metaData = getMetaData(httpServletRequest,"");
    String appID = String.valueOf(metaData.getApplicationId());
    String subAppId = null;
    List<SubApplicationInfo> subAppIDs = metaData.getSubApplicationList();
    if(subAppIDs != null) {
      if(!subAppIDs.isEmpty()) {
        subAppId = subAppIDs.get(0).getSubApplicationId();
      }
    }
    sessionUtil.destroySession(httpServletRequest);
    ResponseEntity response = cancelApplicationFacade.cancelApplication(appID, subAppId);
    return Response.ok(response.getBody(),  MediaType.APPLICATION_JSON).build();
  }
}

