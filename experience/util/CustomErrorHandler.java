package com.td.dcts.eso.experience.util;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class CustomErrorHandler implements ResponseErrorHandler {

	/**
	 * Indicates whether the given response has any errors.
	 * Implementations will typically inspect the {@link ClientHttpResponse#getStatusCode() HttpStatus}
	 * of the response.
	 * @param response the response to inspect
	 * @return {@code true} if the response has an error; {@code false} otherwise
	 * @throws IOException in case of I/O errors
	 */
	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		if (HttpStatus.OK.equals(response.getStatusCode())) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Handles the error in the given response.
	 * This method is only called when {@link #hasError(ClientHttpResponse)} has returned {@code true}.
	 * @param response the response with the error
	 * @throws IOException in case of I/O errors
	 */
	@Override
	public void handleError(ClientHttpResponse response) throws IOException {

	}

}
