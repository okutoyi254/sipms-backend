package com.sipms.exception;

import lombok.Getter;

@Getter
public class ProcurementException extends RuntimeException{

    private final String errorCode;

    public ProcurementException(String message) {
        super(message);
        this.errorCode = "PROCUREMENT_ERROR";
    }

    public ProcurementException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ProcurementException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "PROCUREMENT_ERROR";
    }

    public ProcurementException(String message, Throwable cause, String errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }


}
