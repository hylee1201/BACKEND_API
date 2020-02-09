package com.td.dcts.eso.filter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

public class ReqResLogFilter extends com.td.coreapi.common.logger.ReqResLogFilter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response,FilterChain chain)
    throws IOException, ServletException {
    if (isMultipartRequest(request)) {
      chain.doFilter (request, response);
    } else {
      super.doFilter(request, response, chain);
    }
  }

  private static final boolean isMultipartRequest(ServletRequest request) {
    if (!(request instanceof HttpServletRequest)) {
      return false;
    } else {
      return HttpMethod.POST.equalsIgnoreCase(((HttpServletRequest)request).getMethod())
        && request.getContentType() != null
        && request.getContentType().toLowerCase().startsWith(MediaType.MULTIPART_FORM_DATA);
    }
  }
}
