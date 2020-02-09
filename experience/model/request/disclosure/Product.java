package com.td.dcts.eso.experience.model.request.disclosure;



import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * The Class Product.
 *
 */

@XmlRootElement(name = "Product")
@XmlAccessorType(XmlAccessType.FIELD)
public class Product extends BaseEntity {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8749800657508431645L;

    /** The product id. */
    private String pegaProductID;

    /** The infosite product id. */
    @XmlElement(name = "InfositeProductID")
    private String infositeProductId;

    /** The product name. */
    @XmlElement(name = "ProductName")
    private String productName;

    /** The product group. */
    @XmlElement(name = "ProductGroup")
    private String productGroup;

    /** The product group label. */
    private String productGroupLabel;

    /** The product category label. */
    private String productCategoryLabel;

    /** The product full name. */
    private String productFullName;

    /** The product category. */
    @XmlElement(name = "ProductCategory")
    private String productCategory;

    /** The product group. */
    @XmlElement(name = "ProductGroupRemote")
    private String productGroupRemote;

    /** The product category. */
    @XmlElement(name = "ProductCategoryRemote")
    private String productCategoryRemote;
    /** The aeroplan electra number. */
    private String electraMembershipNumber;

    /** The interest rates pega. */
    private List<InterestRates> interestRatesDetailed;

    /** The min credit limit. */
    private String minCreditLimit;

    /** The max credit limit. */
    private String maxCreditLimit;

    /** The account number. */
    private String accountNumber;

    /** The cross sell flag. */
    private boolean crossSellFlag;

    /** The status. */
    private String status;

    /** The min gross annual income. */
    private String minGrossAnnualIncome;

    /** The min total house hold income. */
    private String minTotalHouseHoldIncome;

    /** The Amount. */
    private String amount;

    /** Interest Rate Type. */
    private String interestRateType;

    /** Repayment Period. */
    private String repaymentPeriod;

    /** Amortization */
    private String amortization;

    /** The spotlight start. */
    @XmlElement(name = "SpotlightStart")
    private String spotlightStart;

    /** The spotlight end. */
    @XmlElement(name = "SpotlightEnd")
    private String spotlightEnd;

    /** The spotlight start. */
    @XmlElement(name = "SearchStart")
    private String searchStart;

    /** The spotlight end. */
    @XmlElement(name = "SearchEnd")
    private String searchEnd;

    /**
     * Gets the account number.
     *
     * @return the accountNumber
     */
    public String getAccountNumber() {
        return accountNumber;
    }

	/**
	 * @return the amortization
	 */
	public String getAmortization() {
		return amortization;
	}

	/**
	 * Gets the Amount.
	 *
	 * @return the amount
	 */

    public String getAmount() {
        return amount;
    }

    /**
     * Gets the electra membership number.
     *
     * @return the electraMembershipNumber
     */
    public String getElectraMembershipNumber() {
        return electraMembershipNumber;
    }

    /**
     * Gets the infosite product id.
     *
     * @return the infosite product id
     */
    public String getInfositeProductId() {
        return infositeProductId;
    }

    /**
     * Gets the interest rates pega.
     *
     * @return the interest rates pega
     */
    public List<InterestRates> getInterestRatesDetailed() {
        return interestRatesDetailed;
    }

    public String getInterestRateType() {
        return interestRateType;
    }

    /**
     * Gets the max credit limit.
     *
     * @return the max credit limit
     */
    public String getMaxCreditLimit() {
        return maxCreditLimit;
    }

    /**
     * Gets the min credit limit.
     *
     * @return the min credit limit
     */
    public String getMinCreditLimit() {
        return minCreditLimit;
    }

    /**
     * Gets the min gross annual income.
     *
     * @return the min gross annual income
     */
    public String getMinGrossAnnualIncome() {
        return minGrossAnnualIncome;
    }

    /**
     * Gets the min total house hold income.
     *
     * @return the min total house hold income
     */
    public String getMinTotalHouseHoldIncome() {
        return minTotalHouseHoldIncome;
    }

    /**
     * Gets the product id.
     *
     * @return the product id
     */
    public String getPegaProductID() {
        return pegaProductID;
    }

    /**
     * Gets the product category.
     *
     * @return the product category
     */
    public String getProductCategory() {
        return productCategory;
    }

