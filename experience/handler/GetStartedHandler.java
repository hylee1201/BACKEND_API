package com.td.dcts.eso.experience.handler;

import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.client.ApplicationManagementRestClient;
import com.td.dcts.eso.experience.model.associatesapi.AssociateOrganizations;
import com.td.dcts.eso.experience.model.associatesapi.Associates;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.dcts.eso.experience.model.response.WealthClientMasterInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class GetStartedHandler {

  @Autowired
  private ApplicationManagementRestClient applicationManagementRestClient;

  public WealthClientMasterInfo getResponse(MetaData metaData, HttpHeaders httpHeaders, String getStartedProfileRetrieveURL) throws ApiException {

    WealthClientMasterInfo wcm = applicationManagementRestClient.getResponse(metaData, null, httpHeaders, getStartedProfileRetrieveURL, WealthClientMasterInfo.class);
    return wcm;
  }
  public AssociateOrganizations getRelatedInternalOrganizationsResponse(MetaData metaData, HttpHeaders httpHeaders, String getStartedProfileRetrieveURL) throws ApiException {

    AssociateOrganizations organizations = applicationManagementRestClient.getResponse(metaData, null, httpHeaders, getStartedProfileRetrieveURL, AssociateOrganizations.class);
    return organizations;
  }
  public Associates getAssociateResponse(MetaData metaData, HttpHeaders httpHeaders, String getAssociateProfileRetrieveURL) throws ApiException {

    Associates associate = applicationManagementRestClient.getResponse(metaData, null, httpHeaders, getAssociateProfileRetrieveURL, Associates.class);
    return associate;
  }
}
