package com.td.dcts.eso.experience.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;


public class EligibilityStatus {

    private Boolean selectionEligible;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer creditScore;

    private List<String> failedEligibilityRules;

    public Boolean getSelectionEligible() {
        return selectionEligible;
    }

    public void setSelectionEligible(Boolean selectionEligible) {
        this.selectionEligible = selectionEligible;
    }

    public List<String> getFailedEligibilityRules() {
        return failedEligibilityRules;
    }

    public void setFailedEligibilityRules(List<String> failedEligibilityRules) {
        this.failedEligibilityRules = failedEligibilityRules;
    }

    public Integer getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(Integer creditScore) {
        this.creditScore = creditScore;
    }
}
