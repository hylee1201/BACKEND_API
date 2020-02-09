package com.td.dcts.eso.experience.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.td.coreapi.common.status.AdditionalStatus;
import com.td.dcts.eso.experience.model.response.RestFormatter;
import com.td.dcts.eso.experience.model.response.SeverityLevel;

import java.util.List;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "ServerStatusCode", "Severity", "StatusCode", "StatusDesc", "AdditonalStatus" })
public class Status {

    // Private Members
    private String serverStatusCode;
    private SeverityLevel severity;
    private Long statusCode;
    private String statusDesc;
    private List<AdditionalStatus> additionalStatus;

    public Status() {

    }

    public Status(String statusCode, String message) {
        this.statusCode = null;
        statusDesc = message;
    }

    // Getters and Setters
    public List<AdditionalStatus> getAdditionalStatus() {
        return additionalStatus;
    }

    public String getServerStatusCode() {
        return serverStatusCode;
    }

    public String getSeverity() {
        return RestFormatter.mapSeverity(severity);
    }

    @JsonIgnore
    public SeverityLevel getSeverityLevel() {
        return severity;
    }

    public Long getStatusCode() {
        return statusCode;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setAdditionalStatus(List<AdditionalStatus> AdditionalStatus) {
        additionalStatus = AdditionalStatus;
    }

    public void setServerStatusCode(String ServerStatusCode) {
        serverStatusCode = ServerStatusCode;
    }

    public void setSeverity(SeverityLevel severity) {
        this.severity = severity;
    }

    public void setStatusCode(Long statusCode) {
        this.statusCode = statusCode;
    }

    public void setStatusDesc(String StatusDesc) {
        statusDesc = StatusDesc;
    }

}
