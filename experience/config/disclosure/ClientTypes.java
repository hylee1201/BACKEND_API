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
    "ClientType"
})
public class ClientTypes {

    @JsonProperty("ClientType")
    private List<ClientType> clientType = new ArrayList<ClientType>();

    @JsonProperty("ClientType")
    public List<ClientType> getClientType() {
        return clientType;
    }

    @JsonProperty("ClientType")
    public void setClientType(List<ClientType> clientType) {
        this.clientType = clientType;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toStringExclude(this, new String[] {""});
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(clientType).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ClientTypes) == false) {
            return false;
        }
        ClientTypes rhs = ((ClientTypes) other);
        return new EqualsBuilder().append(clientType, rhs.clientType).isEquals();
    }

}
