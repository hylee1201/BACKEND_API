package com.td.dcts.eso.experience.facade;

import com.jayway.jsonpath.InvalidJsonException;
import com.td.coreapi.common.config.ApiConfig;
import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.constants.ExperienceConstants;
import com.td.dcts.eso.experience.handler.ContentHandler;
import com.td.dcts.eso.experience.model.response.LocaleAndOverrideMap;
import com.td.dcts.eso.experience.model.response.Product;
import com.td.dcts.eso.experience.model.response.WealthClientMasterInfo;
import com.td.dcts.eso.experience.util.CommonUtil;
import com.td.dcts.eso.experience.util.ExceptionUtil;
import com.td.dcts.eso.experience.util.SessionUtil;
import com.td.dcts.eso.experience.util.WcmContentUtil;
import com.td.dcts.eso.external.clientmaster.Account;
import com.td.dcts.eso.external.clientmaster.Agreement;
import com.td.dcts.eso.session.model.EsoJsonData;
import com.td.dcts.eso.wcm.content.config.*;
import com.td.dcts.eso.wcm.content.exception.WcmException;
import com.td.dcts.eso.wcm.content.service.JsonContentService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.*;

import static com.td.dcts.eso.experience.util.SessionUtil.SESSION_KEY_CHANNEL;

@Component
public class ContentFacade {

  private static final XLogger LOGGER = XLoggerFactory.getXLogger(ContentFacade.class);

  private static final String MARGIN = "MARGIN";
  private static final String MARGIN_US_SUFFIX = "F";
  private static final String MARGIN_CA_SUFFIX = "E";
  private static final String ACTIVE = "ACTIVE";


  @Autowired
  private ContentHandler contentHandler;


  private static final String RRSP = "RRSP";

  @Autowired
  private JsonContentService jsonContentService;

  @Autowired
  private SessionUtil sessionUtil;

  /**
   * Get json content from wcm files.
   *
   * @param httpServletRequest
   * @param pageName
   * @return
   * @throws ApiException
   */

  public Map<String, Object> getPageContent(HttpServletRequest httpServletRequest, String pageName)
    throws ApiException {
    try {
      LocaleAndOverrideMap param = getLocaleAndOverrideMapFromSession(httpServletRequest);
      return getPageContent(param.getLocale(), param.getOverrideMap(), pageName);
    } catch(InvalidJsonException e) {
      LOGGER.error(e.getMessage(), e);
      throw new ApiException(ExceptionUtil.buildErrorStatus(500, e.getMessage()));
    }
  }

  /**
   * Get json content from wcm files.
   *
   * @param locale
   * @param overrideMap
   * @param pageName
   * @return
   * @throws ApiException
   */
  public Map<String, Object> getPageContent(Locale locale, Map<String, String> overrideMap, String pageName)
    throws ApiException {
    try {
      validateParams(pageName);

      // get absolute file path of json files in wcm json content folder
      // for locale (en_CA or fr_CA).
      List<String> filePathList = getJsonFilePathList(locale, pageName);

      return jsonContentService.getContent(filePathList, overrideMap);
    } catch(WcmException e) {
      LOGGER.error(e.getMessage(), e);
      throw new ApiException(ExceptionUtil.buildErrorStatus(500, e.getMessage()), e.getMessage());
    }
  }

  /**
   * Parse string value to {@link Locale} object.
   *
   * @param locale
   * @return
   */
  public Locale parseStringToLocale(String locale) {
    if(locale != null && locale.length() == 5) {
      return new Locale(locale.substring(0, 2), locale.substring(3, 5));
    }
    return Locale.CANADA;
  }

