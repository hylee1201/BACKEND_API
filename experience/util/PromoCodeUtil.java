package com.td.dcts.eso.experience.util;

import com.td.coreapi.common.config.ApiConfig;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.eso.rest.response.model.LookupModel;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.cache.annotation.Cacheable;

public class PromoCodeUtil {

  static final XLogger LOGGER = XLoggerFactory.getXLogger(PromoCodeUtil.class);

  //Not intended use
  private PromoCodeUtil() {
    throw new IllegalAccessError(PromoCodeUtil.class.getName());
  }

  @Cacheable(value=ExperienceConstants.PROMO_CODE_CACHE, key="#root.methodName")
  public static LookupModel[] getPromoCode() {
    LOGGER.info("PromoCodeUtil: Retrieving Promo Code from Json Configuration.");
    return (LookupModel[]) ApiConfig.getInstance().getJsonObjectConfig(ExperienceConstants.FILENAME_PROMO_CODE, LookupModel[].class);
  }
}
