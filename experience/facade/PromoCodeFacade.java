package com.td.dcts.eso.experience.facade;

import com.td.dcts.eso.experience.ApplicationDataController;
import com.td.dcts.eso.experience.model.response.PromoCodeStatus;
import com.td.dcts.eso.experience.util.PromoCodeUtil;
import com.td.eso.rest.response.model.LookupModel;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PromoCodeFacade {

  private static final XLogger LOGGER = XLoggerFactory.getXLogger(ApplicationDataController.class);


  public PromoCodeStatus validatePromoCode(LookupModel promoCode) {

    LOGGER.info("PromoCodeFacade: Validating for PromoCode: " + promoCode.getCode());

    PromoCodeStatus promoCodeStatus = new PromoCodeStatus();

    //default promo code status to false
    promoCodeStatus.setCodeValid(false);

    //TODO: Promo Codes will be retrieved from external API (with Cache) instead of Json configs in later releases
    LookupModel[] promoCodes = PromoCodeUtil.getPromoCode();

    for (LookupModel code : promoCodes ) {
      //Promo Code is case insensitive for now (Business will clarify on this)
      if(code.getCode().equalsIgnoreCase(promoCode.getCode().trim())) {
        LOGGER.info("PromoCodeFacade: PromoCode entered is valid: " + promoCode.getCode());
        promoCodeStatus.setCodeValid(true);
      }
    }

    if(!promoCodeStatus.getCodeValid()) {
      LOGGER.warn("PromoCodeFacade: PromoCode entered is not valid: " + promoCode.getCode());
    }

    return promoCodeStatus;
  }
}
