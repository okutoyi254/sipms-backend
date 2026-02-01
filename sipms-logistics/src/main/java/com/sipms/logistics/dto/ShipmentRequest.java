package com.sipms.logistics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentRequest {

    @NotBlank(message = "Shipped by is required")
    @Size(max = 100, message = "Shipped by must not exceed 100 characters")
    private String shippedBy;

    @NotBlank(message = "Carrier is required")
    @Size(max = 100, message = "Carrier must not exceed 100 characters")
    private String carrier;

    @NotBlank(message = "Tracking number is required")
    @Size(max = 100, message = "Tracking number must not exceed 100 characters")
    private String trackingNumber;
}
