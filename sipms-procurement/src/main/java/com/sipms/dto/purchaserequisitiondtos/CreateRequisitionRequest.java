package com.sipms.dto.purchaserequisitiondtos;

import com.sipms.enums.Priority;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateRequisitionRequest {

    @NotNull(message = "Requester id is required")
    private Integer requesterId;

    @NotNull(message = "Department ID is required")
    private Integer departmentId;

    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Size(max = 2000, message = "Justification must not exceed 2000 characters")
    private String justification;


    @NotNull(message = "Priority is required")
    private Priority priority;

    @NotEmpty(message = "At least one item is required")
    @Size(min = 1, max = 100, message = "Number of items must be between 1 and 100")
    private List<CreateRequisitionItemRequest> items;
}
