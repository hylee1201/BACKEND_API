package com.td.dcts.eso.response.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.td.dcts.eso.experience.model.response.WealthClientMasterInfo;
import com.td.eso.rest.response.model.LookupModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountDetailsRestResponse {
 
	    private String accountNumber;
	 

		public String getAccountNumber() {
			return accountNumber;
		}

		public void setAccountNumber(String accountNumber) {
			this.accountNumber = accountNumber;
		}
		
}
