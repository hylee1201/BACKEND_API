package com.td.dcts.eso.experience.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
public class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {

  private final ObjectMapper mapper;

  public ObjectMapperContextResolver() {
    mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
  }

  @Override
  public ObjectMapper getContext(Class<?> type) {
    return mapper;
  }


}
