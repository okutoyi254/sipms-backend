package com.sipms.exception;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationException extends ProcurementException{

     private final List<String> validationErrors;

    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
        this.validationErrors = new ArrayList<>();
    }

    public ValidationException(String message,List<String>validationErrors) {
        super(message,"VALIDATION_ERROR");
        this.validationErrors = validationErrors !=null ? validationErrors: new ArrayList<>();
    }

    public ValidationException(String message,String errorCode) {
        super(message, errorCode);
        this.validationErrors = new ArrayList<>();
    }


    }

