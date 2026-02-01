package com.sipms.logistics.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// Request DTOs
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManualTransferRequest {

    @NotNull(message = "Source branch ID is required")
    private Long sourceBranchId;

    @NotNull(message = "Destination branch ID is required")
    private Long destinationBranchId;

    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<TransferItemRequest> items;

    @NotBlank(message = "Requested by is required")
    @Size(max = 100, message = "Requested by must not exceed 100 characters")
    private String requestedBy;
}
