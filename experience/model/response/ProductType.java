package com.td.dcts.eso.experience.model.response;

public class ProductType {
	
	private String productTypeId;
	private String productCategoryId;
	private String productSubcategoryId;
	private String clob;
	
	public String getProductTypeId() {
		return productTypeId;
	}
	
	public void setProductTypeId(String productTypeId) {
		this.productTypeId = productTypeId;
	}
	
	public String getClob() {
		return clob;
	}
	
	public void setClob(String clob) {
		this.clob = clob;
	}
	
	public String getProductCategoryId() {
		return productCategoryId;
	}
	
	public void setProductCategoryId(String productCategoryId) {
		this.productCategoryId = productCategoryId;
	}
	
	public String getProductSubcategoryId() {
		return productSubcategoryId;
	}
	
	public void setProductSubcategoryId(String productSubcategoryId) {
		this.productSubcategoryId = productSubcategoryId;
	}

}
