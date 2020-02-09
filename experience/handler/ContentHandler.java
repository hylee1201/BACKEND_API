package com.td.dcts.eso.experience.handler;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.td.dcts.eso.experience.ContentController;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.model.response.Product;

@Component
public class ContentHandler {

  static final XLogger logger = XLoggerFactory.getXLogger(ContentController.class);

  private static final ObjectMapper MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

//Removing the cache as the getallProducts sometime is not picking product list correctly
//The operation to pick products only happens once per application
//  @Cacheable(value= ExperienceConstants.REFERENCE_DATA_LOOKUP_CACHE, key="#root.methodName + #locale")
  public List<Product> getAllProducts(String locale) throws IOException {

    logger.debug("getAllProducts.locale={}", locale);

    ClassLoader classLoader = getClass().getClassLoader(); // TODO move to preload service
    List<Product> products = MAPPER.readValue(classLoader.getResourceAsStream("productContent_" + locale + ".json"), new TypeReference<List<Product>>() {});

    return products;
  }

  public Map<String, Product> getAllProductsMap(String locale) throws IOException {
    //put the list into a map
    Map<String, Product> productMap = new HashMap<>();
    List<Product> products = getAllProducts(locale);
    for (Product aProduct : products) {
      productMap.put(aProduct.getProductId(), aProduct);
    }

    return productMap;
  }





}
