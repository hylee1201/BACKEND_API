package com.td.dcts.eso.experience.constants;

import java.util.Arrays;
import java.util.List;

public class ExperienceConstants {

  public static final String FORWARD_SLASH = "/";
  public static final String ENV_FILE_NAME = "environment";
  public static final String STATUS_DESC = "Successfully adjudicated.";
  public static final String CAA = "CAA";
  public static final String VALIDATE_APPLICATION_URL = "resturl.application.uifields.validate";
  public static final String SUBMIT_ADJUDICATE_APPLICATION_URL = "resturl.application.management.submitApplication.adjudicate";
  public static final String APPLICATION_MANAGEMENT_ADJUDICATE_DECISION_URL = "resturl.application.management.submitApplication.checkAdjudicationDecision";
  public static final String ENROLL_AND_RESERVE_URL = "resturl.enroll.reserve";
  public static final String FINALIZE_EW_URL = "resturl.finalize.easyweb";
  public static final String N2B = "N2B";
  public static final String STUDENT = "STUDENT";
  public static final String ESTATE = "ESTATE";
  public static final String INDIVIDUAL = "INDIVIDUAL";
  public static final String OTHER = "OTHER";
  public static final String SPOUSE = "SPOUSE";

  public static final String ESIGN_PREPARE_SIGN_URL_FROM_REVIEW_PAGE = "Prepare_Sign_URL";
  public static final String ESIGN_USE_PACKAGE_IF_PREVIOUSLY_GENERATED= "Use_Previously_Generated_Package";
  public static final String ESIGN_FORCE_SIGN_URL_ON_ESIGN_PAGE = "Force_Generate_Sign_URL";

  public static final String EW = "EW";
  public static final String N2W = "N2W";
  public static final String CAPTURE_CONSENT_URL = "resturl.application.management.capture.consent";
  public static final String RETRIEVE_CONSENT_URL = "resturl.application.management.capture.retrieve";
  public static final String THREAT_METRIX_TAGS_URL = "url.threat.metrix.tags";

  public static final String CREATE_APPLICATION_EVENT_URL = "resturl.application.event.create";

  public static final String RETRIEVE_PRODUCT_TYPE_URL = "resturl.application.management.productType.retrieve";

  public static final String VALET_SERVICE_URL = "valetservice.url";
  public static final String VALET_SERVICE_TIMEOUT = "valetservice.timeout";

  public static final String WELCOME_PACKAGE_EN = "di.content.en.welcome.package.path";
  public static final String WELCOME_PACKAGE_FR = "di.content.fr.welcome.package.path";
  public static final String SIGNATURE_URL = "resturl.signature.signurl";
  public static final String SIGNATURE_SEND_TO_FILENET_URL = "resturl.signature.sendToFilenet";
  public static final String COAPPLICANT_SIGNATURE_URL = "resturl.signature.coapplicantsignurl";

  public static final String PRINT_PACKAGE_URL = "resturl.application.management.print.package";
  public static final String REFERRING_AGENT_URL = "resturl.application.management.referring.agent";
  public static final String PRINT_DOCLIST_URL = "resturl.application.management.print.doclist";

  public static final String HTTP_HEADER_CLIENT_IP = "HTTP_CLIENT_IP";
  public static final String HTTP_HEADER_LOCALE = "HTTP_LOCALE";
  public static final String HTTP_HEADER_BEARER = "Bearer "; // keep the space

