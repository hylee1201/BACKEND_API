package com.td.dcts.eso.experience.config.disclosure;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Product"
})
public class Products {

    @JsonProperty("Product")
    private List<Product> product = new ArrayList<Product>();

    @JsonProperty("Product")
    public List<Product> getProduct() {
        return product;
    }

    @JsonProperty("Product")
    public void setProduct(List<Product> product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toStringExclude(this, new String[] {""});
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(product).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Products) == false) {
            return false;
        }
        Products rhs = ((Products) other);
        return new EqualsBuilder().append(product, rhs.product).isEquals();
    }

}
