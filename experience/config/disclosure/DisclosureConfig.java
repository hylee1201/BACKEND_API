package com.td.dcts.eso.experience.config.disclosure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "ClientTypes"
})
public class DisclosureConfig {

    @JsonProperty("ClientTypes")
    private ClientTypes clientTypes;

    @JsonProperty("ClientTypes")
    public ClientTypes getClientTypes() {
        return clientTypes;
    }

    @JsonProperty("ClientTypes")
    public void setClientTypes(ClientTypes clientTypes) {
        this.clientTypes = clientTypes;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toStringExclude(this, new String[] {""});
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(clientTypes).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof DisclosureConfig) == false) {
            return false;
        }
        DisclosureConfig rhs = ((DisclosureConfig) other);
        return new EqualsBuilder().append(clientTypes, rhs.clientTypes).isEquals();
    }

}
