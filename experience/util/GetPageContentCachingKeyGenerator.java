package com.td.dcts.eso.experience.util;


import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Caching key generator for wcm content caching.
 *
 * @author yuej2
 */
@Component
public class GetPageContentCachingKeyGenerator implements KeyGenerator {

  private static final String DEFAULT_CACHING_KEY = "getPageContent";

  @Override
  public Object generate(Object object, Method method, Object... params) {

    if(null == params || params.length != 3) {
      return DEFAULT_CACHING_KEY;
    }

		/*
     * params[0] = Locale locale
		 * params[1] = Map<String, String> override
		 * params[2] = Stirng pageName
		 */

    StringBuilder key = new StringBuilder();
    key.append(params[0]).append("_").append(params[1].hashCode()).append("_").append(params[2]);

    return key.toString();
  }
}
