package com.td.dcts.eso.event.response.model;

import java.io.Serializable;

public class SubApplicationInfo implements Serializable {
  private static final long serialVersionUID = 8804168623800995358L;


	private String subApplicationId;
	private String productId;

	public String getSubApplicationId() {
		return subApplicationId;
	}

	public void setSubApplicationId(String subApplicationId) {
		this.subApplicationId = subApplicationId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}
}
