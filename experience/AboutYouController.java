package com.td.dcts.eso.experience;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.client.ApplicationManagementRestClient;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.dao.CustomerDAO;
import com.td.dcts.eso.experience.environmentjs.facade.ReferenceDataEnum;
import com.td.dcts.eso.experience.model.response.*;
import com.td.dcts.eso.experience.util.ExceptionUtil;
import com.td.dcts.eso.experience.util.ValidationHelper;
import com.td.dcts.eso.experience.util.ValidationUtil;
import com.td.dcts.eso.response.model.WealthRestResponse;
import com.td.eso.constants.LookupConstants;
import com.td.eso.constants.WealthConstants;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/aboutYou")
@Controller
public class AboutYouController extends BaseController {

	private static final XLogger LOGGER = XLoggerFactory.getXLogger(AboutYouController.class);

	@Autowired
	private ApplicationManagementRestClient applicationManagementRestClient;

	@Autowired
	private CustomerDAO customerDAO;

	@Autowired
	private ValidationUtil validationUtil;

	@Value("${resturl.about.you.profile.update}")
	private String updateAboutYouURL;

	@Value("${about_you_exclude_country_code}")
	private String[] excludeCountryCodeList;

	private ObjectMapper objectMapper = new ObjectMapper();

	@GET
	@Path("/profile")
	@Produces(MediaType.APPLICATION_JSON)
	public Response retrieveAboutYou(@Context HttpServletRequest httpServletRequest) throws ApiException {
		LOGGER.entry("retrieveAboutYou started");
		WealthRestResponse aboutYouRestResponse = new WealthRestResponse();

		try {
			WealthClientMasterInfo wealthCMInfo = customerDAO.getWealthClientMasterInfo(sessionUtil.getDraftClientProfile(httpServletRequest));
			aboutYouRestResponse.setWealthClientMasterInfo(wealthCMInfo);
		} catch (IOException e) {
			LOGGER.error("Get WealthClientMasterInfo is failed", e);
			throw new ApiException(ExceptionUtil.buildServerErrorStatus());
		} finally {
			LOGGER.exit("retrieveAboutYou finished");
		}
		return Response.ok(aboutYouRestResponse, MediaType.APPLICATION_JSON).build();

	}

	@POST
	@Path("/profile/{eventId}")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateProfile(@Context HttpServletRequest httpServletRequest, @RequestBody WealthClientMasterInfo wcm, @PathParam("eventId") String eventId) throws ApiException {
		LOGGER.entry("updateProfile started");
		MetaData metaData = getMetaData(httpServletRequest, ExperienceConstants.ABOUT_YOU_STAGE);
		HttpHeaders httpHeaders = getHttpHeaders(httpServletRequest);

		try {
			// Validate updated data and merge with saved draft
			LOGGER.debug("Process event {}", eventId);
			WealthClientMasterInfo draftWcm = processProfileUpdateEvent(httpServletRequest, wcm, eventId);

			// Save back to session
			sessionUtil.setDraftClientProfile(httpServletRequest, draftWcm);

			LOGGER.debug("Update about you profile url={}/{}", updateAboutYouURL, eventId);
			ResponseEntity<String> responseEntity = applicationManagementRestClient.getTextResponse(metaData, draftWcm, httpHeaders, updateAboutYouURL + "/" + eventId);
			if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
				String s = responseEntity.getBody();
				LOGGER.debug("Update about you profile resonse={}", s);
				return Response.ok(responseEntity.getBody(), MediaType.TEXT_PLAIN).build();
			} else {
				LOGGER.exit(responseEntity.getStatusCode());
				throw new ApiException(ExceptionUtil.buildErrorStatusFromApiErrorStatus(responseEntity));
			}
		} catch (IOException e) {
			LOGGER.error("Parsing WealthClientMasterInfo JSON data is failed", e);
			throw new ApiException(ExceptionUtil.buildServerErrorStatus());
		} finally {
			LOGGER.exit("updateProfile finished");
		}

	}

	/**
	 * @param httpServletRequest
	 * @param wcm,
	 *            updated profile from client side
	 * @param eventId
	 * @return WealthClientMasterInfo validated and merged draft
	 * @throws ApiException
	 * @throws IOException
	 */
	private WealthClientMasterInfo processProfileUpdateEvent(HttpServletRequest httpServletRequest, WealthClientMasterInfo wcm, String eventId) throws ApiException, IOException {

		AboutYou aboutYou = wcm.getAboutyou();
		if (aboutYou == null) {
			throw new ApiException(ExceptionUtil.buildErrorStatus(400, "Invalid Data, missing About You"));
		}

		WealthClientMasterInfo draftWcm = customerDAO.getWealthClientMasterInfo(sessionUtil.getDraftClientProfile(httpServletRequest));
		AboutYou draftAboutYou = draftWcm.getAboutyou();

		switch (eventId) {
		case WealthConstants.ABOUT_YOU_PERSONAL_INFO:
			processPersonalInfoEvent(draftAboutYou, aboutYou);
			break;
		case WealthConstants.ABOUT_YOU_EMPLOYMENT_INFO:
			processEmploymentInfoEvent(draftAboutYou, aboutYou);
			break;
		case WealthConstants.ABOUT_YOU_CONTACT_INFO:
			processContactInfoEvent(draftAboutYou, aboutYou);
			break;
		}

		return draftWcm;
	}

	private void processPersonalInfoEvent(AboutYou draftAboutYou, AboutYou aboutYou) throws ApiException {
		// Process personalInfo
		PersonalInfo pi = aboutYou.getPersonalInfo();
		if (pi == null) {
			throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), "Personal Info is missing"));
		}