    /**
     * Gets the product category label.
     *
     * @return the productCategoryLabel
     */
    public String getProductCategoryLabel() {
        return productCategoryLabel;
    }

	/**
	 * @return the productCategoryRemote
	 */
	public String getProductCategoryRemote() {
		return productCategoryRemote;
	}

    /**
     * Gets the product full name. Don't take product's Full Name from here
     * unless its a shopping cart object
     *
     * @return the productFullName
     */
    public String getProductFullName() {
        return productFullName == null ? getProductName() : productFullName;
    }

    /**
     * Gets the product group.
     *
     * @return the product group
     */
    public String getProductGroup() {
        return productGroup;
    }

    /**
     * Gets the product group label.
     *
     * @return the productGroupLabel
     */
    public String getProductGroupLabel() {
        return productGroupLabel;
    }

	/**
	 * @return the productGroupRemote
	 */
	public String getProductGroupRemote() {
		return productGroupRemote;
	}

	/**
	 * Gets the product name.
	 *
	 * @return the productName
	 */
	public String getProductName() {
		return productName;
	}

	/**
	 * @return the repaymentPeriod
	 */
	public String getRepaymentPeriod() {
		return repaymentPeriod;
	}

    /**
     * @return the searchEnd
     */
    public String getSearchEnd() {
        return searchEnd;
    }

    /**
     * @return the searchStart
     */
    public String getSearchStart() {
        return searchStart;
    }

    /**
     * Gets the spotlight end.
     *
     * @return the spotlight end
     */
    public String getSpotlightEnd() {
        return spotlightEnd;
    }