  public static final String XSS_HTML_ALLOWED_REGEX = "filter.html.acceptRegex";
  public static final String XSS_HTML_BAD_REGEX = "filter.html.badRegex";
  public static final String GETTING_STARTED_STAGE = "get-started";
  public static final String ACKNOWLEDGEMENT_AGREEMENT_STAGE = "ack_agreement";
  public static final String CREDENTIALS_SETUP_STAGE = "credentials_setup";
  public static final String MARGIN_BRANCH_CHECK_STAGE = "branchMarginConsent";
  public static final String N2B_GET_TO_KNOW_YOU_STAGE = "n2b_getToKnowYou";
  public static final String CAPTURE_CONSENT_STAGE = "capture_consent";
  public static final String ABOUT_YOU_STAGE = "about-you";
  public static final String SET_SUPPLEMENT_FLAGS = "setSupplementFlags";
  public static final String GENERAL_STAGE = "all";
  public static final String SSO_LOGIN_STAGE = "sso-login";
  public static final String FINISH_STAGE = "finish";
  public static final String TRANSFERTYPE_CONSENT_STAGE = "transferType";
  public static final String TRANSFERS_TC_CONSENT_STAGE = "transferTC";
  public static final String EA_TC_CONSENT_STAGE = "tcElectronicAccount";
  public static final String DS_TC_CONSENT_STAGE = "tcDigitalSignature";
  public static final String IMPORTANT_CONSENT_STAGE = "importantConsent";
  public static final String BRANCH_TC_CONSENT_STAGE = "branchTcConsentInfo";
  public static final String RETRIEVE_DB_EVENTS_URL = "resturl.db.events.retrieve";
  public static final String SUBMITAPP_APP_RETRIEVE_PDF = "resturl.completeApp.retrievePDF";
  public static final String SUBMITAPP_APP_SUBMIT = "resturl.completeApp.mark.complete";

  public static final String RETRIEVE_OCCUPATIONS_BY_INDUSTRY = "occupationsByIndustry";
  public static final String RETRIEVE_PROV_STATE_BY_COUNTRY = "provStateByCountry";



  public static final String REFERENCE_DATA_LOOKUP_CACHE = "refDatalookupCache";
  public static final String RESTRICTED_CONNECTID_CACHE = "restrictedConnectIdCache";
  public static final String DISCLOSURE_DOCUMENT_CACHE = "disclosureDocumentCache";

  public static final String REFERENCE_DATA_OCCUPATION_BY_INDUSTRY_CACHE = "refDataOccupationByIndustryCache";
  public static final String PROMO_CODE_CACHE = "promoCodeCache";

  public static final String PARTY_ID_KEY = "partyId";
  public static final String SESSION_ID_KEY = "sessionId";
  public static final String SESSION_KEY_ESIGN_PACKAGE = "eSignPackage";
  public static final String SESSION_KEY_ESIGN_PACKAGE_ALREADY_ATTEMPTED= "eSignPackageAlreadyAttempted";
  public static final String E_SIGN_ATTEMPT_ONCE = "once";
  public static final String E_SIGN_ATTEMPT_MORE_THAN_ONCE = "more_than_once";

  public static final String SESSION_KEY_PRINT_DOC_LIST = "printDocList";
  public static final String CREDIT_REFRENCE_NUMBER_KEY = "creditRefNumber";

  public static final String DATE_FORMAT = "yyyy-MM-dd";

  public static final String Y = "Y";
  public static final String N = "N";

  public static final String EN_CA = "en_CA";
  public static final String FR_CA = "fr_CA";
  public static final String CHANNEL_SELF_SERVE_WEB = "web";
  public static final String CHANNEL_PHONE = "Phone";
  public static final String CHANNEL_BRANCH = "Branch";
  public static final String MOBILE = "MOBILE";


  public static final int MAX_CREATE_IM_NAME_LENGTH = 19;

  public static final String EVENT_TYPE_RETRIEVE_DISCLOSURE = "RETRIEVE_DISCLOSURE";
  public static final String EVENT_TYPE_SWITCH_ACCOUNT = "SWITCH_ACCOUNT";
  public static final String EVENT_TYPE_SIGN_CAPTURE = "SIGN_CAPTURE";
  public static final String EVENT_STATUS_SUCCESS = "SUCCESS";
  public static final String DEFAULT_BUSINESS_OUTCOME = "-1";
  public static final String EVENT_STATUS_FAIL = "FAIL";
  public static final String EVENT_TYPE_LOGIN = "CUSTOMER_LOGIN";
  public static final String EVENT_TYPE_EMPLOYEE_SSO_LOGIN = "EMPLOYEE_SSO";
  public static final String EVENT_TYPE_DEVICE_HANDOFF = "DEVICE_HANDOFF";
  public static final String EVENT_BUSINESS_OUTCOME_AGENT_TO_CUST_HANDOFF = "AGENT_TO_CUST";
  public static final String EVENT_BUSINESS_OUTCOME_CUST_TO_AGENT_HANDOFF = "CUST_TO_AGENT";
  public static final String EVENT_TYPE_DEVICE_ACCEPT = "DEVICE_ACCEPT";
  public static final String EVENT_BUSINESS_OUTCOME_AGENT_ACCEPT = "AGENT_ACCEPT";
  public static final String EVENT_BUSINESS_OUTCOME_CUST_ACCEPT = "CUST_ACCEPT";


