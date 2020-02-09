package com.td.dcts.eso.experience.model.request.disclosure;

import java.io.Serializable;

/**
 * The Class InterestRates.
 *
 */
public class InterestRates implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -8798432098359724216L;

    /** The rate. */
    private String rate;

    /** The range. */
    private String range;

    /**
     * Gets the range.
     *
     * @return the range
     */
    public String getRange() {
        return range;
    }

    /**
     * Gets the rate.
     *
     * @return the rate
     */
    public String getRate() {
        return rate;
    }

    /**
     * Sets the range.
     *
     * @param range
     *            the new range
     */
    public void setRange(String range) {
        this.range = range;
    }

    /**
     * Sets the rate.
     *
     * @param rate
     *            the new rate
     */
    public void setRate(String rate) {
        this.rate = rate;
    }

}
