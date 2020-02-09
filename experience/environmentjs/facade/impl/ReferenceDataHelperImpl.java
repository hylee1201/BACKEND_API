package com.td.dcts.eso.experience.environmentjs.facade.impl;

import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.environmentjs.facade.ReferenceDataEnum;
import com.td.dcts.eso.experience.environmentjs.facade.ReferenceDataHelper;
import com.td.dcts.eso.experience.environmentjs.handler.ReferenceDataHandler;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.eso.rest.response.model.LookupModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;


@Component
class ReferenceDataHelperImpl implements ReferenceDataHelper {

  private ReferenceDataHandler referenceDataHandler;
  private Map<String, List<? extends LookupModel>> referenceData;
  private static String ACTIVETRADING = "ACTIVE_TRADING";
  @Autowired
  public ReferenceDataHelperImpl(ReferenceDataHandler referenceDataHandler) {
    this.referenceDataHandler = referenceDataHandler;
  }

  @Override
  @Cacheable(value = ExperienceConstants.REFERENCE_DATA_LOOKUP_CACHE, key="#metaData.getProductId()")
  public Map<String, List<? extends LookupModel>> getReferenceData(MetaData metaData, HttpHeaders headers) throws ApiException {
    this.referenceData = referenceDataHandler.callReferenceDataApi(metaData, headers, new ParameterizedTypeReference<Map<String, List<? extends LookupModel>>>() {});
    List<LookupModel> accountPurposeCash = buildAccountPurposeTfsaRrsp((List<LookupModel>) referenceData.get(ReferenceDataEnum.ACCOUNTPURPOSES.getReferenceDataName()));
    this.referenceData.put(ReferenceDataEnum.ACCOUNTPURPOSESTFSARRSP.getReferenceDataName(), accountPurposeCash);
    return this.referenceData;
  }

  @Cacheable(value= ExperienceConstants.REFERENCE_DATA_LOOKUP_CACHE, key="#referenceDataEnum")
  public List<? extends LookupModel> getReferenceData(ReferenceDataEnum referenceDataEnum){
    return referenceData.get(referenceDataEnum.getReferenceDataName());
  }

  private List<LookupModel>  buildAccountPurposeTfsaRrsp(List<LookupModel> accountPurpose){
    List<LookupModel> output = new ArrayList<>();
      for (LookupModel referenceDataItem : accountPurpose) {
        if (!referenceDataItem.getCode().equalsIgnoreCase(ACTIVETRADING)) {
          output.add(referenceDataItem);
        }
      }
      return output;
    }
}
