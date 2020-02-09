/*
 * BaseEntity.java
 */
package com.td.dcts.eso.experience.model.request.disclosure;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Base entity class that will be extended by all other classes that will be
 * mapped to a table.
 *
 * @author Sapient
 */

public abstract class BaseEntity extends BaseObject {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * this method returns true or false following the object comparison .
     *
     * @param o
     *            the o
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    /**
     * this method returns 0 or 1 based on hash code generated .
     *
     * @return int
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * this method converts the builder input to string .
     *
     * @return string
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }
}
