package com.sipms.inventory.model;

public enum BatchStatus {
    ACTIVE("Active - available for use"),
    EXPIRED("Expired - past expiry date"),
    RECALLED("Recalled - supplier recall"),
    DAMAGED("Damaged - not suitable for sale"),
    DEPLETED("Depleted - all stock consumed"),
    BLOCKED("Blocked - administrative hold");

    private final String description;

    BatchStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}