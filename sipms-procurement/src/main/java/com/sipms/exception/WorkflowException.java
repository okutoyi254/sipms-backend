package com.sipms.exception;

import lombok.Getter;

@Getter
public class WorkflowException extends ProcurementException{

    private final String workflowType;
    private final String currentStep;

    public WorkflowException(String message) {
        super(message, "WORKFLOW_ERROR");
        this.workflowType = null;
        this.currentStep = null;
    }

    public WorkflowException(String message, String workflowType, String currentStep) {
        super(message, "WORKFLOW_ERROR");
        this.workflowType = workflowType;
        this.currentStep = currentStep;
    }
}
