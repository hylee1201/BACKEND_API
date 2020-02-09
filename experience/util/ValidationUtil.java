package com.td.dcts.eso.experience.util;

import com.td.coreapi.common.status.ApiException;
import com.td.dcts.eso.experience.environmentjs.facade.ReferenceDataEnum;
import com.td.dcts.eso.experience.environmentjs.facade.ReferenceDataHelper;
import com.td.dcts.eso.experience.handler.ContentHandler;
import com.td.dcts.eso.experience.model.response.*;
import com.td.eso.rest.response.model.LookupModel;
import org.apache.commons.collections.ListUtils;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

import static com.td.dcts.eso.experience.util.SessionUtil.DI_INBOUND_SAML_CODE;

@Component
public class ValidationUtil {
	private static final List<String> SUPPORTED_CHANNELS = Arrays.asList("Easyweb", "Infosite");
	private static final List<String> SUPPORTED_LOCALES = Arrays.asList("en_CA", "fr_CA");
	private static final String MARKETING_CODE_DEFAULT = "none";
	private static final String SOURCE_CODE_DEFAULT = "11111";

	private static final XLogger LOGGER = XLoggerFactory.getXLogger(ValidationUtil.class);

  @Autowired
	private ContentHandler contentHandler;

	@Autowired
  private ReferenceDataHelper referenceDataHelper;

