package com.sipms.exception;

import lombok.Getter;

@Getter
public class DataIntegrityException extends ProcurementException{

    private final String constraintName;

    public DataIntegrityException(String message) {
        super(message, "DATA_INTEGRITY_VIOLATION");
        this.constraintName = null;
    }

    public DataIntegrityException(String message, String constraintName) {
        super(message, "DATA_INTEGRITY_VIOLATION");
        this.constraintName = constraintName;
    }

    public DataIntegrityException(String message, Throwable cause) {
        super(message, cause, "DATA_INTEGRITY_VIOLATION");
        this.constraintName = null;
    }
}
