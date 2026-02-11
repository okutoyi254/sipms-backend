package com.sipms.logistics.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RejectionRequest {

    @NotBlank(message = "Rejected by is required")
    @Size(max = 100, message = "Rejected by must not exceed 100 characters")
    private String rejectedBy;

    @NotBlank(message = "Rejection reason is required")
    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;
}
