package com.td.dcts.eso.experience.util;


import com.google.common.base.Strings;
import com.td.dcts.eso.experience.model.response.AboutYou;
import com.td.dcts.eso.experience.model.response.InvestmentInfo;
import com.td.dcts.eso.experience.model.response.Pro;
import com.td.dcts.eso.experience.model.response.WealthClientMasterInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.td.dcts.eso.experience.constants.ExperienceConstants.HTTP_HEADER_BEARER;

public class CommonUtil {

  public static int findAge(Calendar dateOfBirth,Calendar now) {


    int currentYear = now.get(Calendar.YEAR);
    int birthYear = dateOfBirth.get(Calendar.YEAR);

    int age = currentYear - birthYear;

    int currentMonth = now.get(Calendar.MONTH);
    int birthMonth = dateOfBirth.get(Calendar.MONTH);

    if (birthMonth > currentMonth) {
      age--;
    } else if (currentMonth == birthMonth) {
      int today = now.get(Calendar.DAY_OF_MONTH);
      int birthDay = dateOfBirth.get(Calendar.DAY_OF_MONTH);
      if (birthDay > today) {
        age--;
      }
    }

    return age;

  }

  public static boolean isN2B(WealthClientMasterInfo wcm) {
	  return wcm == null || wcm.getCmClientID() == null || wcm.getCmClientID().isEmpty(); // TODO
  }

  public static WealthClientMasterInfo updateAllApplicationArray(WealthClientMasterInfo draftWcm){
    if(draftWcm.getAboutAllApplicantsAndParties() !=null && draftWcm.getAboutAllApplicantsAndParties().size()>0) {
      for (int i = 0; i < draftWcm.getAboutAllApplicantsAndParties().size(); i++) {
        if (draftWcm.getAboutyou() != null) {
          if (draftWcm.getAboutyou().getProfileId().equals(draftWcm.getAboutAllApplicantsAndParties().get(i).getProfileId())) {
            draftWcm.getAboutAllApplicantsAndParties().set(i, draftWcm.getAboutyou());
            break;
          }
        }
      }
    }
    return draftWcm;
  }
  public static void updateInfoFromESignPackage(WealthClientMasterInfo wcmSavedForeSignPackage, WealthClientMasterInfo wcm) {
    if((wcmSavedForeSignPackage != null && wcmSavedForeSignPackage.getAboutyou() != null &&
      wcmSavedForeSignPackage.getAboutyou().getAccountDetails() != null &&
      wcmSavedForeSignPackage.getAboutyou().getAccountDetails().getAccounts() != null &&
      wcmSavedForeSignPackage.getAboutyou().getAccountDetails().getAccounts().size() > 0 ) &&
      ((wcm.getAboutyou()!=null) && (wcm.getAboutyou().getAccountDetails()!=null)
        && (wcm.getAboutyou().getAccountDetails().getAccounts() == null) || (wcm.getAboutyou().getAccountDetails().getAccounts().size() == 0)))
    {
      //The condition below is added to make sure that we use the SavedSignPackage only if the
      //final wcm package does not have an eSign package.  This condition will make sure that
      //if two packages are generated then only the latest package is consumed by Submit Application
      if(wcm.getApplicationInfo() != null && wcm.getApplicationInfo().getSignedDocumentPackageId()==null) {
        wcm.getAboutyou().getAccountDetails().setAccounts(wcmSavedForeSignPackage.getAboutyou().getAccountDetails().getAccounts());

        //set the print doc list here
        wcm.getApplicationInfo().setApplicationPackage(wcmSavedForeSignPackage.getApplicationInfo().getApplicationPackage());

        // Update The About All Parties Array
        updateAllApplicationArray(wcm);
      }
    }
  }

  public static String getLocale(HttpServletRequest httpServletRequest) {
		String locale = Locale.CANADA.toString();
		Locale l = httpServletRequest.getLocale();
		if (l != null) {
			if (l.getLanguage().equals(Locale.FRENCH)) {
				locale = Locale.CANADA_FRENCH.toString();
			} else {
				// use default locale: en_CA
			}
		}
		return locale;
	}

  //This logic is copied over from ApplicationDataFacade that is unchanged here
  public static String getCMClientType(InvestmentInfo investmentInfo) {
    String cmClientTyp = "NONE";
    if(investmentInfo != null) {
      List<Pro> pros = investmentInfo.getKYCPro();
      if(pros != null) {
        for(Pro pro : pros) {
          if(pro.getWhoIsPRO() != null) {
//                    if (pro.getWhoIsPRO().equals(relSelf)) {
            cmClientTyp = "PROI";
//                  }
          }
        }
      }
    }

    return cmClientTyp;
  }

  public static AboutYou getProfile(WealthClientMasterInfo wcm, String profileId) {
    AboutYou aboutYou = null;

    if (wcm == null || wcm.getAboutAllApplicantsAndParties() == null) {
      return null;
    }

    for (AboutYou one : wcm.getAboutAllApplicantsAndParties()) {
      if (profileId.equalsIgnoreCase(one.getProfileId())) {
        aboutYou = one;
      }
    }

    return aboutYou;
  }

  public static WealthClientMasterInfo resetWCM(WealthClientMasterInfo wcm, String profileId) {
    if (wcm.getAboutyou()!=null)
    {
      if((profileId.equals(wcm.getAboutyou().getProfileId()) != true )){
        //Get the correct profile and return
        for (AboutYou aboutApplicantAndParty: wcm.getAboutAllApplicantsAndParties()) {
          if((profileId.equals(aboutApplicantAndParty.getProfileId()) == true )){
            wcm.setAboutyou(aboutApplicantAndParty);
            break;
          }
        }
      }
    }
    return wcm;
  }

  public static String addBearerToAuthToken(String authToken) {
    if (Strings.isNullOrEmpty(authToken)) {
      return HTTP_HEADER_BEARER;
    }

    if (authToken.startsWith(HTTP_HEADER_BEARER)) {
      return authToken;
    } else {
      return HTTP_HEADER_BEARER + authToken;
    }
  }
}
