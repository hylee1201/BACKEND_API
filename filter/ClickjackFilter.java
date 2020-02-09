package com.td.dcts.eso.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class ClickjackFilter implements Filter {
	
	private static final String DENY = "DENY";

	@Override
	public void destroy() {
		
	}

	/*
	 * This filter prevents all content from being embedded in a frame (stops Clickjacking)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response;
        res.addHeader("X-FRAME-OPTIONS", DENY);
        chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig cfg) throws ServletException {
		
	}

}
