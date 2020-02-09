package com.td.dcts.eso.experience.facade;

import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.dao.CustomerDAO;
import com.td.dcts.eso.experience.model.response.WealthClientMasterInfo;
import com.td.dcts.eso.experience.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class ValidationFacade {
  @Autowired
  private CustomerDAO customerDAO;
  @Autowired
  SessionUtil sessionUtil;


  public boolean isBootstrapValid(HttpServletRequest httpServletRequest) throws ApiException, IOException {
    WealthClientMasterInfo wealthCMInfo;
    String draftProfile = null;
    try {
      draftProfile = sessionUtil.getDraftClientProfile(httpServletRequest);
    }
    catch (Exception e)
    {
      return true;
      //we ignore the exception because if no DraftclientProfile is found then it means the session is new and this is good.
    }
    if (draftProfile != null) {
      wealthCMInfo = customerDAO.getWealthClientMasterInfo(sessionUtil.getDraftClientProfile(httpServletRequest));
      if (wealthCMInfo.getUserIdentity() != null) {
        //It means that the applicationId has already been used. We need to create a blank new Application and Session.
        return false;
      } else {
        return true;
      }
    }
    return true;
    }
}
