package com.huanke.iot.base.exception;

import java.io.Serializable;

public class BusinessException extends RuntimeException implements Serializable {

    /**
     * serializable
     */
    private static final long serialVersionUID = 1L;

    public BusinessException(String errMsg) {
        super(errMsg);
    }
}
