package com.sipms.exception;

import lombok.Getter;

@Getter
public class BusinessRuleException extends ProcurementException{

    private final String ruleName;

    public BusinessRuleException(String message) {
        super(message, "BUSINESS_RULE_VIOLATION");
        this.ruleName = null;
    }

    public BusinessRuleException(String message, String ruleName) {
        super(message, "BUSINESS_RULE_VIOLATION");
        this.ruleName = ruleName;
    }

}
