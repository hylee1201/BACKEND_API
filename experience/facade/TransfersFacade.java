package com.td.dcts.eso.experience.facade;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.td.coreapi.common.status.ApiException;
import com.td.coreapi.common.status.Status;
import com.td.dcts.eso.experience.handler.FundsTransferHandler;
import com.td.dcts.eso.experience.helper.TransfersHelper;
import com.td.dcts.eso.experience.model.response.*;
import com.td.dcts.eso.experience.util.ExceptionUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

@Service
public class TransfersFacade {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(TransfersFacade.class);
    private static final String BOTH = "both";
    private static final String CAD = "CAD";
    private static final String USD = "USD";

    private static final String TD_INSTITUTION_ID = "440";

    private ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Autowired
    private TransfersHelper transfersHelper;

    @Autowired
    private FundsTransferHandler transferHandler;

    private FundsTransferItem makeCopy(FundsTransferItem fundsTransferItem) throws ApiException {
        try {
            String fundTransferInJson = objectMapper.writeValueAsString(fundsTransferItem);
            return objectMapper.readValue(fundTransferInJson, FundsTransferItem.class);
        } catch (IOException e) {
            LOGGER.error("Error in processing transfers.", e);
            throw new ApiException(new Status(), "Error in processing transfers.");
        }
    }

    private String findRelatedAccountNumber(String accountType, String currencyType, List<AccountNumber> accounts) {
        for (AccountNumber accountNumber : accounts) {
            if (accountNumber.getAccountType().equalsIgnoreCase(accountType) &&
                    accountNumber.getCurrency().equalsIgnoreCase(currencyType)) {
                return accountNumber.getAccountNumber();
            }
        }

        return null;
    }

    private void adjustTargetAccountNumber(FundsTransferItem fundsTransferItem, List<AccountNumber> accounts)
            throws ApiException {
        String transferAccountType = fundsTransferItem.getTarget().getAccountType();
        String transferCurrencyType = fundsTransferItem.getCurrencyType();
        String accountNumber = findRelatedAccountNumber(transferAccountType, transferCurrencyType, accounts);

        if (accountNumber == null) {
            String errorMessage = String.format("TransfersFacade:adjustTargetAccountNumber - [%s] [%s] [$s] [%s]",
                    "Related account not found for the parameters:", transferAccountType, transferCurrencyType,
                    StringUtils.join(accounts, ","));
            throw new ApiException(ExceptionUtil.buildServerErrorStatus(), errorMessage);
        }

        fundsTransferItem.getTarget().setAccountNumber(accountNumber);
    }

    private boolean isTransferCurrencyAvailableForClient(FundsTransferItem transfer,
                                                         WealthClientMasterInfo wealthClientMasterInfo) {
        // CAD is always available for a transfer request
        if (transfer.getCurrencyType().equals(CAD)) {
            return true;
        }

        // Now we're handling the cases where the transfer currency type is USD or BOTH
        String accountType = transfer.getTarget().getAccountType();
        String clientID = transfer.getTarget().getAccountNumber().substring(0, 6);
        List<AccountNumber> accounts = wealthClientMasterInfo.getAboutyou().getAccountDetails().getAccounts();

        // Checking across all client's accounts if they contain, for the same account type and same
        // client ID of the transfer request, an USD account
        for (AccountNumber account : accounts) {
            if (account.getAccountType().equalsIgnoreCase(accountType) &&
                    account.getAccountNumber().startsWith(clientID) &&
                    account.getCurrency().equals(USD)) {
                return true;
            }
        }

        return false;
    }

    public Institution retrieveInstitutionById(String id) {
        return transfersHelper.retrieveInstitutionById(id);
    }

