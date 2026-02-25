package com.sipms.dto.purchaseorderdtos;

import com.sipms.model.InventoryPurchaseOrderItem;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;

import lombok.Data;
 import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class CreatePurchaseOrderRequest {

        private Long id;

        @NotNull(message = "Vendor ID is required")
        private Long SupplierId;

        private Long prId;

        private String productCode;

        @NotNull(message = "Expected delivery date is required")
        @Future(message = "Expected delivery date must be in the future")
        private LocalDate expectedDeliveryDate;

        @NotBlank(message = "Delivery address is required")
        @Size(max = 500, message = "Delivery address must not exceed 500 characters")
        private String deliveryDestination;

        @NotBlank(message = "Payment terms are required")
        @Size(max = 200, message = "Payment terms must not exceed 200 characters")
        private String paymentTerms;

        @NotEmpty(message = "At least one item is required")
        @Size(min = 1, max = 100, message = "Number of items must be between 1 and 100")
        private List<CreatePurchaseOrderItemRequest> items;


        @NotNull(message = "Created by is required")
        private Long createdBy;
    }


