package com.sipms.dto.purchaserequisitiondtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequisitionDTO {

    private Long id;

    @NotBlank(message = "Requisition number is required")
    private String requisitionNumber;

    @NotNull(message = "Requester id is required")
    private Long requesterId;



}
