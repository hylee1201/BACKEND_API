/*
 * BaseObject.java
 */
package com.td.dcts.eso.experience.model.request.disclosure;

import java.io.Serializable;

/**
 * Base class for Model objects. Child objects should implement toString(),
 * equals() and hashCode().
 *
 * @author Sapient
 */
public abstract class BaseObject implements Serializable {

    /** Generated SerialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Compares object equality. When using Hibernate, the primary key should
     * not be a part of this comparison.
     *
     * @param o
     *            object to compare to
     * @return true/false based on equality tests
     */
    @Override
    public abstract boolean equals(Object o);

    /**
     * When you override equals, you should override hashCode. See "Why are
     * equals() and hashCode() importation" for more information:
     * http://www.hibernate.org/109.html
     *
     * @return hashCode
     */
    @Override
    public abstract int hashCode();

    /**
     * Returns a multi-line String with key=value pairs.
     *
     * @return a String representation of this class.
     */
    @Override
    public abstract String toString();
}
