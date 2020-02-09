package com.td.dcts.eso.experience.config.disclosure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Products",
    "id"
})
public class ClientType {

    @JsonProperty("Products")
    private Products products;
    @JsonProperty("id")
    private String id;

    @JsonProperty("Products")
    public Products getProducts() {
        return products;
    }

    @JsonProperty("Products")
    public void setProducts(Products products) {
        this.products = products;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toStringExclude(this, new String[] {""});
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(products).append(id).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ClientType) == false) {
            return false;
        }
        ClientType rhs = ((ClientType) other);
        return new EqualsBuilder().append(products, rhs.products).append(id, rhs.id).isEquals();
    }

}
