package com.sipms.exception;

import lombok.Getter;

@Getter
public class InvalidOperationException extends ProcurementException {

    private final String operation;
    private final String currentState;

    public InvalidOperationException(String message) {
        super(message, "INVALID_OPERATION");
        this.operation = null;
        this.currentState = null;
    }

    public InvalidOperationException(String message, String operation, String currentState) {
        super(message, "INVALID_OPERATION");
        this.operation = operation;
        this.currentState = currentState;
    }
}
