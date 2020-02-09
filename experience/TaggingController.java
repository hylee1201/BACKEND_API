package com.td.dcts.eso.experience;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.net.URI;

@Component
@Path("/")
public class TaggingController {

  static final XLogger logger = XLoggerFactory.getXLogger(TaggingController.class);

  @Value("${tagging.url:https://nexus.ensighten.com/tdb/eso-dev/Bootstrap.js}")
  private String taggingURL;

  @GET
  @Path("/tagging.js")
  @Produces("application/javascript")
  public Response getTagging(@Context HttpServletRequest httpServletRequest) {

    logger.info("Inside TaggingController. Return Bootstrap.js from: " + taggingURL);
    return Response.seeOther(URI.create(taggingURL)).build();

  }
}
