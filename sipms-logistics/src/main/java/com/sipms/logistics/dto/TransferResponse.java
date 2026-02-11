package com.sipms.logistics.dto;

import com.sipms.logistics.entity.StockTransferRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferResponse {
    private boolean success;
    private StockTransferRequest transfer;
    private String message;
}
