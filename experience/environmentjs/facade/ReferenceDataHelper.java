package com.td.dcts.eso.experience.environmentjs.facade;

import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.eso.rest.response.model.LookupModel;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;

public interface ReferenceDataHelper {
  Map<String, List<? extends LookupModel>> getReferenceData(MetaData metaData, HttpHeaders headers) throws ApiException;

  List<? extends LookupModel> getReferenceData(ReferenceDataEnum referenceDataEnum);
}