	public Map<String, Object> validateWelcomeParameters(String locale, String syntheticProductId, List<String> productIds, String marketingCode, String sourceCode, String sourceUrl,String clientType,
			HttpServletRequest httpServletRequest, String channelCode) throws ApiException, IOException {

		if (!SUPPORTED_LOCALES.contains(locale)) {
			throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), "Unsupported locale"));
		}
		Map<String, Product> products = contentHandler.getAllProductsMap(locale);
		List<String> l = new ArrayList<String>();
		for (String productId : productIds) {
			if (products.containsKey(productId)) {
				l.add(productId);
			} else {
				LOGGER.debug("invalid productID={}", productId);
			}
		}
		/* allow empty
		if (l.isEmpty()) {
			throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), "No selected product(s)"));
		}
		*/
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("channelId", channelCode);
		result.put("ipAddress", httpServletRequest.getRemoteAddr());
		result.put("locale", locale);
		result.put(SessionUtil.SESSION_KEY_PRODUCT_ID, syntheticProductId); // Synthetic product id
		result.put(SessionUtil.SESSION_KEY_PRODUCTS, l);
		result.put("marketingCode", (marketingCode == null || marketingCode.length() > 100) ? MARKETING_CODE_DEFAULT : marketingCode);
		result.put("sourceCode", (sourceCode == null || sourceCode.length() > 100) ? SOURCE_CODE_DEFAULT : sourceCode);
		result.put("sourceUrl", (sourceUrl == null || sourceUrl.length() > 1000) ? httpServletRequest.getHeader(HttpHeaderNames.REFERER) : sourceUrl);
		return result;
	}

	/**
	 *
	 * @param pi PersonalInfo
	 * @param cleanup boolean, true: clean up invalid data only; false: throw ApiException
	 * @throws ApiException
	 */
	public void validateDataAgainstDropdown(PersonalInfo pi, boolean cleanup) throws ApiException {

		if (!isEmpty(pi.getCitizenship()) && !lookupModelListContainsCode(pi.getCitizenship(), ReferenceDataEnum.COUNTRY)) {
			if (cleanup) {
				LOGGER.warn("Invalid Citizenship:{}", pi.getCitizenship());
				pi.setCitizenship(null);
			} else {
				throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), "Invalid Citizenship"));
			}
		}
		if (!isEmpty(pi.getMaritalStatus()) && !lookupModelListContainsCode(pi.getMaritalStatus(), ReferenceDataEnum.MARITALSTATUS)) {
			if (cleanup) {
				LOGGER.warn("Invalid Marital Status:{}", pi.getMaritalStatus());
				pi.setMaritalStatus(null);
			} else {
				throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), "Invalid Marital Status"));
			}
		}
		if (!isEmpty(pi.getSuffix()) && !lookupModelListContainsCode(pi.getSuffix(), ReferenceDataEnum.SUFFIX)) {
			if (cleanup) {
				LOGGER.warn("Invalid Suffix:{}", pi.getSuffix());
				pi.setSuffix(null);
			} else {
				throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), "Invalid Suffix"));
			}
		}

		//Fix ESODI-12208. Ignore case to compare. Set the title to the value in the drop down list
		if (!isEmpty(pi.getTitle()) ) {
      if (!lookupModelListContainsCodeIgnoreCase(pi.getTitle(), ReferenceDataEnum.TITLE)) {
        if (cleanup) {
          LOGGER.warn("Invalid Title:{}", pi.getTitle());
          pi.setTitle(null);
        } else {
          throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), "Invalid Title"));
        }
      } else {
        pi.setTitle(getDropDownValue(pi.getTitle(),ReferenceDataEnum.TITLE));
      }

		}

	}

	/**
	 *
	 * @param ci ContactInfo
	 * @param cleanup boolean, true: clean up invalid data only; false: throw ApiException
	 * @throws ApiException
	 */
	public void validateDataAgainstDropdown(ContactInfo ci, boolean cleanup) throws ApiException {
		validateDataAgainstDropdown(ci.getLegalAddress(), cleanup);
		validateDataAgainstDropdown(ci.getMailingAddress(), cleanup);
	}

	/**
	 *
	 * @param ei EmploymentInfo
	 * @param cleanup boolean, true: clean up invalid data only; false: throw ApiException
	 * @throws ApiException
	 */
	public void validateDataAgainstDropdown(EmploymentInfo ei, boolean cleanup) throws ApiException {
	  if(ei.getStatusCd() != null) {
      if (!isEmpty(ei.getStatusCd()) && !lookupModelListContainsCode(ei.getStatusCd(), ReferenceDataEnum.EMPLOYMENTSTATUS)) {
        if (cleanup) {
          LOGGER.warn("Invalid Employment Status:{}", ei.getStatusCd());
          ei.setStatusCd(null);
        } else {
          throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), "Invalid Employment Status"));
        }
      }
    }
    if (ei.getIndustry()!= null) {
      if (!isEmpty(ei.getIndustry()) && !lookupModelListContainsCode(ei.getIndustry(), ReferenceDataEnum.INDUSTRY)) {
        if (cleanup) {
          LOGGER.warn("Invalid Industry:{}", ei.getIndustry());
          ei.setIndustry(null);
        } else {
          throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), "Invalid Industry"));
        }
      }
    }
    if(ei.getOccupation() != null) {
      if (!isEmpty(ei.getOccupation()) && !lookupModelListContainsCode(getOccupationList(ei.getIndustry()), ei.getOccupation())) {
        if (cleanup) {
          LOGGER.warn("Invalid Occupation:{}", ei.getOccupation());
          ei.setOccupation(null);
        } else {
          throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), "Invalid Occupation"));
        }
      }
    }
		validateDataAgainstDropdown(ei.getAddress(), cleanup);
	}

	/**
	 *
	 * @param address Address
	 * @param cleanup boolean, true: clean up invalid data only; false: throw ApiException
	 * @throws ApiException
	 */
	public void validateDataAgainstDropdown(Address address, boolean cleanup) throws ApiException {
    if (address == null) {
      return;
    }
    if (address.getCountry() != null) {
      if (!isEmpty(address.getCountry()) && !lookupModelListContainsCode(address.getCountry(), ReferenceDataEnum.COUNTRY)) {
        if (cleanup) {
          LOGGER.warn("Invalid Country:{}", address.getCountry());
          address.setCountry(null);
        } else {
          throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), "Invalid Country"));
        }
      }

      List<? extends LookupModel> provStateList = getProvinceStateList(address.getCountry());
      if (address.getProvince() != null) {
        if (!isEmpty(address.getProvince()) && !provStateList.isEmpty() && !lookupModelListContainsCode(provStateList, address.getProvince())) {
          if (cleanup) {
            LOGGER.warn("Invalid Province or State:{}", address.getProvince());
            address.setProvince(null);
          } else {
            throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), "Invalid Province or State"));
          }
        }
      }
    }
    if (address.getUnitType() != null) {
      if (!isEmpty(address.getUnitType()) && !lookupModelListContainsCode(address.getUnitType(), ReferenceDataEnum.UNITTYPES)) {
        if (cleanup) {
          LOGGER.warn("Invalid Unit Type:{}", address.getUnitType());
          address.setUnitType(null);
        } else {
          throw new ApiException(ExceptionUtil.buildErrorStatus(HttpStatus.BAD_REQUEST.value(), "Invalid Unit Type"));
        }
      }
    }
  }

	public boolean lookupModelListContainsCode(String code, ReferenceDataEnum referenceDataEnum) throws ApiException {
		return lookupModelListContainsCode(referenceDataHelper.getReferenceData(referenceDataEnum), code);
	}

  public boolean lookupModelListContainsCodeIgnoreCase(String code, ReferenceDataEnum referenceDataEnum) throws ApiException {
    return lookupModelListContainsCodeIgnoreCase(referenceDataHelper.getReferenceData(referenceDataEnum), code);
  }

  public String getDropDownValue(String code, ReferenceDataEnum referenceDataEnum) {
    return getDropDownValue(referenceDataHelper.getReferenceData(referenceDataEnum), code);
  }

	public boolean lookupModelListContainsCode(List<? extends LookupModel> lookupModelList, String code) {
		boolean result = false;
		for (LookupModel model : lookupModelList) {
			if (model.getCode().equals(code)) {
				result = true;
				break;
			}
		}
		return result;
	}

  public boolean lookupModelListContainsCodeIgnoreCase(List<? extends LookupModel> lookupModelList, String code) {
    boolean result = false;
    for (LookupModel model : lookupModelList) {
      if (model.getCode().equalsIgnoreCase(code)) {
        result = true;
        break;
      }
    }
    return result;
  }

  public String getDropDownValue(List<? extends LookupModel> lookupModelList, String code) {
    String result = null;
    for (LookupModel model : lookupModelList) {
      if (model.getCode().equalsIgnoreCase(code)) {
        result = model.getCode();
        break;
      }
    }
    return result;
  }

	protected boolean isEmpty(String value) {
		return value == null || value.trim().isEmpty();
	}

  public List<?extends LookupModel> getProvinceStateList(String countryCode) throws ApiException {

    List<? extends LookupModel> lookupList = null;

    if(!isEmpty(countryCode)) {
      List<? extends LookupModel> canadianProvinces = referenceDataHelper.getReferenceData(ReferenceDataEnum.CANADIANPROVINCES);
      List<? extends LookupModel> usStates = referenceDataHelper.getReferenceData(ReferenceDataEnum.USASTATES);
      lookupList = ListUtils.union(canadianProvinces, usStates);
    }
    if(lookupList == null) {
      lookupList = new ArrayList<>();
    }

		return lookupList;
	}

	public List<? extends LookupModel> getOccupationList(String industryId) throws ApiException {

    List<? extends LookupModel> lookupList = null;


		if (!isEmpty(industryId)) {
			lookupList = referenceDataHelper.getReferenceData(ReferenceDataEnum.OCCUPATIONS);
		} else {
			LOGGER.warn("User is " + "missing an industry id");
		}

		if (lookupList == null) {
			lookupList = new ArrayList<>();
		}

		return lookupList;
	}

}
