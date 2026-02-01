package com.sipms.logistics.dto;

import com.sipms.logistics.entity.ReceiptItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiveTransferRequest {

    @NotBlank(message = "Received by is required")
    @Size(max = 100, message = "Received by must not exceed 100 characters")
    private String receivedBy;

    @NotEmpty(message = "At least one received item is required")
    @Valid
    private List<ReceiptItem> receivedItems;
}