  private List<String> getJsonFilePathList(Locale locale, String pageName) {
    List<String> pageNameList = Arrays.asList(
      "aboutyou",
      "accountdetails",
      "acknowledgeagree",
      "acknowledgementagreement",
      "all",
      "credentialsetup",
      "datepicker",
      "employmentinfo",
      "financialinfo",
      "finishingup",
      "formwarning",
      "funding",
      "gettingstarted",
      "idcapture",
      "idproofing",
      "ineligible",
      "initialstate",
      "investmentinfo",
      "login",
      "popup",
      "producteligibility",
      "productselector",
      "review",
      "td-address",
      "td-footer",
      "td-header",
      "utils"
    );

    if(!pageNameList.contains(pageName) || !(locale.toString().equals("en_CA") || locale.toString().equals("fr_CA")))
      return null;

    List<String> filePathList;

    // set property key for locale.
    String propertyKey = Locale.CANADA_FRENCH.equals(locale) ? ExperienceConstants.CONTENT_FRE_JSON_PATH : ExperienceConstants.CONTENT_ENG_JSON_PATH;

    // read folder path.
    String folderPath = ApiConfig.getInstance().getPropertiesConfig(ExperienceConstants.ENV_FILE_NAME).getProperty(propertyKey);

    // read content for all pages.
    if(ExperienceConstants.CONTENT_FOR_ALL_PAGES.equalsIgnoreCase(pageName)) {
      File contentFolder = Paths.get(folderPath).toFile();
      File[] contentFiles = contentFolder.listFiles();

      filePathList = new ArrayList<>(contentFiles.length);

      for(File file : contentFiles) {
        filePathList.add(file.getAbsolutePath());
      }
      return filePathList;
    }
    // read content for one given page.
    else {
      String fileName = String.format(ExperienceConstants.FORMAT_WCM_FILE_NAME_JSON, pageName, locale.toString());
      String path = Paths.get(folderPath, fileName).toAbsolutePath().toString();

      filePathList = new ArrayList<>(1);
      filePathList.add(path);
    }
    return filePathList;
  }

  /**
   * Return {@link LocaleAndOverrideMap} object based on session data.
   *
   * @param httpServletRequest
   * @return
   * @throws ApiException
   */
  @SuppressWarnings("unchecked")
  public LocaleAndOverrideMap getLocaleAndOverrideMapFromSession(HttpServletRequest httpServletRequest) throws ApiException {
    EsoJsonData esoData = sessionUtil.getSessionData(httpServletRequest);
    String strLocale = (String) esoData.get(SessionUtil.SESSION_KEY_LOCALE);
    String strChannel = (String) esoData.get(SESSION_KEY_CHANNEL);
    Map<String, String> overrideMap;
    Object object = esoData.get(SessionUtil.SESSION_KEY_OVERRIDE_VALUES);
    if(object == null) {
      LOGGER.warn("Override map not found in eso json data, creating default override map.");
      overrideMap = createDefaultOverrideMap(esoData, strChannel);
    } else {
      overrideMap = (Map<String, String>) object;
    }

    return new LocaleAndOverrideMap(parseStringToLocale(strLocale), overrideMap);
  }

  /**
   * Create override map with default values and set in eso json data object.
   *
   * @param esoData
   * @return Map<String, String>
   */
  public Map<String, String> createDefaultOverrideMap(EsoJsonData esoData, String channelCode) {

    Map<String, String> map = new HashMap<>(7);

    WcmConfig wcmConfig = WcmContentUtil.getWcmConfig();

    map.put(SourceOverride.OverrideKey.SOURCE.value(), wcmConfig.getSourceOverride().getOverrideValue().value());

    Object flowId = esoData.get(SessionUtil.SESSION_KEY_FLOW_ID);
    map.put(CustomerOverride.OverrideKey.CUSTOMER.value(), null == flowId ? "" : flowId.toString());

    Object productId = esoData.get(SessionUtil.SESSION_KEY_PRODUCT_ID);
    map.put(ProductDetailsOverride.OverrideKey.PRODUCT_DETAILS.value(), null == productId ? "" : productId.toString());

    map.put(ProductFamilyOverride.OverrideKey.PRODUCT_FAMILY.value(), wcmConfig.getProductFamilyOverride().getOverrideValue().value());

    map.put(ProductGroupOverride.OverrideKey.PRODUCT_GROUP.value(), wcmConfig.getProductGroupOverride().getOverrideValue().value());

    map.put(SegmentOverride.OverrideKey.SEGMENT.value(), channelCode);

    map.put(GlobalOverride.OverrideKey.GLOBAL.value(), null);

    esoData.put(SessionUtil.SESSION_KEY_OVERRIDE_VALUES, map);

    return map;
  }