//		ValidationResult vr = validationHelper.validate(pi);
//		if (!vr.isValid()) {
//			throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), vr.toString()));
//		}
		validationUtil.validateDataAgainstDropdown(pi, false);
		draftAboutYou.setPersonalInfo(pi);

		// Process partnerInfo when applicant is married or common law
		if (pi.getMaritalStatus().equals(LookupConstants.MARITAL_STATUS_MARRIED) || pi.getMaritalStatus().equals(LookupConstants.MARITAL_STATUS_COMMON_LAW)) {
			PartnerInfo pti = aboutYou.getPartnerInfo();
			if (pti == null) {
				throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), "Partner Info is missing"));
			}
//			vr = validationHelper.validate(pti, PersonNameCheckGroup.class);// Only validate the name group
//			if (!vr.isValid()) {
//				throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), vr.toString()));
//			}
			if (draftAboutYou.getPartnerInfo() == null) {
				draftAboutYou.setPartnerInfo(new PartnerInfo());
			}
			// Only update Spouse name for personalInfo event, don't update
			// Spouse Employment here
			draftAboutYou.getPartnerInfo().setFirstName(pti.getFirstName());
			draftAboutYou.getPartnerInfo().setMiddleName(pti.getMiddleName());
			draftAboutYou.getPartnerInfo().setLastName(pti.getLastName());
		}
	}

	private void processEmploymentInfoEvent(AboutYou draftAboutYou, AboutYou aboutYou) throws ApiException {
		// Process Applicant Employment Info
		EmploymentInfo applicantEI = aboutYou.getEmploymentInfo();
		if (applicantEI == null) {
			throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), "Employment Info is missing"));
		}

		if (applicantEI.getStatusCd().equals(LookupConstants.EMPLOYMENT_STATUS_STUDENT)) {
			// Student doesn't have Industry and Occupation
			applicantEI.setIndustry(null);
			applicantEI.setOccupation(null);
		}

		if (applicantEI.getStatusCd().equals(LookupConstants.EMPLOYMENT_STATUS_STUDENT) || applicantEI.getStatusCd().equals(LookupConstants.EMPLOYMENT_STATUS_EMPLOYED)
				|| applicantEI.getStatusCd().equals(LookupConstants.EMPLOYMENT_STATUS_SELF_EMPLOYED)) {
			// Applicant must provide address for student, employed or self
			// employed
			Address employerAddress = applicantEI.getAddress();
			if (employerAddress == null) {
				throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), "Employer Address is missing"));
			} else {
				String employerCountry = employerAddress.getCountry();
				if (!employerCountry.equals(LookupConstants.COUNTRY_CODE_CA) && !employerCountry.equals(LookupConstants.COUNTRY_CODE_US)) {
					employerAddress.setProvince(null);
				}

			}
		} else {
			applicantEI.setAddress(null);
		}

//		ValidationResult vr = validationHelper.validate(applicantEI);
//		if (!vr.isValid()) {
//			throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), vr.toString()));
//		}
		validationUtil.validateDataAgainstDropdown(applicantEI, false);
		draftAboutYou.setEmploymentInfo(applicantEI);

		// Process Spouse Employment Info when client is married or common law
		String marritalStatus = draftAboutYou.getPersonalInfo().getMaritalStatus();
		if (marritalStatus.equals(LookupConstants.MARITAL_STATUS_COMMON_LAW) || marritalStatus.equals(LookupConstants.MARITAL_STATUS_MARRIED)) {
			// Married or Common Law must have spouse EI
			PartnerInfo pai = aboutYou.getPartnerInfo();
			if (pai == null) {
				throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), "Partner Info is missing"));
			}
			EmploymentInfo spouseEI = pai.getEmploymentInfo();
			if (spouseEI == null) {
				throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), "Partner Employment Info is missing"));
			}
			// Spouse will never have Employer address regardless of employment
			// status
			spouseEI.setAddress(null);
			if (spouseEI.getStatusCd().equals(LookupConstants.EMPLOYMENT_STATUS_STUDENT)) {
				// Student doesn't have Industry and Occupation
				spouseEI.setIndustry(null);
				spouseEI.setOccupation(null);
			}
//			vr = validationHelper.validate(spouseEI);
//			if (!vr.isValid()) {
//				throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), vr.toString()));
//			}
			validationUtil.validateDataAgainstDropdown(spouseEI, false);
			draftAboutYou.getPartnerInfo().setEmploymentInfo(spouseEI);
		}
	}

	private void processContactInfoEvent(AboutYou draftAboutYou, AboutYou aboutYou) throws ApiException {
		// Process Applicant Employment Info
		ContactInfo ci = aboutYou.getContactInfo();
		if (ci == null) {
			throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), "Contact Info is missing"));
		}
//		ValidationResult vr = validationHelper.validate(ci);
//		if (!vr.isValid()) {
//			throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), vr.toString()));
//		}
		if (!isEmpty(ci.getPhoneType()) && !validationUtil.lookupModelListContainsCode(ci.getPhoneType(), ReferenceDataEnum.PHONETYPES)) {
			throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), "Invalid Phone Type"));
		}
		if (ci.getOtherPhones() != null && ci.getOtherPhones().size() > 0) {
		  for (Phone phone : ci.getOtherPhones()) {
        if (!isEmpty(phone.getPhoneType()) && !validationUtil.lookupModelListContainsCode(phone.getPhoneType(), ReferenceDataEnum.PHONETYPES)) {
          throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), "Invalid Phone Type"));
        }
      }
    }
		validationUtil.validateDataAgainstDropdown(ci.getLegalAddress(), false);
		draftAboutYou.setContactInfo(ci);
	}

}