    /**
     * Gets the spotlight start.
     *
     * @return the spotlight start
     */
    public String getSpotlightStart() {
        return spotlightStart;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Checks if is cross sell flag.
     *
     * @return true, if is cross sell flag
     */
    public boolean isCrossSellFlag() {
        return crossSellFlag;
    }

    /**
     * Sets the account number.
     *
     * @param accountNumber
     *            the accountNumber to set
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

	/**
	 * @param amortization
	 *            the amortization to set
	 */
	public void setAmortization(String amortization) {
		this.amortization = amortization;
	}

	/**
	 * Sets the amount.
	 *
	 * @param status
	 *            the new amount
	 */

    public void setAmount(String amount) {
        this.amount = amount;
    }

    /**
     * Sets the cross sell flag.
     *
     * @param crossSellFlag
     *            the new cross sell flag
     */
    public void setCrossSellFlag(boolean crossSellFlag) {
        this.crossSellFlag = crossSellFlag;
    }

    /**
     * Sets the electra membership number.
     *
     * @param electraMembershipNumber
     *            the electraMembershipNumber to set
     */
    public void setElectraMembershipNumber(String electraMembershipNumber) {
        this.electraMembershipNumber = electraMembershipNumber;
    }

    /**
     * Sets the infosite product id.
     *
     * @param infositeProductId
     *            the new infosite product id
     */
    public void setInfositeProductId(String infositeProductId) {
        this.infositeProductId = infositeProductId;
    }

    /**
     * Sets the interest rates detailed.
     *
     * @param interestRatesDetailed
     *            the new interest rates detailed
     */
    public void setInterestRatesDetailed(List<InterestRates> interestRatesDetailed) {
        this.interestRatesDetailed = interestRatesDetailed;
    }

    public void setInterestRateType(String interestRateType) {
        this.interestRateType = interestRateType;
    }

    /**
     * Sets the max credit limit.
     *
     * @param maxCreditLimit
     *            the new max credit limit
     */
    public void setMaxCreditLimit(String maxCreditLimit) {
        if (null != maxCreditLimit) {
            this.maxCreditLimit = maxCreditLimit.replaceAll(",", "");
        } else {
            this.maxCreditLimit = maxCreditLimit;
        }
    }

    /**
     * Sets the min credit limit.
     *
     * @param minCreditLimit
     *            the new min credit limit
     */
    public void setMinCreditLimit(String minCreditLimit) {
        if (null != minCreditLimit) {
            this.minCreditLimit = minCreditLimit.replaceAll(",", "");
        } else {
            this.minCreditLimit = minCreditLimit;
        }
    }

    /**
     * Sets the min gross annual income.
     *
     * @param minGrossAnnualIncome
     *            the new min gross annual income
     */
    public void setMinGrossAnnualIncome(String minGrossAnnualIncome) {
        this.minGrossAnnualIncome = minGrossAnnualIncome;
    }

    /**
     * Sets the min total house hold income.
     *
     * @param minTotalHouseHoldIncome
     *            the new min total house hold income
     */
    public void setMinTotalHouseHoldIncome(String minTotalHouseHoldIncome) {
        this.minTotalHouseHoldIncome = minTotalHouseHoldIncome;
    }

    /**
     * Sets the product id.
     *
     * @param productID
     *            the new product id
     */
    public void setPegaProductID(String productID) {
        pegaProductID = productID;
    }

    /**
     * Sets the product category.
     *
     * @param productCategory
     *            the new product category
     */
    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    /**
     * Sets the product category label.
     *
     * @param productCategoryLabel
     *            the productCategoryLabel to set
     */
    public void setProductCategoryLabel(String productCategoryLabel) {
        this.productCategoryLabel = productCategoryLabel;
    }

	/**
	 * @param productCategoryRemote
	 *            the productCategoryRemote to set
	 */
	public void setProductCategoryRemote(String productCategoryRemote) {
		this.productCategoryRemote = productCategoryRemote;
	}

    /**
     * Sets the product full name.
     *
     * @param productFullName
     *            the productFullName to set
     */
    public void setProductFullName(String productFullName) {
        this.productFullName = productFullName;
    }

    /**
     * Sets the product group.
     *
     * @param productGroup
     *            the new product group
     */
    public void setProductGroup(String productGroup) {
        this.productGroup = productGroup;
    }

    /**
     * Sets the product group label.
     *
     * @param productGroupLabel
     *            the productGroupLabel to set
     */
    public void setProductGroupLabel(String productGroupLabel) {
        this.productGroupLabel = productGroupLabel;
    }

	/**
	 * @param productGroupRemote
	 *            the productGroupRemote to set
	 */
	public void setProductGroupRemote(String productGroupRemote) {
		this.productGroupRemote = productGroupRemote;
	}

    /**
     * Sets the product name.
     *
     * @param productName
     *            the productName to set
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

	/**
	 * @param repaymentPeriod
	 *            the repaymentPeriod to set
	 */
	public void setRepaymentPeriod(String repaymentPeriod) {
		this.repaymentPeriod = repaymentPeriod;
	}

    /**
     * @param searchEnd
     *            the searchEnd to set
     */
    public void setSearchEnd(String searchEnd) {
        this.searchEnd = searchEnd;
    }

    /**
     * @param searchStart
     *            the searchStart to set
     */
    public void setSearchStart(String searchStart) {
        this.searchStart = searchStart;
    }

    /**
     * Sets the spotlight end.
     *
     * @param spotlightEnd
     *            the new spotlight end
     */
    public void setSpotlightEnd(String spotlightEnd) {
        this.spotlightEnd = spotlightEnd;
    }

    /**
     * Sets the spotlight start.
     *
     * @param spotlightStart
     *            the new spotlight start
     */
    public void setSpotlightStart(String spotlightStart) {
        this.spotlightStart = spotlightStart;
    }

    /**
     * Sets the status.
     *
     * @param status
     *            the new status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * This method will provide meaningful information for Product.
     *
     * @return the modified string value.
     */
    @Override
    public String toString() {
        return "{pegaProductID:\"" + pegaProductID + "\", infositeProductId:\"" + infositeProductId + "\", productName:\"" + productName + "\", productFullName:\"" + productFullName + "\", productGroup:\"" + productGroup + "\", productGroupLabel:\"" + productGroupLabel + "\", productCategoryLabel:\"" + productCategoryLabel + "\", productCategory:\"" + productCategory + "\", electraMembershipNumber:\"" + electraMembershipNumber + "\", interestRatesDetailed:\"" + interestRatesDetailed + "\", minCreditLimit:\"" + minCreditLimit + "\", maxCreditLimit:\"" + maxCreditLimit + "\", accountNumber:\"" + accountNumber + "\", spotlightStart:\"" + spotlightStart + "\", spotlightEnd:\"" + spotlightEnd + "\", searchStart:\"" + searchStart + "\", searchEnd:\"" + searchEnd + "\"}";
    }

}
