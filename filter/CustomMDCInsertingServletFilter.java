package com.td.dcts.eso.filter;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.td.coreapi.common.config.ApiConfig;
import com.td.dcts.eso.experience.constants.ExperienceConstants;

/**
 * A servlet filter that inserts various values retrieved from the incoming http
 * request into the MDC.
 */
public class CustomMDCInsertingServletFilter implements Filter {

  static final XLogger logger = XLoggerFactory.getXLogger(CustomMDCInsertingServletFilter.class);
  public static final String REQUEST_URI = "RequestURI";
  public static final String IP_ADDRESS = "IPAddress";
  public static final String TDESOSESSIONID = "TDESOSESSIONID";
  public static final String HEADERS_IGNORE = "headers.to.ignore.in.log";

  public static final String PIPE_DELIMITER = " | ";

  public void destroy() {
    // do nothing
  }

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    throws IOException, ServletException {

    insertIntoMDC(request);
    try {
      chain.doFilter(request, response);
    } finally {
      clearMDC();
    }
  }

  void insertIntoMDC(ServletRequest request) {

    if(request instanceof HttpServletRequest) {
      HttpServletRequest httpServletRequest = (HttpServletRequest) request;
      StringBuffer buffer = new StringBuffer();
      buffer.append(REQUEST_URI).append("=").append(((HttpServletRequest) request).getRequestURI()).append(PIPE_DELIMITER);
      buffer.append(IP_ADDRESS).append("=").append(((HttpServletRequest) request).getRemoteAddr()).append(PIPE_DELIMITER);

      Cookie[] cookies = ((HttpServletRequest) request).getCookies();

      if(cookies != null) {
        for(Cookie cookie : cookies) {
          if(StringUtils.equalsIgnoreCase(TDESOSESSIONID, cookie.getName())) {
            buffer.append(TDESOSESSIONID).append("=").append(cookie.getValue()).append(PIPE_DELIMITER);
            break;
          }
        }
      }

      String headersToIgnore = ApiConfig.getInstance().getPropertiesConfig(ExperienceConstants.ENV_FILE_NAME)
        .getProperty(HEADERS_IGNORE);

      // Add all the headers from the request except the few
      // mentioned in environment properties
      Enumeration headerNames = httpServletRequest.getHeaderNames();
      if(headerNames != null) {
        while(headerNames.hasMoreElements()) {

          String key = (String) headerNames.nextElement();
          String value = httpServletRequest.getHeader(key);

          if(!StringUtils.containsIgnoreCase(headersToIgnore, key)) {
            buffer.append(key).append("=").append(value).append(PIPE_DELIMITER);
          }
        }
      }

      MDC.put("tracking.information", buffer.toString());
    }

  }

  void clearMDC() {
    MDC.remove("tracking.information");
  }

  public void init(FilterConfig arg0) throws ServletException {
    // do nothing
  }
}
