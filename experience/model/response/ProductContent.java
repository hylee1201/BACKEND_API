package com.td.dcts.eso.experience.model.response;

public class ProductContent {

  private String productId;
  private String name;
  private String description;
  private String shortName;
  private String shortDescription;
  private boolean showUSCitizenQuestion;
  private boolean showOtherCountryTax;


  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getShortName() {
    return shortName;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public String getShortDescription() {
    return shortDescription;
  }

  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }

  public boolean isShowUSCitizenQuestion() {
    return showUSCitizenQuestion;
  }

  public void setShowUSCitizenQuestion(boolean showUSCitizenQuestion) {
    this.showUSCitizenQuestion = showUSCitizenQuestion;
  }

  public boolean isShowOtherCountryTax() {
    return showOtherCountryTax;
  }

  public void setShowOtherCountryTax(boolean showOtherCountryTax) {
    this.showOtherCountryTax = showOtherCountryTax;
  }
}
