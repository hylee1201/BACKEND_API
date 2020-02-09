package com.td.dcts.eso.experience.config.disclosure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "optionsTrading",
    "shortSelling"
})
public class Product {

    @JsonProperty("id")
    private String id;

    @JsonProperty("optionsTrading")
    private Options optionsTrading;

    @JsonProperty("shortSelling")
    private Shortselling shortSelling;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("Disclosures")
    private Disclosures disclosures;

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("optionsTrading")
    public Options getOptionsTrading() {
      return optionsTrading;
    }

    @JsonProperty("optionsTrading")
    public void setOptionsTrading(Options optionsTrading) {
      this.optionsTrading = optionsTrading;
    }

    @JsonProperty("shortSelling")
    public Shortselling getShortSelling() {
      return shortSelling;
    }

    @JsonProperty("shortSelling")
    public void setShortSelling(Shortselling shortSelling) {
      this.shortSelling = shortSelling;
    }

    @JsonProperty("Disclosures")
    public Disclosures getDisclosures() {
      return disclosures;
    }

    @JsonProperty("Disclosures")
    public void setDisclosures(Disclosures disclosures) {
      this.disclosures = disclosures;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toStringExclude(this, new String[] {""});
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).append(optionsTrading).append(shortSelling).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Product) == false) {
            return false;
        }
        Product rhs = ((Product) other);
        return new EqualsBuilder().append(id, rhs.id).append(optionsTrading, rhs.optionsTrading).append(shortSelling, rhs.shortSelling).isEquals();
    }

}
