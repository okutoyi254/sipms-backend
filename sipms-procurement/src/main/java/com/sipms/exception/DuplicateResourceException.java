package com.sipms.exception;

import lombok.Getter;

@Getter
public class DuplicateResourceException extends ProcurementException{

    private final String resourceType;
    private final String duplicateField;
    private final Object duplicateValue;

    public DuplicateResourceException(String message) {
        super(message, "DUPLICATE_RESOURCE");
        this.resourceType = null;
        this.duplicateField = null;
        this.duplicateValue = null;
    }

    public DuplicateResourceException(String resourceType, String duplicateField, Object duplicateValue) {
        super(String.format("%s with %s '%s' already exists", resourceType, duplicateField, duplicateValue), "DUPLICATE_RESOURCE");
        this.resourceType = resourceType;
        this.duplicateField = duplicateField;
        this.duplicateValue = duplicateValue;
    }
}
