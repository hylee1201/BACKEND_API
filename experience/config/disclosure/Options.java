package com.td.dcts.eso.experience.config.disclosure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Disclosures"
})
public class Options {

    @JsonProperty("Disclosures")
    private Disclosures disclosures;

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
        return new HashCodeBuilder().append(disclosures).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Options) == false) {
            return false;
        }
        Options rhs = ((Options) other);
        return new EqualsBuilder().append(disclosures, rhs.disclosures).isEquals();
    }

}
