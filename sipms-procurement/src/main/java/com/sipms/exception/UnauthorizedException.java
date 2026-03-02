package com.sipms.exception;

import lombok.Getter;

@Getter
public class UnauthorizedException extends ProcurementException {

    private final String userId;
    private final String requiredPermission;

    public UnauthorizedException(String message) {
        super(message, "UNAUTHORIZED");
        this.userId = null;
        this.requiredPermission = null;
    }

    public UnauthorizedException(String message, String userId, String requiredPermission) {
        super(message, "UNAUTHORIZED");
        this.userId = userId;
        this.requiredPermission = requiredPermission;
    }
}
