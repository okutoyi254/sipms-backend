package com.sipms.dto.purchaseorderdtos;

import com.sipms.dto.supplierdtos.SupplierDTO;
import com.sipms.enums.POStatus;
import com.sipms.model.Supplier;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderDTO {

    private Long id;

    @NotBlank(message = "PO number is required")
    private String poNumber;

    @NotNull(message = "Vendor is required")
    private Supplier supplier;

    private Long requisitionId;

    private String requisitionNumber;

    @NotNull(message = "Order date is required")
    private LocalDateTime orderDate;

    @NotNull(message = "Expected delivery date is required")
    private LocalDate expectedDeliveryDate;

    private LocalDateTime receivedDate;

    @NotBlank(message = "Delivery address is required")
    private String deliveryDestination;

    @NotBlank(message = "Payment terms are required")
    private String paymentTerms;

    @NotNull(message = "Status is required")
    private POStatus status;

    @NotEmpty(message = "At least one item is required")
    private List<PurchaseOrderItemDTO> items;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal totalAmount;

    private BigDecimal taxAmount;

    private BigDecimal discountAmount;


    private LocalDateTime approvedDate;


    private String createdBy;

    private LocalDateTime createdDate;

}
