package com.sipms.logistics.dto;

import com.sipms.branch.model.Branch;
import com.sipms.logistics.entity.ReceiptItem;
import com.sipms.logistics.entity.StockTransferRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferItemRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @Size(max = 255, message = "Reason must not exceed 255 characters")
    private String reason;
}

