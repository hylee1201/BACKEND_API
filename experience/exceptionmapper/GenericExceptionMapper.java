package com.td.dcts.eso.experience.exceptionmapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import com.td.coreapi.common.status.ApiError;
import com.td.coreapi.common.status.ApiException;
import com.td.coreapi.common.status.Status;
import com.td.dcts.eso.experience.util.ExceptionUtil;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

	static final XLogger logger = XLoggerFactory.getXLogger(GenericExceptionMapper.class);

	@Override
	public Response toResponse(Throwable exception) {
		if (!(exception instanceof ApiException)) {
			logger.error("Exception raised by underlying API {} ", exception);
			Status status = ExceptionUtil.buildServerErrorStatus();
			ApiError apiError = new ApiError(status);
			return Response.status(Integer.valueOf(status.getServerStatusCode())).entity(apiError)
					.type(MediaType.APPLICATION_JSON).build();
		} else {
			return null;
		}
	}

}
