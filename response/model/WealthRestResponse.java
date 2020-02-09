package com.td.dcts.eso.response.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.td.dcts.eso.experience.model.response.WealthClientMasterInfo;
import com.td.eso.rest.response.model.LookupModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WealthRestResponse {

    private WealthClientMasterInfo wealthClientMasterInfo;

    public WealthClientMasterInfo getWealthClientMasterInfo() {
        return wealthClientMasterInfo;
    }

    public void setWealthClientMasterInfo(WealthClientMasterInfo wealthClientMasterInfo) {
        this.wealthClientMasterInfo = wealthClientMasterInfo;
    }

    private Dropdowns dropdowns;


    public Dropdowns getDropdowns() {
        return dropdowns;
    }

    public void setDropdowns(Dropdowns dropdowns) {
        this.dropdowns = dropdowns;
    }

    public static class Dropdowns {

        private List<LookupModel> title = new ArrayList<LookupModel>();
        private List<LookupModel> citizenship = new ArrayList<LookupModel>();
        private List<LookupModel> maritalStatus = new ArrayList<LookupModel>();
        private List<LookupModel> suffix = new ArrayList<LookupModel>();
        private List<LookupModel> industry = new ArrayList<LookupModel>();
        private List<LookupModel> occupations = new ArrayList<LookupModel>();
        private List<LookupModel> spouseOccupations = new ArrayList<LookupModel>();
        private List<LookupModel> employmentStatus = new ArrayList<LookupModel>();
        private List<LookupModel> provinces = new ArrayList<LookupModel>();
        private List<LookupModel> canadianProvinces = new ArrayList<LookupModel>();
        private List<LookupModel> usaStates = new ArrayList<LookupModel>();
        private List<LookupModel> phoneTypes = new ArrayList<LookupModel>();
        private List<LookupModel> unitTypes = new ArrayList<LookupModel>();
        private List<LookupModel> phoneCountryCode = new ArrayList<LookupModel>();

        public List<LookupModel> getphoneTypes() {
            return phoneTypes;
        }
        public List<LookupModel> getPhoneCountryCode() {
            return phoneCountryCode;
        }

        public List<LookupModel> getTitle() {
            return title;
        }

        public List<LookupModel> getCitizenship() {
            return citizenship;
        }

        public List<LookupModel> getMaritalStatus() {
            return maritalStatus;
        }

        public List<LookupModel> getSuffix() {
            return suffix;
        }

        public void setTitle(List<LookupModel> title) {
            this.title = title;
        }
        public void setPhoneCountryCode(List<LookupModel> phoneCountryCode) {
            this.phoneCountryCode = phoneCountryCode;
        }

        public void setPhoneTypes(List<LookupModel> phoneTypes) {
            this.phoneTypes = phoneTypes;
        }

        public void setCitizenship(List<LookupModel> citizenship) {
            this.citizenship = citizenship;
        }

        public void setMaritalStatus(List<LookupModel> maritalStatus) {
            this.maritalStatus = maritalStatus;
        }

        public void setSuffix(List<LookupModel> suffix) {
            this.suffix = suffix;
        }

        public List<LookupModel> getIndustry() {
            return industry;
        }

        public void setIndustry(List<LookupModel> industry) {
            this.industry = industry;
        }

        public List<LookupModel> getOccupations() {
            return occupations;
        }

        public void setOccupations(List<LookupModel> occupations) {
            this.occupations = occupations;
        }

        public List<LookupModel> getEmploymentStatus() {
            return employmentStatus;
        }

        public void setEmploymentStatus(List<LookupModel> employmentStatus) {
            this.employmentStatus = employmentStatus;
        }

        public List<LookupModel> getProvinces() {
            return provinces;
        }

        public void setProvinces(List<LookupModel> provinces) {
            this.provinces = provinces;
        }

        public List<LookupModel> getUnitTypes() {
            return unitTypes;
        }

        public void setUnitTypes(List<LookupModel> unitTypes) {
            this.unitTypes = unitTypes;
        }

        public List<LookupModel> getCanadianProvinces() {
            return canadianProvinces;
        }

        public void setCanadianProvinces(List<LookupModel> canadianProvinces) {
            this.canadianProvinces = canadianProvinces;
        }

        public List<LookupModel> getUsaStates() {
            return usaStates;
        }

        public void setUsaStates(List<LookupModel> usaStates) {
            this.usaStates = usaStates;
        }

        public List<LookupModel> getSpouseOccupations() {
            return spouseOccupations;
        }

        public void setSpouseOccupations(List<LookupModel> spouseOccupations) {
            this.spouseOccupations = spouseOccupations;
        }
    }
}
