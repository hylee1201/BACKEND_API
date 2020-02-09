package com.td.dcts.eso.filter;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.td.coreapi.common.config.ApiConfig;
import com.td.coreapi.common.status.AdditionalSeverity;
import com.td.coreapi.common.status.AdditionalStatus;
import com.td.coreapi.common.status.Severity;
import com.td.coreapi.common.status.Status;
import com.td.dcts.eso.experience.constants.ExperienceConstants;

/**
 * Cross-site scripting (XSS) filter
 *
 */
public class XssFilter implements Filter {

	static final XLogger LOGGER = XLoggerFactory.getXLogger(XssFilter.class);
	private final static String GET = "GET";
	private static String ALLOWED_REGEX;
	private static String BAD_REGEX;
	private static final String BAD_URL = "0001";
	private static final String BAD_PARAMS = "0002";
	private static final String BAD_REQ_METHOD = "0003";

    @Override
    public void destroy() {

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        LOGGER.info("XSS Filter Started.");

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURL = httpRequest.getRequestURL().toString();

        if(!GET.equals(httpRequest.getMethod())) {
        	LOGGER.error("Request method is not GET. Terminating the request.");
        	buildErrorResponse(BAD_REQ_METHOD, response);
            return;
        }

        if (requestURL.matches(ALLOWED_REGEX + BAD_REGEX + ALLOWED_REGEX) || !requestURL.matches(ALLOWED_REGEX)) {
            LOGGER.error("Request URL is invalid - \"" + "Input URL is \"" + requestURL + "\". Terminating the request.");
            buildErrorResponse(BAD_URL, response);
            return;
        }

        request.setCharacterEncoding("UTF-8");


        //For our purposes, even having a parameter on the request indicates bad request. Leaving parameter checking logic below
        //in case we ever need it

        if(request.getParameterMap().size() > 0) {
           	LOGGER.error("Request contains unexpected parameters. Terminating the request.");
        	buildErrorResponse(BAD_PARAMS, response);
            return;
        }

        // Iterator paramsItr = request.getParameterMap().entrySet().iterator();
        // Iterating over each request parameter.
        /*while (paramsItr.hasNext()) {

            Map.Entry<String, String[]> paramObj = (Map.Entry<String, String[]>) paramsItr.next();
            LOGGER.info("Request parameter name:: " + paramObj.getKey() + ", Values are:: ");

            request.setCharacterEncoding("UTF-8");

            // Checking the NAME of request parameter
            if (paramObj.getKey().matches(ALLOWED_REGEX + BAD_REGEX + ALLOWED_REGEX) || !paramObj.getKey().matches(ALLOWED_REGEX)) {
            	LOGGER.error("Request parameter name is invalid - \"" + paramObj.getKey() + "\"." + "Input URL is \"" + requestURL + "\". Terminating the request.");
            	buildErrorResponse(BAD_PARAM_NAME, response);
                return;
            }

            // Checking the VALUES of request parameter
            for (String paramVal : paramObj.getValue()) {

                String value = paramVal;

                LOGGER.info(value);

                byte ptext[] = value.getBytes("UTF-8");
                String newValue = new String(ptext, "UTF-8");

                LOGGER.info("Encoded value - " + newValue);

                if (newValue.matches(ALLOWED_REGEX + BAD_REGEX + ALLOWED_REGEX) || !newValue.matches(ALLOWED_REGEX)) {
                	LOGGER.error("Request parameter \"" + paramObj.getKey() + "\" is having a bad value \"" + value + "\"." + "Input URL is \"" + requestURL + "\". Terminating the request.");
                	buildErrorResponse(BAD_PARAM_VALUE, response);
                	return;
                }
            }
        }*/

        LOGGER.info("XSS Filter Complete.");
        chain.doFilter(request, response);

    }

    private void buildErrorResponse(String internalErrorCode, ServletResponse response) throws IOException {
    	response.setContentType("application/json");
    	AdditionalStatus errorDetail = new AdditionalStatus(400, internalErrorCode, AdditionalSeverity.Error, "Bad Request - HTML Resource.");
    	Status errResponse = new Status("400", Severity.Error, errorDetail);
    	response.getWriter().write(new ObjectMapper().writeValueAsString(errResponse));
    }

    @Override
    public void init(FilterConfig cfg) throws ServletException {
    	ALLOWED_REGEX = ApiConfig.getInstance().getPropertiesConfig(ExperienceConstants.ENV_FILE_NAME).getProperty(ExperienceConstants.XSS_HTML_ALLOWED_REGEX);
    	BAD_REGEX = ApiConfig.getInstance().getPropertiesConfig(ExperienceConstants.ENV_FILE_NAME).getProperty(ExperienceConstants.XSS_HTML_BAD_REGEX);
    	if(ALLOWED_REGEX == null || BAD_REGEX == null) {
    		throw new ServletException("Unable to parse regular expressions from properties file.");
    	}
    }

}
