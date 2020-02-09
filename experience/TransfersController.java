package com.td.dcts.eso.experience;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.dao.CustomerDAO;
import com.td.dcts.eso.experience.facade.TransfersFacade;
import com.td.dcts.eso.experience.model.response.*;
import com.td.dcts.eso.experience.util.ExceptionUtil;
import com.td.dcts.eso.experience.util.RestUtil;
import com.td.dcts.eso.experience.util.SessionUtil;
import com.td.dcts.eso.session.model.EsoJsonData;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

@Path("/transfers")
@Component
public class TransfersController extends BaseController {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(TransfersController.class);

    @Autowired
    private CustomerDAO customerDAO;

    @Autowired
    private TransfersFacade transfersFacade;

    private ObjectMapper objectMapper = new ObjectMapper();

    @GET
    @Path("/retrieveInstitutionDetails/{institutionId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response retrieveInstitutionDetails(@Context HttpServletRequest httpServletRequest,
                                               @PathParam("institutionId") String institutionId) throws ApiException {
        LOGGER.info("Inside retrieveInstitutionDetails");

        Institution institution = transfersFacade.retrieveInstitutionById(institutionId);

        if (institution == null) {
            LOGGER.error("Institution not found id " + institutionId);
            throw new ApiException(ExceptionUtil.buildServerErrorStatus());
        }

        return Response.ok(institution).build();
    }


    @POST
    @Path("/submit")
    @Consumes(MediaType.APPLICATION_JSON)
    public @ResponseBody List<FundsTransferItem> submit(@Context HttpServletRequest httpServletRequest,
                                                        @RequestBody FundsTransfer fundsTransfer) throws Exception {
        LOGGER.info("Inside submit transfer");

        String draftClientProfile = sessionUtil.getDraftClientProfile(httpServletRequest);
        WealthClientMasterInfo wealthClientMasterInfo = customerDAO.getWealthClientMasterInfo(draftClientProfile);
        if (!transfersFacade.validate(fundsTransfer, wealthClientMasterInfo)) {
            throw new ApiException(ExceptionUtil.buildServerErrorStatus());
        }

        String connectId = sessionUtil.getConnectIdFromSession(httpServletRequest);
        EsoJsonData esoJsonData = sessionUtil.getSessionData(httpServletRequest);
        MetaData metaData = metaDataUtil.populateMetaData(connectId, esoJsonData, ExperienceConstants.FINISH_STAGE);
        String clientIPAddress = httpServletRequest.getRemoteAddr();
        String locale = (String) esoJsonData.get(SessionUtil.SESSION_KEY_LOCALE);
        HttpHeaders httpHeaders = RestUtil.buildRequestHeaders(locale, clientIPAddress);

        // Transferring the MetaData to the transfers object
        fundsTransfer.setMetaData(metaData);

        // The target Institution won't come from the front-end, we have to set it ourselves
        transfersFacade.setTargetInstitution(fundsTransfer);

        // Handling the requests that are supposed to transfer both USD and CAD currencies
        transfersFacade.separateTransfersInBothCurrencies(fundsTransfer);

        // Ensuring the account numbers of all funds transfer items match their currencies, as the UI sends
        // CAD by default, to simplify the user experience and list only one currency of each account type
        transfersFacade.adjustTargetAccountNumbers(fundsTransfer, wealthClientMasterInfo);

        List<FundsTransferItem> fundsTransferItems = transfersFacade.submit(httpHeaders, fundsTransfer);
        fundsTransfer.setFundsTransferItems(fundsTransferItems);
        fundsTransfer.setSvgSignature("");
        wealthClientMasterInfo.setFundsTransfers(fundsTransfer);

        // Update session
        String jsonEncodedWealthClientMasterInfo = objectMapper.writeValueAsString(wealthClientMasterInfo);
        sessionUtil.setDraftClientProfile(httpServletRequest, jsonEncodedWealthClientMasterInfo);

        return fundsTransferItems;
    }

    @GET
    @Path("/download/{documentId}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response retrievePDF(@Context HttpServletRequest httpServletRequest,
                                @PathParam("documentId") String documentId) throws ApiException, IOException {

        if (documentId == null || documentId.trim().isEmpty()) {
            throw new ApiException(ExceptionUtil.buildServerErrorStatus("Invalid document ID"));
        }

        String connectId = sessionUtil.getConnectIdFromSession(httpServletRequest);
        EsoJsonData esoJsonData = sessionUtil.getSessionData(httpServletRequest);
        MetaData metaData = metaDataUtil.populateMetaData(connectId, esoJsonData, ExperienceConstants.GENERAL_STAGE);
        String clientIPAddress = httpServletRequest.getRemoteAddr();
        String locale = (String) esoJsonData.get(SessionUtil.SESSION_KEY_LOCALE);
        HttpHeaders httpHeaders = RestUtil.buildRequestHeaders(locale, clientIPAddress);

        final String documentIdWithoutCurlyBraces = documentId.trim().replace("{", "").replace("}", "");

        String draftClientProfile = sessionUtil.getDraftClientProfile(httpServletRequest);
        WealthClientMasterInfo wealthClientMasterInfo = customerDAO.getWealthClientMasterInfo(draftClientProfile);

        transfersFacade.ensureDocumentIDBelongsToFundsTransfer(documentIdWithoutCurlyBraces,
                wealthClientMasterInfo.getFundsTransfer());

        byte[] fileContents = transfersFacade.retrievePDF(documentIdWithoutCurlyBraces, metaData, httpHeaders);

        return Response.ok(fileContents).type(MediaType.valueOf("application/pdf")).build();
    }
}