    public boolean validate(FundsTransfer fundsTransferRequest, WealthClientMasterInfo wealthClientMasterInfo) {
        if (fundsTransferRequest == null) {
            LOGGER.debug("Transfer Validation: Funds Transfer object is invalid");
            return false;
        }

        if (wealthClientMasterInfo == null) {
            LOGGER.debug("Transfer Validation: Wealth Client Master Info is invalid");
            return false;
        }

        String svgSignature = fundsTransferRequest.getSvgSignature();
        if (svgSignature == null || svgSignature.isEmpty()) {
            LOGGER.debug("Transfer Validation: Signature is empty.");
            return false;
        }

        List<FundsTransferItem> fundsTransferItems = fundsTransferRequest.getFundsTransferItems();
        if (fundsTransferItems == null || fundsTransferItems.isEmpty()) {
            LOGGER.debug("Transfer Validation: Funds Transfer List is empty");
            return false;
        }

        for (FundsTransferItem transfer : fundsTransferItems) {
            if (transfer.getTransferType().equalsIgnoreCase("cash")) {
                LOGGER.debug("Transfer Validation: Cash transfer should not be submitted to the backend");
                return false;
            }

            if (!isTransferCurrencyAvailableForClient(transfer, wealthClientMasterInfo)) {
                LOGGER.debug("Transfer Validation: Client's jurisdiction does not allow this currency.");
                return false;
            }

            TransferAccountDetails targetAccountDetails = transfer.getTarget();
            if (targetAccountDetails == null || targetAccountDetails.getAccountNumber() == null ||
                    targetAccountDetails.getAccountNumber().isEmpty()) {
                LOGGER.debug("Transfer Validation: Target account details are invalid");
                return false;
            }

            TransferAccountDetails sourceAccountDetails = transfer.getSource();
            if (sourceAccountDetails == null || sourceAccountDetails.getAccountNumber() == null ||
                    sourceAccountDetails.getAccountNumber().isEmpty()) {
                LOGGER.debug("Transfer Validation: Source account details are invalid");
                return false;
            }
        }

        return true;
    }

    public void setTargetInstitution(FundsTransfer fundsTransfer) {
        Institution tdCanadaTrust = retrieveInstitutionById(TD_INSTITUTION_ID);

        for (FundsTransferItem fundsTransferItem : fundsTransfer.getFundsTransferItems()) {
            fundsTransferItem.getTarget().setFinancialInstitution(tdCanadaTrust);
        }
    }

    public byte[] retrievePDF(String documentId, MetaData metaData, HttpHeaders httpHeaders) throws ApiException {
        try {
            return transferHandler.retrievePDF(documentId, metaData, httpHeaders);
        } catch (Exception e) {
            LOGGER.error("Submit failed", e);

            throw new ApiException(ExceptionUtil.buildServerErrorStatus());
        }
    }

    public void separateTransfersInBothCurrencies(FundsTransfer fundsTransfer) throws ApiException {
        ListIterator<FundsTransferItem> iterator = fundsTransfer.getFundsTransferItems().listIterator();

        while (iterator.hasNext()) {
            FundsTransferItem fundsTransferItem = iterator.next();

            // if currency is set to BOTH, two transfers to happen. One to CAD account another to USD account.
            if (fundsTransferItem.getCurrencyType().equalsIgnoreCase(BOTH)) {

                // change one to CAD
                fundsTransferItem.setCurrencyType(CAD);

                // add additional transfer for USD
                FundsTransferItem anotherFundsTransferItem = makeCopy(fundsTransferItem);
                anotherFundsTransferItem.setCurrencyType(USD);

                iterator.add(anotherFundsTransferItem);
            }
        }
    }

    public void adjustTargetAccountNumbers(FundsTransfer fundsTransfer, WealthClientMasterInfo wealthClientMasterInfo)
            throws ApiException {
        List<AccountNumber> accountNumbers = wealthClientMasterInfo.getAboutyou().getAccountDetails().getAccounts();
        for (FundsTransferItem fundsTransferItem : fundsTransfer.getFundsTransferItems()) {
            adjustTargetAccountNumber(fundsTransferItem, accountNumbers);
        }
    }

    public List<FundsTransferItem> submit(HttpHeaders httpHeaders, FundsTransfer fundsTransfer)
            throws ApiException {
        try {
            return transferHandler.submit(httpHeaders, fundsTransfer);
        } catch (Exception e) {
            LOGGER.error("Submit failed", e);

            throw new ApiException(ExceptionUtil.buildServerErrorStatus());
        }
    }

    public void ensureDocumentIDBelongsToFundsTransfer(String documentIdWithoutCurlyBraces,
                                                       FundsTransfer fundsTransfer) throws ApiException {
        if (fundsTransfer == null || fundsTransfer.getFundsTransferItems() == null) {
            LOGGER.error("TransfersFacade:retrievePDF - There isn't a Funds Transfer process for this application.");
            throw new ApiException(ExceptionUtil.buildServerErrorStatus());
        }

        boolean isDocumentIdPartOfCurrentTransferProcess = false;
        for (FundsTransferItem fundsTransferItem : fundsTransfer.getFundsTransferItems()) {
            if (fundsTransferItem.getDocumentId().equalsIgnoreCase(documentIdWithoutCurlyBraces)) {
                isDocumentIdPartOfCurrentTransferProcess = true;
                break;
            }
        }

        if (!isDocumentIdPartOfCurrentTransferProcess) {
            LOGGER.error("TransfersFacade:retrievePDF - The document ID [{}] isn't part of the Funds Transfer process",
                    documentIdWithoutCurlyBraces);
            throw new ApiException(ExceptionUtil.buildServerErrorStatus());
        }
    }
}