  // UAP
  public static final String CONSUMER_ID = "consumerId";
  public static final String RESPONSE_TYPE = "responseType";
  public static final String IDP_ADAPTER = "idpAdapter";
  public static final String CONSUMER_APP_ID = "tsnConsumerAppId";
  public static final String REDIRECT_URI = "redirect_uri";
  public static final String REDIRECT_URI_FR = "redirect_uri_fr";
  public static final String UAP_DOMAIN = "uapDomain";
  public static final String INITIAL_UAP_COOKIE_DOMAIN = "initialUAPCookieDomain";


  // page content constants used in wcm content implementation.

  public static final String CONTENT_ENG_JSON_PATH = "di.content.en.json.path";
  public static final String CONTENT_FRE_JSON_PATH = "di.content.fr.json.path";


  public static final String FORMAT_WCM_FILE_NAME_JSON = "%s_%s.json";
  public static final String CONTENT_FOR_ALL_PAGES = "all";
  public static final String FILENAME_WCM_CONFIG = "WcmConfig";
  public static final String FILENAME_PROMO_CODE = "PromoCode";

  public static final String DEFAULT_ENCODING = "UTF-8";
  public static final String PRODUCT_MARGIN = "MARGIN";
  public static final String PRODUCT_USTP = "USTP";
  public static final String PRODUCT_CASH = "CASH";
  public static final String SYSTEM_TYPE_WD = "WD";
  public static final String SYSTEM_TYPE_C3 = "C3";
  public static final String EVENT_TYPE_ORGANIZATION_RETRIEVE = "ASSO_ORG_RETRVIEVE";
  public static final String EVENT_TYPE_ASSOCIATE_RETRIEVE = "ASSOCIATE_RETRIEVE";
  public static final String SAML_OAUTH_TOKEN = "SAMLOAuthToken";
  public static final String SERVER_ERROR_STATUS_CODE = "500";
  public static final String SAML_DROP_DOWN_CALL_ONCE = "NewSessionSamlDropdown";
  public static final String SAML_RESPONSE_TAG_NAME = "SAMLResponse";
  public static final String PAYLOAD_TAG_NAME = "payload";
  public static final String C3_EXTERNAL_APP_COOKIE = "ExternalApp_Message";
  public static final String C3_EXTERNAL_APP_MESSAGE_EN = "All previously entered information will be lost. Are you sure you want to exit?";
  public static final String C3_EXTERNAL_APP_MESSAGE_FR = "Toutes les données entrées seront perdues. Voulez vous vraiment quitter?";

  public static final String TFSA = "TFSA";
  public static final String RRSP = "RRSP";
  public static final String CASH = "CASH";
  public static final String MARGIN = "MARGIN";
  public static final String MARGIN_LONG = "MARGIN_LONG";
  public static final String MARGIN_SHORT = "MARGIN_SHORT";
  public static final String SRSP = "SRSP";
  public static final String LIRA = "LIRA";
  public static final String LRSP = "LRSP";
  public static final String RLSP = "RLSP";
  public static final String RRIF = "RRIF";
  public static final String SRIF = "SRIF";
  public static final String LIF = "LIF";
  public static final String LRIF = "LRIF";
  public static final String RLIF = "RLIF";
  public static final String PRIF = "PRIF";
  public static final String RESP = "RESP";
  public static final String RDSP = "RDSP";
  public static final String ITF = "ITF";
  public static final String INFORMAL_TRUST = "INFORMAL_TRUST";
  public static final String SWEEP = "INCOME_SWEEP";
  public static final String USTP = "USTP";
  public static final String USTP_SHORT = "USTP_SHORT";
  public static final List<String> RIF_GROUP = Arrays.asList(RRIF,SRIF,LIF,LRIF,PRIF,RLIF);
  public static final List<String> RSP_GROUP = Arrays.asList(RRSP,SRSP,LIRA,RLSP,LRSP);
  public static final List<String> CASH_GROUP = Arrays.asList(CASH, MARGIN, MARGIN_SHORT);
  public static final List<String> USTP_GROUP = Arrays.asList(USTP, USTP_SHORT);
}