  /**
   * Update customer type and product id in {@link EsoJsonData} object.
   *
   * @param esoData
   */
  public void updateOverrideMap(EsoJsonData esoData) {

    Object object = esoData.get(SessionUtil.SESSION_KEY_OVERRIDE_VALUES);
    if(null == object) {
      // nothing to update, return.
      return;
    }

    @SuppressWarnings("unchecked")
    Map<String, String> map = (Map<String, String>) object;

    Object flowId = esoData.get(SessionUtil.SESSION_KEY_FLOW_ID);
    map.put(CustomerOverride.OverrideKey.CUSTOMER.value(), null == flowId ? "" : flowId.toString());

    Object productId = esoData.get(SessionUtil.SESSION_KEY_PRODUCT_ID);
    map.put(ProductDetailsOverride.OverrideKey.PRODUCT_DETAILS.value(),
      null == productId ? "" : productId.toString());

    esoData.put(SessionUtil.SESSION_KEY_OVERRIDE_VALUES, map);
  }

  private void validateParams(String pageName) throws ApiException {
    if(null == pageName || pageName.isEmpty()) {
      String message = "Page name is null or empty.";
      throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), message), message);
    }
  }

  public List<Product> getProducts(String locale, WealthClientMasterInfo wealthClientMasterInfo) throws IOException {

    List<Product> products = contentHandler.getAllProducts(locale);

    try {
      //filter products by customer channel
      String customerChannel = wealthClientMasterInfo.getUserIdentity().getChannel();
      if(customerChannel != null) {
        products.removeIf(entry -> !entry.getChannels().contains(customerChannel.toLowerCase()));
      }

      //find the age
      if(wealthClientMasterInfo.getAboutyou().getPersonalInfo().getDOB() != null) {
        Date dob = new SimpleDateFormat("yyyy-MM-dd").parse(wealthClientMasterInfo.getAboutyou().getPersonalInfo().getDOB());
        Calendar dobInCalendar = Calendar.getInstance();
        dobInCalendar.setTime(dob);

        int age = CommonUtil.findAge(dobInCalendar, Calendar.getInstance());

        //if the age is >= 72, filter out RSP
        if(age >= 72) {
          products = filterProduct(RRSP, products);
        }
      }

      boolean marginAllowed = wealthClientMasterInfo.getAgreements() != null ? isMarginAllowed(wealthClientMasterInfo.getAgreements()) : true;

      if(!marginAllowed) {
        products = filterProduct(MARGIN, products);
      }

    } catch(ParseException e) {
      LOGGER.error("Error in converting the DOB to date", e);
    }


    return products;

  }

   public List<Product> getProductsContent(String locale, List<String> productsList) throws IOException {
    List<Product> products = contentHandler.getAllProducts(locale);
    return products;
  }

  private boolean isMarginAllowed(List<Agreement> agreements) {

    int numberOfActiveCAMarginAccounts = 0;
    int numberOfActiveUSMarginAccounts = 0;

    Account anAccount;

    for(Agreement anAgreement : agreements) {
      anAccount = anAgreement.getAccount();

      if(anAccount.getAccountTypeCd().equals(MARGIN)
        && anAccount.getAccountNum().endsWith(MARGIN_US_SUFFIX)
        && anAccount.getStatusCd().equals(ACTIVE)) numberOfActiveUSMarginAccounts++;

      if(anAccount.getAccountTypeCd().equals(MARGIN)
        && anAccount.getAccountNum().endsWith(MARGIN_CA_SUFFIX)
        && anAccount.getStatusCd().equals(ACTIVE)) numberOfActiveCAMarginAccounts++;

      if(numberOfActiveUSMarginAccounts >= 2 || numberOfActiveCAMarginAccounts >= 2) {
        return false;
      }

    }

    return true;
  }

  private List<Product> filterProduct(String productId, List<Product>products) {
    Iterator<Product> iterator = products.iterator();
    while(iterator.hasNext()) {
      Product aProduct = iterator.next();
      if(aProduct.getProductId().equalsIgnoreCase(productId)) {
        iterator.remove();
      }
    }

    return products;
  }

}
