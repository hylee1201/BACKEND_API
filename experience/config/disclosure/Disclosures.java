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
    "Disclosure"
})
public class Disclosures {

    @JsonProperty("Disclosure")
    private List<Disclosure> disclosure = new ArrayList<Disclosure>();

    @JsonProperty("Disclosure")
    public List<Disclosure> getDisclosure() {
        return disclosure;
    }

    @JsonProperty("Disclosure")
    public void setDisclosure(List<Disclosure> disclosure) {
        this.disclosure = disclosure;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toStringExclude(this, new String[] {""});
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(disclosure).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Disclosures) == false) {
            return false;
        }
        Disclosures rhs = ((Disclosures) other);
        return new EqualsBuilder().append(disclosure, rhs.disclosure).isEquals();
    }

}
