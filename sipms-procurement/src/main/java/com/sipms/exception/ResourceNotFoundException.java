package com.sipms.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends ProcurementException{

    private final String resourceType;
    private final Object resourceId;

    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND");
        this.resourceType = null;
        this.resourceId = null;
    }

    public ResourceNotFoundException(String resourceType, Object resourceId) {
        super(String.format("%s with ID %s not found", resourceType, resourceId), "RESOURCE_NOT_FOUND");
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    public ResourceNotFoundException(String message, String resourceType, Object resourceId) {
        super(message, "RESOURCE_NOT_FOUND");
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

}
