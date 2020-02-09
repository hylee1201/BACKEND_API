package com.td.dcts.eso.experience.model.response;

import java.io.Serializable;

public abstract class BaseResponseContent implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 6130258813241570386L;

    abstract public Status getStatus();

    abstract public void setStatus(Status status);

}
