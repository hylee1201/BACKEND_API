package com.td.dcts.eso.experience.handler;

import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.model.response.FundsTransfer;
import com.td.dcts.eso.experience.model.response.FundsTransferItem;
import com.td.dcts.eso.experience.model.response.MetaData;
import com.td.dcts.eso.experience.util.CustomErrorHandler;
import com.td.dcts.eso.experience.util.ExceptionUtil;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

@Service
public class FundsTransferHandler {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(FundsTransferHandler.class);

    @Value("${resturl.funds.download}")
    String pdfURL;

    @Value("${resturl.funds.submitfunds}")
    String submitFundsUrl;

    @Autowired
    @Qualifier("restTemplatePdf")
    private RestTemplate restTemplate;

    public List<FundsTransferItem> submit(HttpHeaders httpHeaders, FundsTransfer fundsTransfer) throws ApiException {
        LOGGER.debug("Inside generateTransferForm");

        HttpEntity<FundsTransfer> httpEntity = new HttpEntity<>(fundsTransfer, httpHeaders);
        ResponseEntity<FundsTransferItem[]> responseEntity = restTemplate.exchange(submitFundsUrl,
                HttpMethod.POST, httpEntity, FundsTransferItem[].class);

        if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            LOGGER.debug("End of generateTransferForm");
            return Arrays.asList(responseEntity.getBody());
        } else {
            LOGGER.error("Error in retrieving PDF");
            throw new ApiException(ExceptionUtil.buildErrorStatusFromApiErrorStatus(responseEntity));
        }
    }

    public byte[] retrievePDF(String documentId,
                              MetaData metaData,
                              MultiValueMap<String, String> httpHeaders) throws ApiException {
        LOGGER.info("documentId: {}", documentId);
        try {
            restTemplate.setErrorHandler(new CustomErrorHandler());

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(pdfURL).pathSegment(documentId);

            HttpEntity<MetaData> httpEntity = new HttpEntity<>(metaData, httpHeaders);
            ResponseEntity<byte[]> response = restTemplate.exchange(builder.build().encode().toUri(),
                    HttpMethod.POST, httpEntity, byte[].class);

            return response.getBody();
        } catch (HttpServerErrorException e) {
            LOGGER.error("FundsTransferHandler.retrievePDF", e);
            throw new ApiException(ExceptionUtil.buildServerErrorStatus(), e);
        }
    }
}
