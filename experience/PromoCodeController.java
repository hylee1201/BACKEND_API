package com.td.dcts.eso.experience;

import com.td.dcts.eso.experience.facade.PromoCodeFacade;
import com.td.dcts.eso.experience.model.response.PromoCodeStatus;
import com.td.eso.rest.response.model.LookupModel;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/promoCode")
@Controller
public class PromoCodeController {

  static final XLogger LOGGER = XLoggerFactory.getXLogger(PromoCodeController.class);

  @Autowired
  private PromoCodeFacade promoCodeFacade;

  @POST
  @Path("/validate")
  @Produces(MediaType.APPLICATION_JSON)
  public Response validatePromoCode(@Context HttpServletRequest httpServletRequest, @RequestBody LookupModel promoCode) {

    LOGGER.info("PromoCodeController: Start Promo Code Validation.");

    PromoCodeStatus promoCodeStatus = promoCodeFacade.validatePromoCode(promoCode);

    return Response.ok(promoCodeStatus, MediaType.APPLICATION_JSON).build();

  }

}
